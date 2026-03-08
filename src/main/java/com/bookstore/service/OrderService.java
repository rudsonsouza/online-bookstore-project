package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.model.Cart;
import com.bookstore.model.CartItem;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.model.OrderItem;
import com.bookstore.model.OrderStatus;
import com.bookstore.model.PaymentMethod;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CustomerRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.jms.OrderMessageProducer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class OrderService {

    private static final Logger LOGGER = Logger.getLogger(OrderService.class.getName());
    private static final double VAT_RATE = 0.20; // 20% VAT

    @EJB
    private OrderRepository orderRepository;

    @EJB
    private CustomerRepository customerRepository;

    @EJB
    private BookRepository bookRepository;

    @Inject
    private CartService cartService;

    @Inject
    private EmailNotificationService emailNotificationService;

    @Inject
    private OrderMessageProducer orderMessageProducer;

    /**
     * Creates an order from the customer's cart.
     */
    public Order createOrderFromCart(Long customerId, String deliveryAddress, PaymentMethod paymentMethod) {
        Customer customer = customerRepository.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found with id: " + customerId);
        }

        Cart cart = cartService.getCart(customerId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty for customer: " + customerId);
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setDeliveryAddress(deliveryAddress != null ? deliveryAddress : customer.getAddress());
        order.setPaymentMethod(paymentMethod);
        order.setOrderStatus(OrderStatus.PENDING);

        double totalPrice = 0;
        for (CartItem cartItem : cart.getItems()) {
            Book book = bookRepository.findById(cartItem.getBookId());
            if (book == null) {
                throw new IllegalArgumentException("Book not found with id: " + cartItem.getBookId());
            }

            if (book.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for book: " + book.getName());
            }

            OrderItem orderItem = new OrderItem(book, cartItem.getQuantity(), book.getPrice());
            order.addItem(orderItem);
            totalPrice += orderItem.getSubtotal();

            // Reduce stock
            book.setStockQuantity(book.getStockQuantity() - cartItem.getQuantity());
            bookRepository.update(book);

            // Check if stock is depleted and send warehouse notification
            if (book.getStockQuantity() <= 0) {
                LOGGER.info("Book out of stock: " + book.getName() + ". Sending warehouse notification.");
                orderMessageProducer.sendStockDepletedMessage(book);
            }
        }

        double priceWithoutVat = totalPrice / (1 + VAT_RATE);
        order.setTotalPrice(Math.round(totalPrice * 100.0) / 100.0);
        order.setPriceWithoutVat(Math.round(priceWithoutVat * 100.0) / 100.0);

        Order savedOrder = orderRepository.create(order);

        // Clear the customer's cart after order creation
        cartService.clearCart(customerId);

        // Send order confirmation email
        emailNotificationService.sendOrderConfirmation(savedOrder);

        LOGGER.info("Order created: " + savedOrder.getId() + " for customer: " + customer.getEmail());
        return savedOrder;
    }

    public Order getOrderById(Long id) {
        Order order = orderRepository.findById(id);
        if (order == null) {
            throw new IllegalArgumentException("Order not found with id: " + id);
        }
        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Updates order status and sends email notification to customer.
     */
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found with id: " + orderId);
        }

        OrderStatus oldStatus = order.getOrderStatus();
        order.setOrderStatus(newStatus);
        Order updatedOrder = orderRepository.update(order);

        // Send email notification about status change
        emailNotificationService.sendOrderStatusUpdate(updatedOrder, oldStatus, newStatus);

        LOGGER.info("Order " + orderId + " status changed from " + oldStatus + " to " + newStatus);
        return updatedOrder;
    }

    // Setters for testing
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public void setEmailNotificationService(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    public void setOrderMessageProducer(OrderMessageProducer orderMessageProducer) {
        this.orderMessageProducer = orderMessageProducer;
    }
}
