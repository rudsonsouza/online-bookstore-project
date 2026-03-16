package com.bookstore.rest;

import com.bookstore.model.Order;
import com.bookstore.model.OrderStatus;
import com.bookstore.rest.dto.CreateOrderRequest;
import com.bookstore.rest.dto.UpdateOrderStatusRequest;
import com.bookstore.service.OrderService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
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

@Tag(name = "Orders", description = "Order lifecycle management")
@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    private OrderService orderService;

    @Operation(summary = "Create an order from the customer's cart")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Order created"),
            @APIResponse(responseCode = "400", description = "Invalid input, empty cart, or insufficient stock")
    })
    @POST
    public Response createOrder(@Valid CreateOrderRequest request) {
        Order order = orderService.createOrderFromCart(
                request.getCustomerId(),
                request.getDeliveryAddress(),
                request.getPaymentMethod()
        );
        return Response.status(Response.Status.CREATED).entity(order).build();
    }

    @Operation(summary = "List orders", description = "Filter by customerId or status. Supports pagination.")
    @APIResponse(responseCode = "200", description = "List of orders")
    @GET
    public Response getAllOrders(
            @Parameter(description = "Filter by customer ID") @QueryParam("customerId") Long customerId,
            @Parameter(description = "Filter by status (e.g. PENDING, CONFIRMED, SHIPPED)")
            @QueryParam("status") String status,
            @Parameter(description = "Zero-based page number") @QueryParam("page") @DefaultValue("0") int page,
            @Parameter(description = "Page size (default 20)") @QueryParam("size") @DefaultValue("20") int size) {
        List<Order> orders;
        if (customerId != null) {
            orders = orderService.getOrdersByCustomerId(customerId, page, size);
        } else if (status != null && !status.isEmpty()) {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            orders = orderService.getOrdersByStatus(orderStatus, page, size);
        } else {
            orders = orderService.getAllOrders(page, size);
        }
        return Response.ok(orders).build();
    }

    @Operation(summary = "Get an order by ID")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Order found"),
            @APIResponse(responseCode = "404", description = "Order not found")
    })
    @GET
    @Path("/{id}")
    public Response getOrderById(@PathParam("id") Long id) {
        Order order = orderService.getOrderById(id);
        return Response.ok(order).build();
    }

    @Operation(summary = "Update order status")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Status updated"),
            @APIResponse(responseCode = "400", description = "Invalid input or order not found")
    })
    @PUT
    @Path("/{id}/status")
    public Response updateOrderStatus(@PathParam("id") Long id, @Valid UpdateOrderStatusRequest request) {
        Order order = orderService.updateOrderStatus(id, request.getStatus());
        return Response.ok(order).build();
    }
}
