package app.persistence;

import app.entities.OrderStatus;

public record OrderDetails(String remark, OrderStatus status) {
}
