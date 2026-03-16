package com.bookstore.rest;

import com.bookstore.model.Customer;
import com.bookstore.service.CustomerService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Tag(name = "Customers", description = "Customer account management")
@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @Inject
    private CustomerService customerService;

    @Operation(summary = "Create a new customer")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Customer created"),
            @APIResponse(responseCode = "400", description = "Invalid input or duplicate email")
    })
    @POST
    public Response createCustomer(@Valid Customer customer) {
        Customer created = customerService.createCustomer(customer);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @Operation(summary = "List all customers", description = "Supports pagination")
    @APIResponse(responseCode = "200", description = "List of customers")
    @GET
    public Response getAllCustomers(
            @Parameter(description = "Zero-based page number") @QueryParam("page") @DefaultValue("0") int page,
            @Parameter(description = "Page size (default 20)") @QueryParam("size") @DefaultValue("20") int size) {
        List<Customer> customers = customerService.getAllCustomers(page, size);
        return Response.ok(customers).build();
    }

    @Operation(summary = "Get a customer by ID")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Customer found"),
            @APIResponse(responseCode = "404", description = "Customer not found")
    })
    @GET
    @Path("/{id}")
    public Response getCustomerById(@PathParam("id") Long id) {
        Customer customer = customerService.getCustomerById(id);
        return Response.ok(customer).build();
    }

    @Operation(summary = "Get a customer by email")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Customer found"),
            @APIResponse(responseCode = "404", description = "Customer not found")
    })
    @GET
    @Path("/email/{email}")
    public Response getCustomerByEmail(@PathParam("email") String email) {
        Customer customer = customerService.getCustomerByEmail(email);
        return Response.ok(customer).build();
    }

    @Operation(summary = "Update a customer")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Customer updated"),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "404", description = "Customer not found")
    })
    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Long id, @Valid Customer customer) {
        Customer updated = customerService.updateCustomer(id, customer);
        return Response.ok(updated).build();
    }

    @Operation(summary = "Delete a customer")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Customer deleted"),
            @APIResponse(responseCode = "404", description = "Customer not found")
    })
    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        customerService.deleteCustomer(id);
        return Response.noContent().build();
    }
}
