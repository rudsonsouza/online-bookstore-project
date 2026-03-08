package com.bookstore.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book("Test Book", "978-1234567890", "Test Publisher", BookCategory.SCIENCE_FICTION, 19.99, 100);
    }

    @Test
    void testBookCreation() {
        assertNotNull(book);
        assertEquals("Test Book", book.getName());
        assertEquals("978-1234567890", book.getIsbn());
        assertEquals("Test Publisher", book.getPublicationHouse());
        assertEquals(BookCategory.SCIENCE_FICTION, book.getCategory());
        assertEquals(19.99, book.getPrice());
        assertEquals(100, book.getStockQuantity());
    }

    @Test
    void testBookDefaultConstructor() {
        Book emptyBook = new Book();
        assertNotNull(emptyBook);
        assertNull(emptyBook.getId());
        assertNull(emptyBook.getName());
    }

    @Test
    void testSettersAndGetters() {
        book.setId(1L);
        book.setName("Updated Book");
        book.setIsbn("978-0987654321");
        book.setPublicationHouse("Updated Publisher");
        book.setCategory(BookCategory.HORROR);
        book.setPrice(29.99);
        book.setStockQuantity(50);

        assertEquals(1L, book.getId());
        assertEquals("Updated Book", book.getName());
        assertEquals("978-0987654321", book.getIsbn());
        assertEquals("Updated Publisher", book.getPublicationHouse());
        assertEquals(BookCategory.HORROR, book.getCategory());
        assertEquals(29.99, book.getPrice());
        assertEquals(50, book.getStockQuantity());
    }

    @Test
    void testEquals() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setIsbn("978-1234567890");

        Book book2 = new Book();
        book2.setId(1L);
        book2.setIsbn("978-1234567890");

        assertEquals(book1, book2);
    }

    @Test
    void testNotEquals() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setIsbn("978-1234567890");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setIsbn("978-0987654321");

        assertNotEquals(book1, book2);
    }

    @Test
    void testHashCode() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setIsbn("978-1234567890");

        Book book2 = new Book();
        book2.setId(1L);
        book2.setIsbn("978-1234567890");

        assertEquals(book1.hashCode(), book2.hashCode());
    }

    @Test
    void testToString() {
        book.setId(1L);
        String result = book.toString();
        assertTrue(result.contains("Test Book"));
        assertTrue(result.contains("978-1234567890"));
    }

    @Test
    void testOnCreate() {
        book.onCreate();
        assertNotNull(book.getDateCreated());
    }
}
