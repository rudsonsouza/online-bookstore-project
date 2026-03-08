package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.model.BookCategory;
import com.bookstore.repository.BookRepository;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class BookService {

    private static final Logger LOGGER = Logger.getLogger(BookService.class.getName());

    @EJB
    private BookRepository bookRepository;

    public Book createBook(Book book) {
        LOGGER.info("Creating book: " + book.getName());
        Book existing = bookRepository.findByIsbn(book.getIsbn());
        if (existing != null) {
            throw new IllegalArgumentException("A book with ISBN " + book.getIsbn() + " already exists");
        }
        return bookRepository.create(book);
    }

    public Book getBookById(Long id) {
        Book book = bookRepository.findById(id);
        if (book == null) {
            throw new IllegalArgumentException("Book not found with id: " + id);
        }
        return book;
    }

    public Book getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            throw new IllegalArgumentException("Book not found with ISBN: " + isbn);
        }
        return book;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getBooksByCategory(BookCategory category) {
        return bookRepository.findByCategory(category);
    }

    public Book updateBook(Long id, Book bookData) {
        Book existing = bookRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Book not found with id: " + id);
        }
        existing.setName(bookData.getName());
        existing.setIsbn(bookData.getIsbn());
        existing.setPublicationHouse(bookData.getPublicationHouse());
        existing.setCategory(bookData.getCategory());
        existing.setPrice(bookData.getPrice());
        existing.setStockQuantity(bookData.getStockQuantity());
        LOGGER.info("Updating book: " + existing.getName());
        return bookRepository.update(existing);
    }

    public void deleteBook(Long id) {
        Book existing = bookRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Book not found with id: " + id);
        }
        LOGGER.info("Deleting book: " + existing.getName());
        bookRepository.delete(id);
    }

    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
}
