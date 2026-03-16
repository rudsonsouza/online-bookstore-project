package com.bookstore.rest;

import com.bookstore.model.Cart;
import com.bookstore.rest.dto.AddToCartRequest;
import com.bookstore.service.CartService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Tag(name = "Cart", description = "Shopping cart operations (stored in Redis)")
@Path("/carts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartResource {

    @Inject
    private CartService cartService;

    @Operation(summary = "Get the cart for a customer")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Cart contents"),
            @APIResponse(responseCode = "500", description = "Redis unavailable")
    })
    @GET
    @Path("/{customerId}")
    public Response getCart(@PathParam("customerId") Long customerId) {
        Cart cart = cartService.getCart(customerId);
        return Response.ok(cart).build();
    }

    @Operation(summary = "Add an item to the cart")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Updated cart"),
            @APIResponse(responseCode = "400", description = "Invalid input")
    })
    @POST
    @Path("/{customerId}/items")
    public Response addItemToCart(@PathParam("customerId") Long customerId, @Valid AddToCartRequest request) {
        Cart cart = cartService.addItemToCart(customerId, request.getBookId(), request.getQuantity());
        return Response.ok(cart).build();
    }

    @Operation(summary = "Remove an item from the cart")
    @APIResponse(responseCode = "200", description = "Updated cart")
    @DELETE
    @Path("/{customerId}/items/{bookId}")
    public Response removeItemFromCart(@PathParam("customerId") Long customerId,
                                       @PathParam("bookId") Long bookId) {
        Cart cart = cartService.removeItemFromCart(customerId, bookId);
        return Response.ok(cart).build();
    }

    @Operation(summary = "Clear the entire cart")
    @APIResponse(responseCode = "204", description = "Cart cleared")
    @DELETE
    @Path("/{customerId}")
    public Response clearCart(@PathParam("customerId") Long customerId) {
        cartService.clearCart(customerId);
        return Response.noContent().build();
    }
}
