package com.bookstore.rest;

import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.model.OrderStatus;
import com.bookstore.model.PaymentMethod;
import com.bookstore.rest.dto.CreateOrderRequest;
import com.bookstore.rest.dto.UpdateOrderStatusRequest;
import com.bookstore.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderResourceTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderResource orderResource;

    private Order testOrder;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer("John", "Doe", "john@example.com",
                "+1-555-0101", "123 Main St");
        testCustomer.setId(1L);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomer(testCustomer);
        testOrder.setDeliveryAddress("456 Oak Ave");
        testOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        testOrder.setOrderStatus(OrderStatus.PENDING);
        testOrder.setTotalPrice(39.98);
        testOrder.setPriceWithoutVat(33.32);
        testOrder.setDateCreated(new Date());
    }

    @Test
    void testCreateOrder_Success() {
        CreateOrderRequest request = new CreateOrderRequest(1L, "456 Oak Ave", PaymentMethod.CREDIT_CARD);
        when(orderService.createOrderFromCart(1L, "456 Oak Ave", PaymentMethod.CREDIT_CARD))
                .thenReturn(testOrder);

        Response response = orderResource.createOrder(request);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void testCreateOrder_BadRequest() {
        CreateOrderRequest request = new CreateOrderRequest(999L, "address", PaymentMethod.CREDIT_CARD);
        when(orderService.createOrderFromCart(999L, "address", PaymentMethod.CREDIT_CARD))
                .thenThrow(new IllegalArgumentException("Customer not found"));

        Response response = orderResource.createOrder(request);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAllOrders_NoFilter() {
        when(orderService.getAllOrders()).thenReturn(Collections.singletonList(testOrder));

        Response response = orderResource.getAllOrders(null, null);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<Order> orders = (List<Order>) response.getEntity();
        assertEquals(1, orders.size());
    }

    @Test
    void testGetAllOrders_ByCustomerId() {
        when(orderService.getOrdersByCustomerId(1L))
                .thenReturn(Collections.singletonList(testOrder));

        Response response = orderResource.getAllOrders(1L, null);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAllOrders_ByStatus() {
        when(orderService.getOrdersByStatus(OrderStatus.PENDING))
                .thenReturn(Collections.singletonList(testOrder));

        Response response = orderResource.getAllOrders(null, "PENDING");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAllOrders_InvalidStatus() {
        Response response = orderResource.getAllOrders(null, "INVALID_STATUS");

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetOrderById_Success() {
        when(orderService.getOrderById(1L)).thenReturn(testOrder);

        Response response = orderResource.getOrderById(1L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Order result = (Order) response.getEntity();
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderService.getOrderById(999L))
                .thenThrow(new IllegalArgumentException("Not found"));

        Response response = orderResource.getOrderById(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdateOrderStatus_Success() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.SHIPPED);
        when(orderService.updateOrderStatus(1L, OrderStatus.SHIPPED)).thenReturn(testOrder);

        Response response = orderResource.updateOrderStatus(1L, request);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdateOrderStatus_BadRequest() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.SHIPPED);
        when(orderService.updateOrderStatus(999L, OrderStatus.SHIPPED))
                .thenThrow(new IllegalArgumentException("Order not found"));

        Response response = orderResource.updateOrderStatus(999L, request);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }
}
