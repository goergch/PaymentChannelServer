package io.swagger.api;

import io.swagger.model.*;
import io.swagger.api.InvoicesApiService;
import io.swagger.api.factories.InvoicesApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import io.swagger.model.Error;
import io.swagger.model.Invoice;
import io.swagger.model.State;
import java.util.UUID;

import java.util.List;
import io.swagger.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.*;
import javax.validation.constraints.*;

@Path("/invoices")
@Consumes({ "application/json" })
@Produces({ "application/json", "text/plain" })
@io.swagger.annotations.Api(description = "the invoices API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-06-09T08:42:18.129Z")
public class InvoicesApi  {
   private final InvoicesApiService delegate = InvoicesApiServiceFactory.getInvoicesApi();

    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json", "text/plain" })
    @io.swagger.annotations.ApiOperation(value = "Add one new invoice.", notes = "", response = Invoice.class, tags={  })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "id of new invoice", response = Invoice.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "bad request", response = Invoice.class),
        
        @io.swagger.annotations.ApiResponse(code = 503, message = "service unavailable", response = Invoice.class) })
    public Response addInvoice(@ApiParam(value = "one new invoice" ,required=true) Invoice invoice
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.addInvoice(invoice,securityContext);
    }
    @DELETE
    @Path("/{invoiceId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json", "text/plain" })
    @io.swagger.annotations.ApiOperation(value = "Deletes the invoice to the provided ID.", notes = "", response = void.class, tags={  })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "invoice deleted", response = void.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "invoice not found", response = void.class) })
    public Response deleteInvoiceById(@ApiParam(value = "the id of the invoice to delete",required=true) @PathParam("invoiceId") UUID invoiceId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.deleteInvoiceById(invoiceId,securityContext);
    }
    @GET
    @Path("/{invoiceId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json", "text/plain" })
    @io.swagger.annotations.ApiOperation(value = "Returns information about the invoice to the provided id.", notes = "", response = Invoice.class, tags={  })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "returns the information about the invoice", response = Invoice.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "invoice not found", response = Invoice.class) })
    public Response getInvoiceById(@ApiParam(value = "the invoice id to get the information for",required=true) @PathParam("invoiceId") UUID invoiceId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.getInvoiceById(invoiceId,securityContext);
    }
    @GET
    @Path("/{invoiceId}/state")
    @Consumes({ "application/json" })
    @Produces({ "application/json", "text/plain" })
    @io.swagger.annotations.ApiOperation(value = "Returns a confidence object that describes the state of the incoming tx.", notes = "", response = State.class, tags={  })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "returns the state object of the incoming tx", response = State.class),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "invoice not found", response = State.class) })
    public Response getInvoiceState(@ApiParam(value = "the invoice id to get the state for",required=true) @PathParam("invoiceId") UUID invoiceId
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.getInvoiceState(invoiceId,securityContext);
    }
    @GET
    
    @Consumes({ "application/json" })
    @Produces({ "application/json", "text/plain" })
    @io.swagger.annotations.ApiOperation(value = "The invoices endpoint returns a list of all known invoices ids.", notes = "", response = UUID.class, responseContainer = "List", tags={  })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "an array of invoice ids", response = UUID.class, responseContainer = "List") })
    public Response getInvoices(@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.getInvoices(securityContext);
    }
}
