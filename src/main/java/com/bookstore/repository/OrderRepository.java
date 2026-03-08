package com.bookstore.repository;

import com.bookstore.model.Order;
import com.bookstore.model.OrderStatus;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class OrderRepository {

    @PersistenceContext(unitName = "BookstorePU")
    private EntityManager em;

    public Order create(Order order) {
        em.persist(order);
        return order;
    }

    public Order findById(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll() {
        return em.createQuery("SELECT o FROM Order o ORDER BY o.dateCreated DESC", Order.class)
                .getResultList();
    }

    public List<Order> findByCustomerId(Long customerId) {
        TypedQuery<Order> query = em.createQuery(
                "SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.dateCreated DESC", Order.class);
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    public List<Order> findByStatus(OrderStatus status) {
        TypedQuery<Order> query = em.createQuery(
                "SELECT o FROM Order o WHERE o.orderStatus = :status ORDER BY o.dateCreated DESC", Order.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    public Order update(Order order) {
        return em.merge(order);
    }

    public void delete(Long id) {
        Order order = findById(id);
        if (order != null) {
            em.remove(order);
        }
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
