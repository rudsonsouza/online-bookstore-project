package com.bookstore.rest;

import com.bookstore.model.Customer;
import com.bookstore.service.CustomerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerResourceTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerResource customerResource;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer("John", "Doe", "john@example.com",
                "+1-555-0101", "123 Main St");
        testCustomer.setId(1L);
    }

    @Test
    void testCreateCustomer_Success() {
        when(customerService.createCustomer(any(Customer.class))).thenReturn(testCustomer);

        Response response = customerResource.createCustomer(testCustomer);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void testCreateCustomer_BadRequest() {
        when(customerService.createCustomer(any(Customer.class)))
                .thenThrow(new IllegalArgumentException("Duplicate email"));

        Response response = customerResource.createCustomer(testCustomer);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAllCustomers() {
        when(customerService.getAllCustomers()).thenReturn(Arrays.asList(testCustomer));

        Response response = customerResource.getAllCustomers();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<Customer> customers = (List<Customer>) response.getEntity();
        assertEquals(1, customers.size());
    }

    @Test
    void testGetCustomerById_Success() {
        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);

        Response response = customerResource.getCustomerById(1L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Customer result = (Customer) response.getEntity();
        assertEquals("John", result.getFirstName());
    }

    @Test
    void testGetCustomerById_NotFound() {
        when(customerService.getCustomerById(999L))
                .thenThrow(new IllegalArgumentException("Not found"));

        Response response = customerResource.getCustomerById(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetCustomerByEmail_Success() {
        when(customerService.getCustomerByEmail("john@example.com")).thenReturn(testCustomer);

        Response response = customerResource.getCustomerByEmail("john@example.com");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetCustomerByEmail_NotFound() {
        when(customerService.getCustomerByEmail("unknown@example.com"))
                .thenThrow(new IllegalArgumentException("Not found"));

        Response response = customerResource.getCustomerByEmail("unknown@example.com");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdateCustomer_Success() {
        when(customerService.updateCustomer(eq(1L), any(Customer.class))).thenReturn(testCustomer);

        Response response = customerResource.updateCustomer(1L, testCustomer);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdateCustomer_NotFound() {
        when(customerService.updateCustomer(eq(999L), any(Customer.class)))
                .thenThrow(new IllegalArgumentException("Not found"));

        Response response = customerResource.updateCustomer(999L, testCustomer);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testDeleteCustomer_Success() {
        doNothing().when(customerService).deleteCustomer(1L);

        Response response = customerResource.deleteCustomer(1L);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    void testDeleteCustomer_NotFound() {
        doThrow(new IllegalArgumentException("Not found")).when(customerService).deleteCustomer(999L);

        Response response = customerResource.deleteCustomer(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
}
