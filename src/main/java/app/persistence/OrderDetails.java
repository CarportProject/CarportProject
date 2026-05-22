package app.persistence;

import app.entities.OrderStatus;

import java.util.UUID;

public record OrderDetails(String remark, OrderStatus status, Double price, UUID uuid) {

    public OrderDetails(String remark, OrderStatus status) {
        this(remark, status, null, null);
    }

    public OrderDetails(Double price) {
        this(null, null, price, null);
    }
    public OrderDetails(UUID uuid){
        this(null, null, null, uuid);
    }
}
