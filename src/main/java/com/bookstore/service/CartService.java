package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.model.Cart;
import com.bookstore.model.CartItem;
import com.bookstore.repository.BookRepository;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Service for managing shopping carts.
 * Carts are stored in Redis cache (via RedisCartStore), not in the database.
 */
@Stateless
public class CartService {

    private static final Logger LOGGER = Logger.getLogger(CartService.class.getName());

    @Inject
    private RedisCartStore redisCartStore;

    @EJB
    private BookRepository bookRepository;

    public Cart getCart(Long customerId) {
        Cart cart = redisCartStore.getCart(customerId);
        if (cart == null) {
            cart = new Cart(customerId);
            redisCartStore.saveCart(cart);
        }
        return cart;
    }

    public Cart addItemToCart(Long customerId, Long bookId, Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        Book book = bookRepository.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found with id: " + bookId);
        }

        Cart cart = getCart(customerId);
        CartItem item = new CartItem(book.getId(), book.getName(), book.getPrice(), quantity);
        cart.addItem(item);
        redisCartStore.saveCart(cart);

        LOGGER.info("Added book " + book.getName() + " (qty: " + quantity + ") to cart for customer " + customerId);
        return cart;
    }

    public Cart removeItemFromCart(Long customerId, Long bookId) {
        Cart cart = getCart(customerId);
        cart.removeItem(bookId);
        redisCartStore.saveCart(cart);

        LOGGER.info("Removed book " + bookId + " from cart for customer " + customerId);
        return cart;
    }

    public void clearCart(Long customerId) {
        redisCartStore.deleteCart(customerId);
        LOGGER.info("Cleared cart for customer " + customerId);
    }

    public void setRedisCartStore(RedisCartStore redisCartStore) {
        this.redisCartStore = redisCartStore;
    }

    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
}
