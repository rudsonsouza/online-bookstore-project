package com.bookstore.rest.dto;

import com.bookstore.model.OrderStatus;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * DTO for updating an order's status.
 */
public class UpdateOrderStatusRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Order status is required")
    private OrderStatus status;

    public UpdateOrderStatusRequest() {
    }

    public UpdateOrderStatusRequest(OrderStatus status) {
        this.status = status;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
