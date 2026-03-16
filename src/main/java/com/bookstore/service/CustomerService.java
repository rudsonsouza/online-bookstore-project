package com.bookstore.service;

import com.bookstore.model.Customer;
import com.bookstore.repository.CustomerRepository;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class CustomerService {

    private static final Logger LOGGER = Logger.getLogger(CustomerService.class.getName());

    @EJB
    private CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        LOGGER.info("Creating customer: " + customer.getEmail());
        Customer existing = customerRepository.findByEmail(customer.getEmail());
        if (existing != null) {
            throw new IllegalArgumentException("A customer with email " + customer.getEmail() + " already exists");
        }
        return customerRepository.create(customer);
    }

    public Customer getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found with id: " + id);
        }
        return customer;
    }

    public Customer getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found with email: " + email);
        }
        return customer;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Customer> getAllCustomers(int page, int size) {
        return customerRepository.findAll(page, size);
    }

    public Customer updateCustomer(Long id, Customer customerData) {
        Customer existing = customerRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Customer not found with id: " + id);
        }
        existing.setFirstName(customerData.getFirstName());
        existing.setLastName(customerData.getLastName());
        existing.setEmail(customerData.getEmail());
        existing.setMobilePhone(customerData.getMobilePhone());
        existing.setAddress(customerData.getAddress());
        LOGGER.info("Updating customer: " + existing.getEmail());
        return customerRepository.update(existing);
    }

    public void deleteCustomer(Long id) {
        Customer existing = customerRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Customer not found with id: " + id);
        }
        LOGGER.info("Deleting customer: " + existing.getEmail());
        customerRepository.delete(id);
    }

    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
}
