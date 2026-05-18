package app.persistence;

import app.entities.Order;
import app.entities.OrderStatus;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {
    ContactInfoMapper contactInfoMapper = new ContactInfoMapper();
    SpecificationMapper specificationMapper = new SpecificationMapper();
    WorkshopMapper workshopMapper = new WorkshopMapper();

    public void insertOrder(Order order, ConnectionPool connectionPool) throws DatabaseException {
        boolean hasWorkshop = order.getWorkshop().getId() != 0;
        String sql = "INSERT INTO orders (contact_info, specifications, workshop, remarks, status) " +
                "VALUES (?, ?, ? ,? ,?::order_status)";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            int contactId = contactInfoMapper.insertContactInfo(order.getContactInfo(), connectionPool);
            preparedStatement.setInt(1, contactId);
            int specId = specificationMapper.insertSpecifications(order.getSpecifications(), connectionPool);
            preparedStatement.setInt(2, specId);
            if (hasWorkshop) {
                int workshopId = workshopMapper.insertWorkshop(order.getWorkshop(), connectionPool);
                preparedStatement.setInt(3, workshopId);
            } else {
                preparedStatement.setNull(3, Types.INTEGER);
            }
            preparedStatement.setString(4, order.getOrderDetails().remark());
            preparedStatement.setString(5, String.valueOf(order.getOrderDetails().status()));
            preparedStatement.executeUpdate();

        } catch (DatabaseException e) {
            System.err.println("[OrderMapper.insertOrder] " + e.getMessage());
            throw e;
        } catch (SQLException e) {
            System.err.println("[OrderMapper.insertOrder]" + e.getMessage());
            throw new DatabaseException("Could not insert order");
        }

    }

    private OrderDetails getOrderDetailsById(int id, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM orders WHERE id=?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String remark = resultSet.getString("remarks");

                String stringStatus = resultSet.getString("status");

                OrderStatus status = OrderStatus.valueOf(stringStatus);

                return new OrderDetails(remark, status);
            } else throw new DatabaseException("Order not found");
        } catch (SQLException e) {
            System.err.println("[OrderMapper.getOrderDetailsById] " + e.getMessage());
            throw new DatabaseException("Something went wrong while getting order details");
        }
    }

    public Order getOrderById(Order order, ConnectionPool connectionPool) throws DatabaseException {

        return new Order.Builder()
                .contactInfo(contactInfoMapper.findContactInfoById(order.getContactInfo().getId(), connectionPool))
                .specifications(specificationMapper.findSpecificationsById(order.getSpecifications().getId(), connectionPool))
                .workshop(workshopMapper.findWorkshopById(order.getWorkshop().getId(), connectionPool))
                .orderDetails(getOrderDetailsById(order.getId(), connectionPool))
                .build();
    }

    public List<Order> getAllOrders(ConnectionPool connectionPool) throws DatabaseException {
        List<Order> orderList = new ArrayList<>();


        String sql = "SELECT * FROM orders";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                int contactInfo = resultSet.getInt(2);
                int specifications = resultSet.getInt(3);
                int workshop = resultSet.getInt(4);
                String remarks = resultSet.getString(5);
                String orderStatusString = resultSet.getString(6);

                OrderStatus orderStatus = OrderStatus.valueOf(orderStatusString);
                OrderDetails orderDetails = new OrderDetails(remarks, orderStatus);
                orderList.add(new Order.Builder()
                        .id(id)
                        .contactInfo(contactInfoMapper.findContactInfoById(contactInfo, connectionPool))
                        .specifications(specificationMapper.findSpecificationsById(specifications, connectionPool))
                        .workshop(workshopMapper.findWorkshopById(workshop, connectionPool))
                        .orderDetails(orderDetails)
                        .build());
            }
        } catch (SQLException e) {
            System.err.println("[OrderMapper.getAllOrders] " + e.getMessage());
            throw new DatabaseException("Something went wrong, try again later");
        }
        return orderList;
    }

    public static void changeOrderStatus(ConnectionPool connectionPool, OrderStatus status, Order order) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setObject(1, status.getDatabaseEnum());
            preparedStatement.setInt(2, order.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
