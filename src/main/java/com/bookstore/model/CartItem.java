package com.bookstore.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents an item in a shopping cart.
 * Cart items are stored in Redis, not in the database.
 */
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long bookId;
    private String bookName;
    private Double price;
    private Integer quantity;

    public CartItem() {
    }

    public CartItem(Long bookId, String bookName, Double price, Integer quantity) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getSubtotal() {
        return price * quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(bookId, cartItem.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId);
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "bookId=" + bookId +
                ", bookName='" + bookName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
