package com.bookstore.rest.dto;

import java.io.Serializable;

/**
 * DTO for adding an item to the cart.
 */
public class AddToCartRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long bookId;
    private Integer quantity;

    public AddToCartRequest() {
    }

    public AddToCartRequest(Long bookId, Integer quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
