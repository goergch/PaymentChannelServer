package iuno.tdm.paymentchannel.server;

import ch.qos.logback.classic.Level;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import io.swagger.model.Invoice;
import io.swagger.model.State;
import org.bitcoinj.core.*;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.protocols.channels.*;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by goergch on 09.06.17.
 */
public class PaymentChannelServer implements PaymentChannelServerListener.HandlerFactory {

    private static PaymentChannelServer instance;
    private Context context;

    private static final String PREFIX = "PaymentChannelServer";
    final static int CLEANUPINTERVAL = 20; // clean up every n minutes

    private Wallet wallet = null;
    private PeerGroup peerGroup = null;
    private static final Logger logger = LoggerFactory.getLogger(PaymentChannelServer.class);
    private DateTime lastCleanup = DateTime.now();
    private DeterministicSeed randomSeed;

    private HashMap<UUID, PaymentChannelInvoice> channelInvoices = new HashMap<>();

    private PaymentChannelServer(){
        BriefLogFormatter.initWithSilentBitcoinJ();
        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.toLevel("info"));

        // Context.enableStrictMode();
        final NetworkParameters params = TestNet3Params.get();
        context = new Context(params);
        Context.propagate(context);

        byte[] seed = new byte[DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS/8];
        List<String> mnemonic = new ArrayList<>(0);
        randomSeed = new DeterministicSeed(seed, mnemonic, MnemonicCode.BIP39_STANDARDISATION_TIME_SECS);
    }

    public static PaymentChannelServer getInstance(){
        if(instance == null){
            instance =  new PaymentChannelServer();
        }else{

            Context.propagate(instance.context);
        }
        return instance;
    }

    public void start() { // TODO: this method must be called once only!
        String workDir = System.getProperty("user.home") + "/." + PREFIX;
        new File(workDir).mkdirs();

        File chainFile = new File(workDir, PREFIX + ".spvchain");
        final File walletFile = new File(workDir, PREFIX + ".wallet");
        File backupFile = new File(System.getProperty("user.home"), PREFIX + ".wallet");
//        File backupFile = new File(workDir, PREFIX + ".backup"); // this shall be activated in the middle of april 2017 for a smooth migration from homedir to ~/.PaymentService

        // try to load regular wallet or if not existant load backup wallet or create new wallet
        // fail if an existing wallet file can not be read and admin needs to examine the wallets
        String filename = "n/a";
        try {
            if (walletFile.exists()) {
                filename = walletFile.toString();
                wallet = Wallet.loadFromFile(walletFile);

            } else {
                if (backupFile.exists()) {
                    filename = backupFile.toString();
                    wallet = Wallet.loadFromFile(backupFile);

                } else {
                    wallet = new Wallet(context);

                    wallet.addExtension(new StoredPaymentChannelServerStates(null));
                }
                chainFile.delete();
            }

        } catch (UnreadableWalletException e) {
            logger.warn(String.format("wallet file %s could not be read: %s", filename, e.getMessage()));
            e.printStackTrace();
            return;
        }


        // eventually create backup file
        try {
            if (!backupFile.exists()) wallet.saveToFile(backupFile);
        } catch (IOException e) {
            logger.error(String.format("creating backup wallet failed: %s", e.getMessage()));
            e.printStackTrace();
            return;
        }

        // wallets configuration
        if (!chainFile.exists())
            wallet.reset(); // reset wallet if chainfile does not exist
        // wallet.allowSpendingUnconfirmedTransactions();
//        wallet.addCoinsReceivedEventListener(this);
        wallet.setDescription(this.getClass().getName());
        logStatus();

        // auto save wallets at least every five seconds
        try {
            wallet.autosaveToFile(walletFile, 5, TimeUnit.SECONDS, null).saveNow();
        } catch (IOException e) {
            logger.error(String.format("creating wallet file failed: %s", e.getMessage()));
            e.printStackTrace();
            return;
        }

        // initialize blockchain file
        BlockChain blockChain;
        try {
            blockChain = new BlockChain(context, wallet, new SPVBlockStore(context.getParams(), chainFile));
        } catch (BlockStoreException e) {
            e.printStackTrace();
            return;
        }

        // initialize peer group
        peerGroup = new PeerGroup(context, blockChain);
        peerGroup.addWallet(wallet);

        peerGroup.addPeerDiscovery(new DnsDiscovery(context.getParams()));
        Futures.addCallback(peerGroup.startAsync(), new FutureCallback() {
                    @Override
                    public void onSuccess(@Nullable Object o) {
                        logger.info("peer group finished starting");
                        peerGroup.connectTo(new InetSocketAddress("tdm-payment.axoom.cloud", 18333)); // TODO make this configurable
                        peerGroup.startBlockChainDownload(new DownloadProgressTracker());


                        try {
                            new PaymentChannelServerListener(peerGroup,wallet,
                                    250, Coin.valueOf(100000),
                                    PaymentChannelServer.this)
                                    .bindAndStart(4242);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                }
        );
    }

    public void stop() {
        peerGroup.stop();
        wallet.shutdownAutosaveAndWait();
    }

    private void logStatus() {
        logger.info("Balance: " + wallet.getBalance().toFriendlyString());
        logger.info("Estimated: " + wallet.getBalance(Wallet.BalanceType.ESTIMATED).toFriendlyString());
        logger.info("Seed: " + wallet.getKeyChainSeed().getMnemonicCode());
        logger.info("wallet receive address: " + wallet.currentReceiveAddress());
    }

    public boolean isRunning() {
        return ((null != peerGroup) && (0 < peerGroup.numConnectedPeers()));
    }


    public UUID addInvoice(Invoice invoice){
        UUID invoiceId = UUID.randomUUID();
        invoice.setInvoiceId(invoiceId);
        PaymentChannelInvoice paymentChannelInvoice = new PaymentChannelInvoice(invoice);
        channelInvoices.put(invoiceId,paymentChannelInvoice);
        return invoiceId;
    }

    public void deleteInvoiceById(UUID invoiceId){
        if(!channelInvoices.containsKey(invoiceId)){
            throw new NullPointerException("No invoice with this id");
        }
        channelInvoices.remove(invoiceId);
    }


    public Invoice getInvoiceById(UUID invoiceId){
        if(!channelInvoices.containsKey(invoiceId)){
            throw new NullPointerException("No invoice with this id");
        }
        return channelInvoices.get(invoiceId).getInvoice();
    }

    public State getInvoiceState(UUID invoiceId){
        if(!channelInvoices.containsKey(invoiceId)){
            throw new NullPointerException("No invoice with this id");
        }
        return channelInvoices.get(invoiceId).getState();
    }

    public Set<UUID> getInvoiceIds(){
        return channelInvoices.keySet();
    }

    public void checkPaymentIncreaseFulfillsInvoice(Coin by, String invoiceIdString){
        try{
            UUID invoiceId = UUID.fromString(invoiceIdString);
            if(channelInvoices.containsKey(invoiceId)){
                PaymentChannelInvoice paymentChannelInvoice = channelInvoices.get(invoiceId);
                if(paymentChannelInvoice.increasePaidSum(by)){
                    logger.info("Invoice {} is now fully paid",invoiceId);
                }else{
                    logger.info("Invoice {} is now paid by {} percent",invoiceId,
                            100.0 * (float)paymentChannelInvoice.getPaidCoins().getValue() / (float)paymentChannelInvoice.getTotalAmount().getValue());
                }

            }else {
                logger.warn("Got payment for invoice we do not know: {}",invoiceId);
            }
        }catch (IllegalArgumentException e){
            logger.warn("The given invoiceIdString is no UUID: {}",invoiceIdString);
        }



    }

    @Nullable
    @Override
    public ServerConnectionEventHandler onNewConnection(final SocketAddress clientAddress) {
        return new ServerConnectionEventHandler() {
            @Override
            public void channelOpen(Sha256Hash channelId) {
                logger.info("Channel open for {}: {}.", clientAddress, channelId);

                // Try to get the state object from the stored state set in our wallet
                PaymentChannelServerState state = null;
                try {
                    StoredPaymentChannelServerStates storedStates = (StoredPaymentChannelServerStates)
                            wallet.getExtensions().get(StoredPaymentChannelServerStates.class.getName());
                    state = storedStates.getChannel(channelId).getOrCreateState(wallet, peerGroup);
                } catch (VerificationException e) {
                    // This indicates corrupted data, and since the channel was just opened, cannot happen
                    throw new RuntimeException(e);
                }
                logger.info("   with a maximum value of {}, expiring at UNIX timestamp {}.",
                        // The channel's maximum value is the value of the multisig contract which locks in some
                        // amount of money to the channel
                        state.getContract().getOutput(0).getValue(),
                        // The channel expires at some offset from when the client's refund transaction becomes
                        // spendable.
                        state.getExpiryTime() + StoredPaymentChannelServerStates.CHANNEL_EXPIRE_OFFSET);
            }

            @Override
            public ListenableFuture<ByteString> paymentIncrease(Coin by, Coin to, ByteString info) {
                if(info != null){
                    logger.info("Client {} paid increased payment by {} for a total of {}, info string is {}", clientAddress, by, to, info.toStringUtf8());
                    checkPaymentIncreaseFulfillsInvoice(by,info.toStringUtf8());
                }else{
                    logger.info("Client {} paid increased payment by {} for a total of {}", clientAddress, by, to);
                }

                return null;
            }

            @Override
            public void channelClosed(PaymentChannelCloseException.CloseReason reason) {
                logger.info("Client {} closed channel for reason {}", clientAddress, reason);
            }
        };
    }
}


