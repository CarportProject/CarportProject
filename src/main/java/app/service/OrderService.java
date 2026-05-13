package app.service;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.observer.CustomerEmailObserver;
import app.observer.OrderEvent;
import app.observer.OrderObserver;
import app.observer.SalesEmailObserver;
import app.persistence.ConnectionPool;

import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private static final List<OrderObserver> orderObserverList = new ArrayList<>(
            List.of(new CustomerEmailObserver(), new SalesEmailObserver())
    );

    private void notifyObservers(Order order, OrderEvent orderEvent) {
        for (OrderObserver observer : orderObserverList) {
            observer.update(order, orderEvent);
        }
    }

    public void createOrder(Order order, ConnectionPool connectionPool) throws DatabaseException {
        //TODO create orderMapper
        //orderMapper.insertOrder(order, connectionPool);
        notifyObservers(order, OrderEvent.ORDER_CREATED);
    }
    public void cancelOrder(Order order, ConnectionPool connectionPool){

        notifyObservers(order, OrderEvent.ORDER_CANCELLED);
    }
}
