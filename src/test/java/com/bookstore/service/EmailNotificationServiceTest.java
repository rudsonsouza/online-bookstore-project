package com.bookstore.service;

import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.model.OrderStatus;
import com.bookstore.model.PaymentMethod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class EmailNotificationServiceTest {

    private EmailNotificationService emailNotificationService;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        emailNotificationService = new EmailNotificationService();

        Customer customer = new Customer("John", "Doe", "john@example.com",
                "+1-555-0101", "123 Main St");
        customer.setId(1L);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomer(customer);
        testOrder.setDeliveryAddress("456 Oak Ave");
        testOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        testOrder.setOrderStatus(OrderStatus.PENDING);
        testOrder.setTotalPrice(39.98);
        testOrder.setPriceWithoutVat(33.32);
        testOrder.setDateCreated(new Date());
    }

    @Test
    void testSendOrderConfirmation_NoException() {
        // Should not throw any exception (stub implementation logs only)
        assertDoesNotThrow(() -> emailNotificationService.sendOrderConfirmation(testOrder));
    }

    @Test
    void testSendOrderStatusUpdate_NoException() {
        // Should not throw any exception (stub implementation logs only)
        assertDoesNotThrow(() -> emailNotificationService.sendOrderStatusUpdate(
                testOrder, OrderStatus.PENDING, OrderStatus.SHIPPED));
    }

    @Test
    void testSendOrderConfirmation_WithDifferentPaymentMethods() {
        for (PaymentMethod method : PaymentMethod.values()) {
            testOrder.setPaymentMethod(method);
            assertDoesNotThrow(() -> emailNotificationService.sendOrderConfirmation(testOrder));
        }
    }

    @Test
    void testSendOrderStatusUpdate_AllStatusTransitions() {
        OrderStatus[] statuses = OrderStatus.values();
        for (OrderStatus from : statuses) {
            for (OrderStatus to : statuses) {
                if (from != to) {
                    assertDoesNotThrow(() ->
                            emailNotificationService.sendOrderStatusUpdate(testOrder, from, to));
                }
            }
        }
    }
}
