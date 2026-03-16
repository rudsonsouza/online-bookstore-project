package com.bookstore.rest.dto;

import com.bookstore.model.PaymentMethod;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * DTO for creating an order from a customer's cart.
 */
public class CreateOrderRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private String deliveryAddress;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(Long customerId, String deliveryAddress, PaymentMethod paymentMethod) {
        this.customerId = customerId;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
