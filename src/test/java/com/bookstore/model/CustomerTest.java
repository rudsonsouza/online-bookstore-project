package com.bookstore.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer("John", "Doe", "john.doe@example.com", "+1-555-0101", "123 Main St");
    }

    @Test
    void testCustomerCreation() {
        assertNotNull(customer);
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("john.doe@example.com", customer.getEmail());
        assertEquals("+1-555-0101", customer.getMobilePhone());
        assertEquals("123 Main St", customer.getAddress());
    }

    @Test
    void testDefaultConstructor() {
        Customer emptyCustomer = new Customer();
        assertNotNull(emptyCustomer);
        assertNull(emptyCustomer.getId());
        assertNull(emptyCustomer.getFirstName());
    }

    @Test
    void testSettersAndGetters() {
        customer.setId(1L);
        customer.setFirstName("Jane");
        customer.setLastName("Smith");
        customer.setEmail("jane.smith@example.com");
        customer.setMobilePhone("+1-555-0202");
        customer.setAddress("456 Oak Ave");

        assertEquals(1L, customer.getId());
        assertEquals("Jane", customer.getFirstName());
        assertEquals("Smith", customer.getLastName());
        assertEquals("jane.smith@example.com", customer.getEmail());
        assertEquals("+1-555-0202", customer.getMobilePhone());
        assertEquals("456 Oak Ave", customer.getAddress());
    }

    @Test
    void testEquals() {
        Customer c1 = new Customer();
        c1.setId(1L);
        c1.setEmail("test@test.com");

        Customer c2 = new Customer();
        c2.setId(1L);
        c2.setEmail("test@test.com");

        assertEquals(c1, c2);
    }

    @Test
    void testNotEquals() {
        Customer c1 = new Customer();
        c1.setId(1L);
        c1.setEmail("test1@test.com");

        Customer c2 = new Customer();
        c2.setId(2L);
        c2.setEmail("test2@test.com");

        assertNotEquals(c1, c2);
    }

    @Test
    void testHashCode() {
        Customer c1 = new Customer();
        c1.setId(1L);
        c1.setEmail("test@test.com");

        Customer c2 = new Customer();
        c2.setId(1L);
        c2.setEmail("test@test.com");

        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testToString() {
        customer.setId(1L);
        String result = customer.toString();
        assertTrue(result.contains("John"));
        assertTrue(result.contains("Doe"));
        assertTrue(result.contains("john.doe@example.com"));
    }
}
