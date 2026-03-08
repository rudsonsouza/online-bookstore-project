package com.bookstore.service;

import com.bookstore.model.Order;
import com.bookstore.model.OrderStatus;

import javax.enterprise.context.ApplicationScoped;
import java.util.logging.Logger;

/**
 * Service for sending email notifications to customers.
 * Note: This is a stub implementation as per requirements (no actual SMTP server needed).
 * In production, this would integrate with JavaMail API and an SMTP server.
 */
@ApplicationScoped
public class EmailNotificationService {

    private static final Logger LOGGER = Logger.getLogger(EmailNotificationService.class.getName());

    /**
     * Sends an order confirmation email to the customer.
     */
    public void sendOrderConfirmation(Order order) {
        String customerEmail = order.getCustomer().getEmail();
        String subject = "Order Confirmation - Order #" + order.getId();
        String body = buildOrderConfirmationBody(order);

        sendEmail(customerEmail, subject, body);
    }

    /**
     * Sends an order status update email to the customer.
     */
    public void sendOrderStatusUpdate(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        String customerEmail = order.getCustomer().getEmail();
        String subject = "Order Status Update - Order #" + order.getId();
        String body = buildStatusUpdateBody(order, oldStatus, newStatus);

        sendEmail(customerEmail, subject, body);
    }

    /**
     * Stub email sending method.
     * Logs the email instead of actually sending it.
     */
    private void sendEmail(String to, String subject, String body) {
        LOGGER.info("========== EMAIL NOTIFICATION ==========");
        LOGGER.info("To: " + to);
        LOGGER.info("Subject: " + subject);
        LOGGER.info("Body: " + body);
        LOGGER.info("========================================");
    }

    private String buildOrderConfirmationBody(Order order) {
        return String.format(
                "Dear %s %s,\n\n" +
                "Thank you for your order!\n\n" +
                "Order #%d\n" +
                "Total Price: $%.2f\n" +
                "Price (excl. VAT): $%.2f\n" +
                "Payment Method: %s\n" +
                "Delivery Address: %s\n\n" +
                "Your order is being processed and you will receive another email when it ships.\n\n" +
                "Thank you for shopping with Online Bookstore!",
                order.getCustomer().getFirstName(),
                order.getCustomer().getLastName(),
                order.getId(),
                order.getTotalPrice(),
                order.getPriceWithoutVat(),
                order.getPaymentMethod(),
                order.getDeliveryAddress()
        );
    }

    private String buildStatusUpdateBody(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        return String.format(
                "Dear %s %s,\n\n" +
                "Your order #%d status has been updated.\n\n" +
                "Previous Status: %s\n" +
                "New Status: %s\n\n" +
                "Thank you for shopping with Online Bookstore!",
                order.getCustomer().getFirstName(),
                order.getCustomer().getLastName(),
                order.getId(),
                oldStatus,
                newStatus
        );
    }
}
