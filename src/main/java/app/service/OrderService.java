package app.service;

import app.entities.Order;
import app.entities.OrderStatus;
import app.exceptions.DatabaseException;
import app.observer.CustomerEmailObserver;
import app.observer.OrderObserver;
import app.observer.SalesEmailObserver;
import app.persistence.ConnectionPool;
import app.persistence.MaterialsMapper;
import app.entities.OrderDetails;
import app.persistence.OrderMapper;
import jakarta.mail.MessagingException;

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
    private static void notifyObservers(Order order, OrderStatus orderStatus, ConnectionPool connectionPool) throws MessagingException {
        for (OrderObserver observer : orderObserverList) {
            observer.update(order, orderStatus, connectionPool);
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
    public static void createOrder(Order order, MaterialService materialService, ConnectionPool connectionPool) throws DatabaseException, MessagingException {

        MaterialsMapper materialsMapper = new MaterialsMapper();

        int orderId = OrderMapper.insertOrder(order, connectionPool);

        order.setId(orderId);

        materialService.finalizeOrder(orderId, order.getSpecifications(), connectionPool);

        Double orderPrice = materialService.getMaterialListCost(materialsMapper.findMaterialListById(orderId, connectionPool));

        OrderDetails orderDetails = new OrderDetails(null, null, orderPrice, null);

        OrderMapper.changeOrderDetails(orderId, orderDetails, connectionPool);

        changeOrderStatus(order, OrderStatus.PENDING, connectionPool);
    }


    public static void changeOrderStatus(Order order, OrderStatus orderStatus, ConnectionPool connectionPool) throws MessagingException, DatabaseException {
        OrderMapper.changeOrderStatus(connectionPool, orderStatus, order);
        notifyObservers(order, orderStatus, connectionPool);
    }
}
