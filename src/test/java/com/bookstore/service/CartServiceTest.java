package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.model.BookCategory;
import com.bookstore.model.Cart;
import com.bookstore.model.CartItem;
import com.bookstore.repository.BookRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private RedisCartStore redisCartStore;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CartService cartService;

    private Book testBook;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        testBook = new Book("Test Book", "978-1234567890", "Publisher",
                BookCategory.SCIENCE_FICTION, 19.99, 100);
        testBook.setId(1L);

        testCart = new Cart(1L);
    }

    @Test
    void testGetCart_Existing() {
        when(redisCartStore.getCart(1L)).thenReturn(testCart);

        Cart result = cartService.getCart(1L);

        assertNotNull(result);
        assertEquals(1L, result.getCustomerId());
        verify(redisCartStore).getCart(1L);
    }

    @Test
    void testGetCart_NewCart() {
        when(redisCartStore.getCart(1L)).thenReturn(null);

        Cart result = cartService.getCart(1L);

        assertNotNull(result);
        assertEquals(1L, result.getCustomerId());
        verify(redisCartStore).saveCart(any(Cart.class));
    }

    @Test
    void testAddItemToCart_Success() {
        when(redisCartStore.getCart(1L)).thenReturn(testCart);
        when(bookRepository.findById(1L)).thenReturn(testBook);

        Cart result = cartService.addItemToCart(1L, 1L, 2);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(2, result.getItems().get(0).getQuantity());
        assertEquals(19.99, result.getItems().get(0).getPrice());
        verify(redisCartStore).saveCart(any(Cart.class));
    }

    @Test
    void testAddItemToCart_BookNotFound() {
        when(bookRepository.findById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> cartService.addItemToCart(1L, 999L, 1));
    }

    @Test
    void testAddItemToCart_InvalidQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addItemToCart(1L, 1L, 0));
    }

    @Test
    void testAddItemToCart_NullQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.addItemToCart(1L, 1L, null));
    }

    @Test
    void testRemoveItemFromCart() {
        CartItem item = new CartItem(1L, "Test Book", 19.99, 2);
        testCart.addItem(item);

        when(redisCartStore.getCart(1L)).thenReturn(testCart);

        Cart result = cartService.removeItemFromCart(1L, 1L);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        verify(redisCartStore).saveCart(any(Cart.class));
    }

    @Test
    void testClearCart() {
        doNothing().when(redisCartStore).deleteCart(1L);

        cartService.clearCart(1L);

        verify(redisCartStore).deleteCart(1L);
    }
}
