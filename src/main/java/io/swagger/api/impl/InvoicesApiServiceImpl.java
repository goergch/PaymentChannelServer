package io.swagger.api.impl;

import io.swagger.api.*;
import io.swagger.model.*;

import io.swagger.model.Error;
import io.swagger.model.Invoice;
import io.swagger.model.State;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.UUID;

import java.util.List;
import io.swagger.api.NotFoundException;

import java.io.InputStream;

import iuno.tdm.paymentchannel.server.PaymentChannelInvoice;
import iuno.tdm.paymentchannel.server.PaymentChannelServer;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-06-09T08:42:18.129Z")
public class InvoicesApiServiceImpl extends InvoicesApiService {


    private static final Logger logger = LoggerFactory.getLogger(InvoicesApiServiceImpl.class);

    @Override
    public Response addInvoice(Invoice invoice, SecurityContext securityContext) throws NotFoundException {
        Response resp;
        Error err = new Error();
        err.setMessage("success");
        UUID invoiceId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        final PaymentChannelServer pcs = PaymentChannelServer.getInstance();
        // send service unavailable (503) if bitcoin peergroup is not connected
        if (false == pcs.isRunning()) {
            err.setMessage("Peergroup is unavailable.");
            resp = Response.status(503).entity(err).build();

        } else {
            URI createdUri = null;
            try {
                invoiceId = pcs.addInvoice(invoice);
                createdUri = new URI("http://localhost:8085/v1/invoices/" + invoiceId.toString() + "/");
                resp = Response.created(createdUri).entity(invoice).build();

            } catch (IllegalArgumentException e) { // error in invoice
                err.setMessage(e.getMessage());
                resp = Response.status(400).entity(err).build();

            } catch (URISyntaxException e) { // should never happen
                e.printStackTrace();
                resp = Response.serverError().build();
            }
        }
        logger.info(String.format("%s (%03d) addInvoice: %s", invoiceId, resp.getStatus(), err.getMessage()));
        return resp;
    }
    @Override
    public Response deleteInvoiceById(UUID invoiceId, SecurityContext securityContext) throws NotFoundException {
        Response resp;
        Error err = new Error();
        err.setMessage("success");
        try {
            PaymentChannelServer.getInstance().deleteInvoiceById(invoiceId);
            resp = Response.ok().entity("invoice deleted").type(MediaType.TEXT_PLAIN_TYPE).build();

        } catch (NullPointerException e) { // likely no invoice found for provided invoiceID
            err.setMessage("no invoice found for id " + invoiceId);
            resp = Response.status(404).entity(err).build();
        }
        logger.info(String.format("%s (%03d) deleteInvoiceById: %s", invoiceId, resp.getStatus(), err.getMessage()));
        return resp;
    }
    @Override
    public Response getInvoiceById(UUID invoiceId, SecurityContext securityContext) throws NotFoundException {
        Response resp;
        Error err = new Error();
        err.setMessage("success");
        try {
            Invoice invoice = PaymentChannelServer.getInstance().getInvoiceById(invoiceId);
            resp = Response.ok().entity(invoice).build();

        } catch (NullPointerException e) { // likely no invoice found for provided invoiceID
            err.setMessage("no invoice found for id " + invoiceId);
            resp = Response.status(404).entity(err).build();
        }
        logger.info(String.format("%s (%03d) getInvoiceById: %s", invoiceId, resp.getStatus(), err.getMessage()));
        return resp;
    }
    @Override
    public Response getInvoiceState(UUID invoiceId, SecurityContext securityContext) throws NotFoundException {
        Response resp;
        Error err = new Error();
        err.setMessage("success");
        try {
            State state = PaymentChannelServer.getInstance().getInvoiceState(invoiceId);
            resp = Response.ok().entity(state).build();

        } catch (NullPointerException e) { // likely no invoice found for provided invoiceID
            err.setMessage("no invoice found for id " + invoiceId);
            resp = Response.status(404).entity(err).build();
        }
        logger.info(String.format("%s (%03d) getInvoiceState: %s", invoiceId, resp.getStatus(), err.getMessage()));
        return resp;
    }
    @Override
    public Response getInvoices(SecurityContext securityContext) throws NotFoundException {
        Set<UUID> invoiceIds = PaymentChannelServer.getInstance().getInvoiceIds();
        logger.info(String.format("00000000-0000-0000-0000-000000000000 (200) getInvoices"));
        return Response.ok().entity(invoiceIds).build();
    }
}
