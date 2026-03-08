package com.bookstore.repository;

import com.bookstore.model.Customer;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class CustomerRepository {

    @PersistenceContext(unitName = "BookstorePU")
    private EntityManager em;

    public Customer create(Customer customer) {
        em.persist(customer);
        return customer;
    }

    public Customer findById(Long id) {
        return em.find(Customer.class, id);
    }

    public Customer findByEmail(String email) {
        TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c WHERE c.email = :email", Customer.class);
        query.setParameter("email", email);
        List<Customer> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public List<Customer> findAll() {
        return em.createQuery("SELECT c FROM Customer c ORDER BY c.lastName, c.firstName", Customer.class)
                .getResultList();
    }

    public Customer update(Customer customer) {
        return em.merge(customer);
    }

    public void delete(Long id) {
        Customer customer = findById(id);
        if (customer != null) {
            em.remove(customer);
        }
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
