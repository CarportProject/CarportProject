package app.observer;

import app.entities.Order;
import app.entities.OrderStatus;
import jakarta.mail.MessagingException;

public interface OrderObserver {
    void update(Order order, OrderStatus orderStatus);
}
