package app.service;

import app.entities.Order;
import app.entities.OrderStatus;
import app.exceptions.DatabaseException;
import app.observer.CustomerEmailObserver;
import app.observer.OrderEvent;
import app.observer.OrderObserver;
import app.observer.SalesEmailObserver;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for order-related business logic,
 * including creation, cancellation, and notifying observers when order events occur.
 */
public class OrderService {

    private static final OrderMapper ORDER_MAPPER = new OrderMapper();

    private static final List<OrderObserver> orderObserverList = new ArrayList<>(
            List.of(new CustomerEmailObserver(), new SalesEmailObserver())
    );

    /**
     * Notifies all registered observers of an order event.
     *
     * @param order      the order the event relates to
     * @param orderEvent the type of event that occurred
     */
    private void notifyObservers(Order order, OrderEvent orderEvent) {
        for (OrderObserver observer : orderObserverList) {
            observer.update(order, orderEvent);
        }
    }

    /**
     * Persists a new order and notifies observers.
     * The order must be built with {@link OrderStatus#PENDING} before calling this method.
     *
     * @param order          the order to create
     * @param connectionPool the database connection pool
     * @throws DatabaseException if a database error occurs during the insert
     */
    public void createOrder(Order order, ConnectionPool connectionPool) throws DatabaseException {
        ORDER_MAPPER.insertOrder(order, connectionPool);
        notifyObservers(order, OrderEvent.ORDER_CREATED);
    }

    /**
     * Cancels an existing order and notifies observers.
     * The caller is responsible for updating the order's status in the database beforehand.
     *
     * @param order          the order to cancel
     * @param connectionPool the database connection pool
     */
    public void cancelOrder(Order order, ConnectionPool connectionPool) {
        notifyObservers(order, OrderEvent.ORDER_CANCELLED);
    }
}
