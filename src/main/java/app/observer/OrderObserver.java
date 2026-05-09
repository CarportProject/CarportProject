package app.observer;

import app.entities.Order;
import jakarta.mail.MessagingException;

public interface OrderObserver {
    void update(Order order, OrderEvent orderEvent);
}
