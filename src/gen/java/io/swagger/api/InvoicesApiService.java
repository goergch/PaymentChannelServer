package io.swagger.api;

import io.swagger.api.*;
import io.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import io.swagger.model.Error;
import io.swagger.model.Invoice;
import io.swagger.model.State;
import java.util.UUID;

import java.util.List;
import io.swagger.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-06-09T08:42:18.129Z")
public abstract class InvoicesApiService {
    public abstract Response addInvoice(Invoice invoice,SecurityContext securityContext) throws NotFoundException;
    public abstract Response deleteInvoiceById(UUID invoiceId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response getInvoiceById(UUID invoiceId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response getInvoiceState(UUID invoiceId,SecurityContext securityContext) throws NotFoundException;
    public abstract Response getInvoices(SecurityContext securityContext) throws NotFoundException;
}
