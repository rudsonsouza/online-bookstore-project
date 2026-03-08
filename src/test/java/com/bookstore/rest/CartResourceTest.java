package com.bookstore.rest;

import com.bookstore.model.Cart;
import com.bookstore.model.CartItem;
import com.bookstore.rest.dto.AddToCartRequest;
import com.bookstore.service.CartService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartResourceTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartResource cartResource;

    private Cart testCart;

    @BeforeEach
    void setUp() {
        testCart = new Cart(1L);
        testCart.addItem(new CartItem(1L, "Test Book", 19.99, 2));
    }

    @Test
    void testGetCart_Success() {
        when(cartService.getCart(1L)).thenReturn(testCart);

        Response response = cartResource.getCart(1L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Cart result = (Cart) response.getEntity();
        assertEquals(1L, result.getCustomerId());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void testGetCart_Error() {
        when(cartService.getCart(1L)).thenThrow(new RuntimeException("Redis unavailable"));

        Response response = cartResource.getCart(1L);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testAddItemToCart_Success() {
        AddToCartRequest request = new AddToCartRequest(1L, 2);
        when(cartService.addItemToCart(1L, 1L, 2)).thenReturn(testCart);

        Response response = cartResource.addItemToCart(1L, request);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testAddItemToCart_BadRequest() {
        AddToCartRequest request = new AddToCartRequest(999L, 1);
        when(cartService.addItemToCart(1L, 999L, 1))
                .thenThrow(new IllegalArgumentException("Book not found"));

        Response response = cartResource.addItemToCart(1L, request);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testRemoveItemFromCart_Success() {
        Cart emptyCart = new Cart(1L);
        when(cartService.removeItemFromCart(1L, 1L)).thenReturn(emptyCart);

        Response response = cartResource.removeItemFromCart(1L, 1L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Cart result = (Cart) response.getEntity();
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void testClearCart_Success() {
        doNothing().when(cartService).clearCart(1L);

        Response response = cartResource.clearCart(1L);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(cartService).clearCart(1L);
    }

    @Test
    void testClearCart_Error() {
        doThrow(new RuntimeException("Error")).when(cartService).clearCart(1L);

        Response response = cartResource.clearCart(1L);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }
}
