package com.bookstore.repository;

import com.bookstore.model.Book;
import com.bookstore.model.BookCategory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class BookRepository {

    @PersistenceContext(unitName = "BookstorePU")
    private EntityManager em;

    public Book create(Book book) {
        em.persist(book);
        return book;
    }

    public Book findById(Long id) {
        return em.find(Book.class, id);
    }

    public Book findByIsbn(String isbn) {
        TypedQuery<Book> query = em.createQuery(
                "SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class);
        query.setParameter("isbn", isbn);
        List<Book> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public List<Book> findAll() {
        return em.createQuery("SELECT b FROM Book b ORDER BY b.name", Book.class)
                .getResultList();
    }

    public List<Book> findAll(int page, int size) {
        return em.createQuery("SELECT b FROM Book b ORDER BY b.name", Book.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Book> findByCategory(BookCategory category) {
        TypedQuery<Book> query = em.createQuery(
                "SELECT b FROM Book b WHERE b.category = :category ORDER BY b.name", Book.class);
        query.setParameter("category", category);
        return query.getResultList();
    }

    public List<Book> findByCategory(BookCategory category, int page, int size) {
        TypedQuery<Book> query = em.createQuery(
                "SELECT b FROM Book b WHERE b.category = :category ORDER BY b.name", Book.class);
        query.setParameter("category", category);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public Book update(Book book) {
        return em.merge(book);
    }

    public void delete(Long id) {
        Book book = findById(id);
        if (book != null) {
            em.remove(book);
        }
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
