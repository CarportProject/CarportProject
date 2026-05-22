package app.persistence;

import app.entities.OrderStatus;

public record OrderDetails(String remark, OrderStatus status, Double price) {

    public OrderDetails(String remark, OrderStatus status) {
        this(remark, status, null);
    }

    public OrderDetails(Double price) {
        this(null, null, price);
    }
}
