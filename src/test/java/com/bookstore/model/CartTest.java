package com.bookstore.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart(1L);
    }

    @Test
    void testCartCreation() {
        assertNotNull(cart);
        assertEquals(1L, cart.getCustomerId());
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testAddItem() {
        CartItem item = new CartItem(1L, "Test Book", 19.99, 2);
        cart.addItem(item);

        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getTotalItems());
    }

    @Test
    void testAddItemMergesQuantity() {
        CartItem item1 = new CartItem(1L, "Test Book", 19.99, 2);
        CartItem item2 = new CartItem(1L, "Test Book", 19.99, 3);

        cart.addItem(item1);
        cart.addItem(item2);

        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    void testAddDifferentItems() {
        CartItem item1 = new CartItem(1L, "Book 1", 19.99, 1);
        CartItem item2 = new CartItem(2L, "Book 2", 29.99, 1);

        cart.addItem(item1);
        cart.addItem(item2);

        assertEquals(2, cart.getItems().size());
    }

    @Test
    void testRemoveItem() {
        CartItem item = new CartItem(1L, "Test Book", 19.99, 2);
        cart.addItem(item);
        cart.removeItem(1L);

        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testGetTotalPrice() {
        cart.addItem(new CartItem(1L, "Book 1", 10.00, 2)); // 20.00
        cart.addItem(new CartItem(2L, "Book 2", 15.00, 1)); // 15.00

        assertEquals(35.00, cart.getTotalPrice(), 0.01);
    }

    @Test
    void testGetTotalItems() {
        cart.addItem(new CartItem(1L, "Book 1", 10.00, 2));
        cart.addItem(new CartItem(2L, "Book 2", 15.00, 3));

        assertEquals(5, cart.getTotalItems());
    }

    @Test
    void testEmptyCartTotalPrice() {
        assertEquals(0.0, cart.getTotalPrice(), 0.01);
    }

    @Test
    void testEquals() {
        Cart cart1 = new Cart(1L);
        Cart cart2 = new Cart(1L);
        assertEquals(cart1, cart2);
    }

    @Test
    void testNotEquals() {
        Cart cart1 = new Cart(1L);
        Cart cart2 = new Cart(2L);
        assertNotEquals(cart1, cart2);
    }

    @Test
    void testHashCode() {
        Cart cart1 = new Cart(1L);
        Cart cart2 = new Cart(1L);
        assertEquals(cart1.hashCode(), cart2.hashCode());
    }

    @Test
    void testToString() {
        String result = cart.toString();
        assertTrue(result.contains("customerId=1"));
    }
}
