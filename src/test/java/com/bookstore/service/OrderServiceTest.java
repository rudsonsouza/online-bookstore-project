package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.model.BookCategory;
import com.bookstore.model.Cart;
import com.bookstore.model.CartItem;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.model.OrderStatus;
import com.bookstore.model.PaymentMethod;
import com.bookstore.jms.OrderMessageProducer;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CustomerRepository;
import com.bookstore.repository.OrderRepository;

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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CartService cartService;

    @Mock
    private EmailNotificationService emailNotificationService;

    @Mock
    private OrderMessageProducer orderMessageProducer;

    @InjectMocks
    private OrderService orderService;

    private Customer testCustomer;
    private Book testBook;
    private Cart testCart;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer("John", "Doe", "john@example.com",
                "+1-555-0101", "123 Main St");
        testCustomer.setId(1L);

        testBook = new Book("Test Book", "978-1234567890", "Publisher",
                BookCategory.SCIENCE_FICTION, 19.99, 100);
        testBook.setId(1L);

        testCart = new Cart(1L);
        testCart.addItem(new CartItem(1L, "Test Book", 19.99, 2));

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomer(testCustomer);
        testOrder.setDeliveryAddress("123 Main St");
        testOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        testOrder.setOrderStatus(OrderStatus.PENDING);
        testOrder.setTotalPrice(39.98);
        testOrder.setPriceWithoutVat(33.32);
        testOrder.setDateCreated(new Date());
    }

    @Test
    void testCreateOrderFromCart_Success() {
        when(customerRepository.findById(1L)).thenReturn(testCustomer);
        when(cartService.getCart(1L)).thenReturn(testCart);
        when(bookRepository.findById(1L)).thenReturn(testBook);
        when(bookRepository.update(any(Book.class))).thenReturn(testBook);
        when(orderRepository.create(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });
        doNothing().when(cartService).clearCart(1L);
        doNothing().when(emailNotificationService).sendOrderConfirmation(any(Order.class));

        Order result = orderService.createOrderFromCart(1L, "456 Oak Ave", PaymentMethod.CREDIT_CARD);

        assertNotNull(result);
        assertEquals(PaymentMethod.CREDIT_CARD, result.getPaymentMethod());
        assertEquals("456 Oak Ave", result.getDeliveryAddress());
        assertEquals(OrderStatus.PENDING, result.getOrderStatus());
        assertFalse(result.getItems().isEmpty());
        verify(cartService).clearCart(1L);
        verify(emailNotificationService).sendOrderConfirmation(any(Order.class));
    }

    @Test
    void testCreateOrderFromCart_CustomerNotFound() {
        when(customerRepository.findById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrderFromCart(999L, "address", PaymentMethod.CREDIT_CARD));
    }

    @Test
    void testCreateOrderFromCart_EmptyCart() {
        Cart emptyCart = new Cart(1L);
        when(customerRepository.findById(1L)).thenReturn(testCustomer);
        when(cartService.getCart(1L)).thenReturn(emptyCart);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrderFromCart(1L, "address", PaymentMethod.CREDIT_CARD));
    }

    @Test
    void testCreateOrderFromCart_InsufficientStock() {
        testBook.setStockQuantity(1); // Only 1 in stock but cart wants 2

        when(customerRepository.findById(1L)).thenReturn(testCustomer);
        when(cartService.getCart(1L)).thenReturn(testCart);
        when(bookRepository.findById(1L)).thenReturn(testBook);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrderFromCart(1L, "address", PaymentMethod.CREDIT_CARD));
    }

    @Test
    void testCreateOrderFromCart_StockDepleted() {
        testBook.setStockQuantity(2); // Exactly 2 in stock, cart wants 2 -> stock becomes 0

        when(customerRepository.findById(1L)).thenReturn(testCustomer);
        when(cartService.getCart(1L)).thenReturn(testCart);
        when(bookRepository.findById(1L)).thenReturn(testBook);
        when(bookRepository.update(any(Book.class))).thenReturn(testBook);
        when(orderRepository.create(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });
        doNothing().when(cartService).clearCart(1L);
        doNothing().when(emailNotificationService).sendOrderConfirmation(any(Order.class));
        doNothing().when(orderMessageProducer).sendStockDepletedMessage(any(Book.class));

        Order result = orderService.createOrderFromCart(1L, null, PaymentMethod.PAYPAL);

        assertNotNull(result);
        // Should use customer's default address when deliveryAddress is null
        assertEquals("123 Main St", result.getDeliveryAddress());
        verify(orderMessageProducer).sendStockDepletedMessage(any(Book.class));
    }

    @Test
    void testGetOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(testOrder);

        Order result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> orderService.getOrderById(999L));
    }

    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(testOrder));

        List<Order> result = orderService.getAllOrders();

        assertEquals(1, result.size());
    }

    @Test
    void testGetOrdersByCustomerId() {
        when(orderRepository.findByCustomerId(1L)).thenReturn(Collections.singletonList(testOrder));

        List<Order> result = orderService.getOrdersByCustomerId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testGetOrdersByStatus() {
        when(orderRepository.findByStatus(OrderStatus.PENDING))
                .thenReturn(Collections.singletonList(testOrder));

        List<Order> result = orderService.getOrdersByStatus(OrderStatus.PENDING);

        assertEquals(1, result.size());
    }

    @Test
    void testUpdateOrderStatus_Success() {
        when(orderRepository.findById(1L)).thenReturn(testOrder);
        when(orderRepository.update(any(Order.class))).thenReturn(testOrder);
        doNothing().when(emailNotificationService)
                .sendOrderStatusUpdate(any(Order.class), any(OrderStatus.class), any(OrderStatus.class));

        Order result = orderService.updateOrderStatus(1L, OrderStatus.SHIPPED);

        assertNotNull(result);
        verify(emailNotificationService).sendOrderStatusUpdate(
                any(Order.class), eq(OrderStatus.PENDING), eq(OrderStatus.SHIPPED));
    }

    @Test
    void testUpdateOrderStatus_OrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.updateOrderStatus(999L, OrderStatus.SHIPPED));
    }
}
