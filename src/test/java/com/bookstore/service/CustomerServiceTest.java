package com.bookstore.service;

import com.bookstore.model.Customer;
import com.bookstore.repository.CustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer("John", "Doe", "john.doe@example.com",
                "+1-555-0101", "123 Main Street");
        testCustomer.setId(1L);
    }

    @Test
    void testCreateCustomer_Success() {
        when(customerRepository.findByEmail("john.doe@example.com")).thenReturn(null);
        when(customerRepository.create(any(Customer.class))).thenReturn(testCustomer);

        Customer result = customerService.createCustomer(testCustomer);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(customerRepository).findByEmail("john.doe@example.com");
        verify(customerRepository).create(testCustomer);
    }

    @Test
    void testCreateCustomer_DuplicateEmail() {
        when(customerRepository.findByEmail("john.doe@example.com")).thenReturn(testCustomer);

        assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(testCustomer));
        verify(customerRepository, never()).create(any(Customer.class));
    }

    @Test
    void testGetCustomerById_Success() {
        when(customerRepository.findById(1L)).thenReturn(testCustomer);

        Customer result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetCustomerById_NotFound() {
        when(customerRepository.findById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> customerService.getCustomerById(999L));
    }

    @Test
    void testGetCustomerByEmail_Success() {
        when(customerRepository.findByEmail("john.doe@example.com")).thenReturn(testCustomer);

        Customer result = customerService.getCustomerByEmail("john.doe@example.com");

        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void testGetCustomerByEmail_NotFound() {
        when(customerRepository.findByEmail("unknown@example.com")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> customerService.getCustomerByEmail("unknown@example.com"));
    }

    @Test
    void testGetAllCustomers() {
        Customer customer2 = new Customer("Jane", "Smith", "jane@example.com",
                "+1-555-0102", "456 Oak Ave");
        customer2.setId(2L);

        when(customerRepository.findAll()).thenReturn(Arrays.asList(testCustomer, customer2));

        List<Customer> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
    }

    @Test
    void testGetAllCustomers_Empty() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        List<Customer> result = customerService.getAllCustomers();

        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateCustomer_Success() {
        Customer updatedData = new Customer("Jane", "Smith", "jane@example.com",
                "+1-555-0202", "789 Pine Rd");

        when(customerRepository.findById(1L)).thenReturn(testCustomer);
        when(customerRepository.update(any(Customer.class))).thenReturn(testCustomer);

        Customer result = customerService.updateCustomer(1L, updatedData);

        assertNotNull(result);
        verify(customerRepository).findById(1L);
        verify(customerRepository).update(testCustomer);
    }

    @Test
    void testUpdateCustomer_NotFound() {
        when(customerRepository.findById(999L)).thenReturn(null);

        Customer updatedData = new Customer("Jane", "Smith", "jane@example.com",
                "+1-555-0202", "789 Pine Rd");

        assertThrows(IllegalArgumentException.class,
                () -> customerService.updateCustomer(999L, updatedData));
        verify(customerRepository, never()).update(any(Customer.class));
    }

    @Test
    void testDeleteCustomer_Success() {
        when(customerRepository.findById(1L)).thenReturn(testCustomer);
        doNothing().when(customerRepository).delete(1L);

        customerService.deleteCustomer(1L);

        verify(customerRepository).findById(1L);
        verify(customerRepository).delete(1L);
    }

    @Test
    void testDeleteCustomer_NotFound() {
        when(customerRepository.findById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> customerService.deleteCustomer(999L));
        verify(customerRepository, never()).delete(anyLong());
    }
}
