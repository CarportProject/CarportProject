package app.persistence;

import app.entities.Order;
import app.entities.OrderDetails;
import app.entities.OrderStatus;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderMapper {
    static ContactInfoMapper contactInfoMapper = new ContactInfoMapper();
    static SpecificationMapper specificationMapper = new SpecificationMapper();
    static WorkshopMapper workshopMapper = new WorkshopMapper();

    public static int insertOrder(Order order, ConnectionPool connectionPool) throws DatabaseException {
        boolean hasWorkshop = order.getWorkshop().getId() != 0;
        String sql = "INSERT INTO orders (contact_info, specifications, workshop, remarks, status) " +
                "VALUES (?, ?, ? ,? ,?)";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
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
            preparedStatement.setObject(5, order.getOrderDetails().status().getDatabaseEnum());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            System.err.println("[OrderMapper.insertOrder]");
            throw new DatabaseException("Could not get generated keys");

        } catch (DatabaseException e) {
            System.err.println("[OrderMapper.insertOrder] " + e.getMessage());
            throw e;
        } catch (SQLException e) {
            System.err.println("[OrderMapper.insertOrder]" + e.getMessage());
            throw new DatabaseException("Could not insert order");
        }

    }

    public static Order getOrderById(int id, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM orders WHERE id=?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                int contactInfoId = resultSet.getInt("contact_info");
                int specificationsId = resultSet.getInt("specifications");
                int workshopId = resultSet.getInt("workshop");

                String remark = resultSet.getString("remarks");
                String stringStatus = resultSet.getString("status");

                double price = 0.0;

                if (resultSet.getDouble("price") != 0.0) {
                    price = resultSet.getDouble("price");
                }
                String uuidString = resultSet.getString("uuid");

                OrderStatus status = OrderStatus.valueOf(stringStatus);

                UUID uuid = uuidString != null ? UUID.fromString(uuidString) : null;

                return new Order.Builder()
                        .id(id)
                        .contactInfo(contactInfoMapper.findContactInfoById(contactInfoId, connectionPool))
                        .specifications(specificationMapper.findSpecificationsById(specificationsId, connectionPool))
                        .workshop(workshopMapper.findWorkshopById(workshopId, connectionPool))
                        .orderDetails(new OrderDetails(remark, status, price, uuid))
                        .build();
            } else throw new DatabaseException("Order not found");
        } catch (SQLException e) {
            System.err.println("[OrderMapper.getOrderById] " + e.getMessage());
            throw new DatabaseException("Something went wrong while getting order details");
        }
    }

    public static List<Order> getAllOrders(ConnectionPool connectionPool) throws DatabaseException {
        List<Order> orderList = new ArrayList<>();


        String sql = "SELECT * FROM orders ORDER BY id DESC";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int contactInfo = resultSet.getInt("contact_info");
                int specifications = resultSet.getInt("specifications");
                int workshop = resultSet.getInt("workshop");
                String remarks = resultSet.getString("remarks");
                String orderStatusString = resultSet.getString("status");
                double rawPrice = resultSet.getDouble("price");
                Double price = resultSet.wasNull() ? null : rawPrice;

                OrderStatus orderStatus = OrderStatus.valueOf(orderStatusString);
                OrderDetails orderDetails = new OrderDetails(remarks, orderStatus, price, null);
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

    public static void changeOrderStatus(ConnectionPool connectionPool, OrderStatus status, Order order) throws DatabaseException {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setObject(1, status.getDatabaseEnum());
            preparedStatement.setInt(2, order.getId());
            preparedStatement.executeUpdate();
            System.err.println("Updating order id: " + order.getId() + " to status: " + status);
        } catch (SQLException e) {
            System.err.println("[OrderMapper.changeOrderStatus] " + e.getMessage());
            throw new DatabaseException("Could not update orderstatus");
        }
    }

    public static void changeOrderDetails(int orderId, OrderDetails orderDetails, ConnectionPool connectionPool) throws DatabaseException {
        StringBuilder sql = new StringBuilder("UPDATE orders SET ");
        List<Object> params = new ArrayList<>();
        if (orderDetails.remark() != null) {
            sql.append("remark = ?, ");
            params.add(orderDetails.remark());
        }
        if (orderDetails.price() != null) {
            sql.append("price = ?, ");
            params.add(orderDetails.price());
        }
        if (orderDetails.uuid() != null) {
            sql.append("uuid = ?, ");
            params.add(orderDetails.uuid().toString());
        }
        if (params.isEmpty()) {
            throw new DatabaseException("No fields to update for order with id " + orderId);
        }
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        params.add(orderId);
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())
        ) {


            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[OrderMapper.changeOrderDetails]");
            throw new DatabaseException("Something went wrong");
        }
    }

    public static Order getOrderByUuid(UUID uuid, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM orders WHERE uuid = ?";
        String uuidString = uuid.toString();
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, uuidString);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {

                int id = resultSet.getInt("id");
                int contactInfoId = resultSet.getInt("contact_info");
                int specificationId = resultSet.getInt("specifications");
                int workshopId = resultSet.getInt("workshop");

                String remark = resultSet.getString("remarks");
                String status = resultSet.getString("status");

                double price = resultSet.getDouble("price");

                OrderStatus orderStatus = OrderStatus.valueOf(status);

                return new Order.Builder()
                        .id(id)
                        .contactInfo(contactInfoMapper.findContactInfoById(contactInfoId, connectionPool))
                        .specifications(specificationMapper.findSpecificationsById(specificationId, connectionPool))
                        .workshop(workshopMapper.findWorkshopById(workshopId, connectionPool))
                        .orderDetails(new OrderDetails(remark, orderStatus, price, uuid))
                        .build();
            }
        } catch (SQLException | DatabaseException e) {
            System.err.println("[OrderMapper] " + e.getMessage());
            throw new DatabaseException("Something went wrong");
        }

        return null;
    }
}
