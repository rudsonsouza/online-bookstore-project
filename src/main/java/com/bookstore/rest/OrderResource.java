package com.bookstore.rest;

import com.bookstore.model.Order;
import com.bookstore.model.OrderStatus;
import com.bookstore.rest.dto.CreateOrderRequest;
import com.bookstore.rest.dto.UpdateOrderStatusRequest;
import com.bookstore.service.OrderService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
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

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    private OrderService orderService;

    @POST
    public Response createOrder(CreateOrderRequest request) {
        try {
            Order order = orderService.createOrderFromCart(
                    request.getCustomerId(),
                    request.getDeliveryAddress(),
                    request.getPaymentMethod()
            );
            return Response.status(Response.Status.CREATED).entity(order).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    public Response getAllOrders(@QueryParam("customerId") Long customerId,
                                 @QueryParam("status") String status) {
        List<Order> orders;
        if (customerId != null) {
            orders = orderService.getOrdersByCustomerId(customerId);
        } else if (status != null && !status.isEmpty()) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orders = orderService.getOrdersByStatus(orderStatus);
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid status: " + status).build();
            }
        } else {
            orders = orderService.getAllOrders();
        }
        return Response.ok(orders).build();
    }

    @GET
    @Path("/{id}")
    public Response getOrderById(@PathParam("id") Long id) {
        try {
            Order order = orderService.getOrderById(id);
            return Response.ok(order).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/status")
    public Response updateOrderStatus(@PathParam("id") Long id, UpdateOrderStatusRequest request) {
        try {
            Order order = orderService.updateOrderStatus(id, request.getStatus());
            return Response.ok(order).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
