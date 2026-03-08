package com.bookstore.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;
    private Customer customer;
    private Book book;

    @BeforeEach
    void setUp() {
        customer = new Customer("John", "Doe", "john@example.com", "+1-555-0101", "123 Main St");
        customer.setId(1L);

        book = new Book("Test Book", "978-1234567890", "Publisher", BookCategory.SCIENCE_FICTION, 19.99, 100);
        book.setId(1L);

        order = new Order();
        order.setCustomer(customer);
        order.setDeliveryAddress("456 Oak Ave");
        order.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setTotalPrice(39.98);
        order.setPriceWithoutVat(33.32);
    }

    @Test
    void testOrderCreation() {
        assertNotNull(order);
        assertEquals(customer, order.getCustomer());
        assertEquals("456 Oak Ave", order.getDeliveryAddress());
        assertEquals(PaymentMethod.CREDIT_CARD, order.getPaymentMethod());
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
        assertEquals(39.98, order.getTotalPrice());
        assertEquals(33.32, order.getPriceWithoutVat());
    }

    @Test
    void testAddItem() {
        OrderItem item = new OrderItem(book, 2, 19.99);
        order.addItem(item);

        assertEquals(1, order.getItems().size());
        assertEquals(order, item.getOrder());
    }

    @Test
    void testRemoveItem() {
        OrderItem item = new OrderItem(book, 2, 19.99);
        order.addItem(item);
        order.removeItem(item);

        assertTrue(order.getItems().isEmpty());
        assertNull(item.getOrder());
    }

    @Test
    void testOnCreate() {
        order.onCreate();
        assertNotNull(order.getDateCreated());
        assertNotNull(order.getDateUpdated());
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
    }

    @Test
    void testOnCreateDefaultStatus() {
        Order newOrder = new Order();
        newOrder.onCreate();
        assertEquals(OrderStatus.PENDING, newOrder.getOrderStatus());
    }

    @Test
    void testSetOrderStatusUpdatesDate() {
        order.setOrderStatus(OrderStatus.SHIPPED);
        assertEquals(OrderStatus.SHIPPED, order.getOrderStatus());
        assertNotNull(order.getDateUpdated());
    }

    @Test
    void testEquals() {
        Order o1 = new Order();
        o1.setId(1L);

        Order o2 = new Order();
        o2.setId(1L);

        assertEquals(o1, o2);
    }

    @Test
    void testNotEquals() {
        Order o1 = new Order();
        o1.setId(1L);

        Order o2 = new Order();
        o2.setId(2L);

        assertNotEquals(o1, o2);
    }

    @Test
    void testToString() {
        order.setId(1L);
        String result = order.toString();
        assertTrue(result.contains("id=1"));
    }
}
