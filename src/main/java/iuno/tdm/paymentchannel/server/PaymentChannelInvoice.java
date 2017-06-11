package iuno.tdm.paymentchannel.server;

import io.swagger.model.Invoice;
import io.swagger.model.State;
import org.bitcoinj.core.Coin;

/**
 * Created by goergch on 11.06.17.
 */
public class PaymentChannelInvoice {

    private Invoice invoice;

    private State state;

    private Coin paidCoins = Coin.ZERO;

    public PaymentChannelInvoice(Invoice invoice){
        this.state = new State();
        state.setState(State.StateEnum.UNPAID);
        this.invoice = invoice;
    }

    public Invoice getInvoice(){
        return invoice;
    }

    public State getState(){
        return state;
    }

    public boolean increasePaidSum(Coin by){
        paidCoins = paidCoins.add(by);
        if(paidCoins.compareTo(Coin.valueOf(invoice.getTotalAmount()))>=0){
            state.setState(State.StateEnum.PAID);
            return true;
        }
        return false;
    }
    public Coin getPaidCoins(){
        return paidCoins;
    }

    public Coin getTotalAmount(){
        return  Coin.valueOf(invoice.getTotalAmount());
    }


}
