package com.bookstore.rest;

import com.bookstore.model.Book;
import com.bookstore.model.BookCategory;
import com.bookstore.service.BookService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookResourceTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookResource bookResource;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book("Test Book", "978-1234567890", "Test Publisher",
                BookCategory.SCIENCE_FICTION, 19.99, 100);
        testBook.setId(1L);
        testBook.setDateCreated(new Date());
    }

    @Test
    void testCreateBook_Success() {
        when(bookService.createBook(any(Book.class))).thenReturn(testBook);

        Response response = bookResource.createBook(testBook);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void testCreateBook_BadRequest() {
        when(bookService.createBook(any(Book.class)))
                .thenThrow(new IllegalArgumentException("Duplicate ISBN"));

        Response response = bookResource.createBook(testBook);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAllBooks_NoFilter() {
        when(bookService.getAllBooks()).thenReturn(Arrays.asList(testBook));

        Response response = bookResource.getAllBooks(null);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<Book> books = (List<Book>) response.getEntity();
        assertEquals(1, books.size());
    }

    @Test
    void testGetAllBooks_WithCategory() {
        when(bookService.getBooksByCategory(BookCategory.SCIENCE_FICTION))
                .thenReturn(Collections.singletonList(testBook));

        Response response = bookResource.getAllBooks("SCIENCE_FICTION");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAllBooks_InvalidCategory() {
        Response response = bookResource.getAllBooks("INVALID_CATEGORY");

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetBookById_Success() {
        when(bookService.getBookById(1L)).thenReturn(testBook);

        Response response = bookResource.getBookById(1L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Book result = (Book) response.getEntity();
        assertEquals("Test Book", result.getName());
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookService.getBookById(999L))
                .thenThrow(new IllegalArgumentException("Not found"));

        Response response = bookResource.getBookById(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetBookByIsbn_Success() {
        when(bookService.getBookByIsbn("978-1234567890")).thenReturn(testBook);

        Response response = bookResource.getBookByIsbn("978-1234567890");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetBookByIsbn_NotFound() {
        when(bookService.getBookByIsbn("invalid"))
                .thenThrow(new IllegalArgumentException("Not found"));

        Response response = bookResource.getBookByIsbn("invalid");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdateBook_Success() {
        when(bookService.updateBook(eq(1L), any(Book.class))).thenReturn(testBook);

        Response response = bookResource.updateBook(1L, testBook);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdateBook_NotFound() {
        when(bookService.updateBook(eq(999L), any(Book.class)))
                .thenThrow(new IllegalArgumentException("Not found"));

        Response response = bookResource.updateBook(999L, testBook);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testDeleteBook_Success() {
        doNothing().when(bookService).deleteBook(1L);

        Response response = bookResource.deleteBook(1L);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    void testDeleteBook_NotFound() {
        doThrow(new IllegalArgumentException("Not found")).when(bookService).deleteBook(999L);

        Response response = bookResource.deleteBook(999L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
}
