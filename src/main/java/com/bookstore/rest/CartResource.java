package com.bookstore.rest;

import com.bookstore.model.Cart;
import com.bookstore.rest.dto.AddToCartRequest;
import com.bookstore.service.CartService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/carts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartResource {

    @Inject
    private CartService cartService;

    @GET
    @Path("/{customerId}")
    public Response getCart(@PathParam("customerId") Long customerId) {
        try {
            Cart cart = cartService.getCart(customerId);
            return Response.ok(cart).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{customerId}/items")
    public Response addItemToCart(@PathParam("customerId") Long customerId, AddToCartRequest request) {
        try {
            Cart cart = cartService.addItemToCart(customerId, request.getBookId(), request.getQuantity());
            return Response.ok(cart).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{customerId}/items/{bookId}")
    public Response removeItemFromCart(@PathParam("customerId") Long customerId,
                                       @PathParam("bookId") Long bookId) {
        try {
            Cart cart = cartService.removeItemFromCart(customerId, bookId);
            return Response.ok(cart).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{customerId}")
    public Response clearCart(@PathParam("customerId") Long customerId) {
        try {
            cartService.clearCart(customerId);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }
}
