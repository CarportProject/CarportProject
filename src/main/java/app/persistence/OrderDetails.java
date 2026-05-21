package app.persistence;

import app.entities.OrderStatus;

public record OrderDetails(String remark, OrderStatus status, Integer price) {

    public OrderDetails(String remark, OrderStatus status) {
        this(remark, status, null);
    }
}
