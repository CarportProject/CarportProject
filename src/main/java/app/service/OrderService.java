package app.service;

import app.entities.Order;
import app.entities.OrderStatus;
import app.exceptions.DatabaseException;
import app.observer.CustomerEmailObserver;
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
     * @param order       the order the event relates to
     * @param orderStatus the type of event that occurred
     */
    private static void notifyObservers(Order order, OrderStatus orderStatus) {
        for (OrderObserver observer : orderObserverList) {
            observer.update(order, orderStatus);
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
    public static void createOrder(Order order, ConnectionPool connectionPool) throws DatabaseException {
        ORDER_MAPPER.insertOrder(order, connectionPool);
        notifyObservers(order, OrderStatus.PENDING);
    }

    /**
     * Cancels an existing order and notifies observers.
     * The caller is responsible for updating the order's status in the database beforehand.
     *
     * @param order          the order to cancel
     * @param connectionPool the database connection pool
     */
    public static void cancelOrder(Order order, ConnectionPool connectionPool) {
        OrderMapper.changeOrderStatus(connectionPool, OrderStatus.CANCELLED, order);
        notifyObservers(order, OrderStatus.CANCELLED);
    }

    public static void rejectOrder(Order order, ConnectionPool connectionPool) {
        OrderMapper.changeOrderStatus(connectionPool, OrderStatus.REJECTED, order);
        notifyObservers(order, OrderStatus.REJECTED);
    }

    public static void acceptOrder(Order order, ConnectionPool connectionPool) {
        OrderMapper.changeOrderStatus(connectionPool, OrderStatus.OFFER_SENT, order);
        notifyObservers(order, OrderStatus.OFFER_SENT);
    }
}
