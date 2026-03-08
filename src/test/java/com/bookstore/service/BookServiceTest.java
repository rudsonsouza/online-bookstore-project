package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.model.BookCategory;
import com.bookstore.repository.BookRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

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
        when(bookRepository.findByIsbn("978-1234567890")).thenReturn(null);
        when(bookRepository.create(any(Book.class))).thenReturn(testBook);

        Book result = bookService.createBook(testBook);

        assertNotNull(result);
        assertEquals("Test Book", result.getName());
        verify(bookRepository).findByIsbn("978-1234567890");
        verify(bookRepository).create(testBook);
    }

    @Test
    void testCreateBook_DuplicateIsbn() {
        when(bookRepository.findByIsbn("978-1234567890")).thenReturn(testBook);

        assertThrows(IllegalArgumentException.class, () -> bookService.createBook(testBook));
        verify(bookRepository, never()).create(any(Book.class));
    }

    @Test
    void testGetBookById_Success() {
        when(bookRepository.findById(1L)).thenReturn(testBook);

        Book result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Book", result.getName());
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> bookService.getBookById(999L));
    }

    @Test
    void testGetBookByIsbn_Success() {
        when(bookRepository.findByIsbn("978-1234567890")).thenReturn(testBook);

        Book result = bookService.getBookByIsbn("978-1234567890");

        assertNotNull(result);
        assertEquals("978-1234567890", result.getIsbn());
    }

    @Test
    void testGetBookByIsbn_NotFound() {
        when(bookRepository.findByIsbn("invalid")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> bookService.getBookByIsbn("invalid"));
    }

    @Test
    void testGetAllBooks() {
        Book book2 = new Book("Book 2", "978-0987654321", "Publisher 2",
                BookCategory.HORROR, 14.99, 50);
        book2.setId(2L);

        when(bookRepository.findAll()).thenReturn(Arrays.asList(testBook, book2));

        List<Book> result = bookService.getAllBooks();

        assertEquals(2, result.size());
        verify(bookRepository).findAll();
    }

    @Test
    void testGetAllBooks_Empty() {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        List<Book> result = bookService.getAllBooks();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetBooksByCategory() {
        when(bookRepository.findByCategory(BookCategory.SCIENCE_FICTION))
                .thenReturn(Collections.singletonList(testBook));

        List<Book> result = bookService.getBooksByCategory(BookCategory.SCIENCE_FICTION);

        assertEquals(1, result.size());
        assertEquals(BookCategory.SCIENCE_FICTION, result.get(0).getCategory());
    }

    @Test
    void testUpdateBook_Success() {
        Book updatedData = new Book("Updated Book", "978-1234567890", "New Publisher",
                BookCategory.FANTASY, 24.99, 80);

        when(bookRepository.findById(1L)).thenReturn(testBook);
        when(bookRepository.update(any(Book.class))).thenReturn(testBook);

        Book result = bookService.updateBook(1L, updatedData);

        assertNotNull(result);
        verify(bookRepository).findById(1L);
        verify(bookRepository).update(testBook);
    }

    @Test
    void testUpdateBook_NotFound() {
        when(bookRepository.findById(999L)).thenReturn(null);

        Book updatedData = new Book("Updated Book", "978-1234567890", "New Publisher",
                BookCategory.FANTASY, 24.99, 80);

        assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(999L, updatedData));
        verify(bookRepository, never()).update(any(Book.class));
    }

    @Test
    void testDeleteBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(testBook);
        doNothing().when(bookRepository).delete(1L);

        bookService.deleteBook(1L);

        verify(bookRepository).findById(1L);
        verify(bookRepository).delete(1L);
    }

    @Test
    void testDeleteBook_NotFound() {
        when(bookRepository.findById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> bookService.deleteBook(999L));
        verify(bookRepository, never()).delete(anyLong());
    }
}
