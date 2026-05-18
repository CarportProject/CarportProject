package app.persistence;

import app.entities.Order;
import app.entities.Status;
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

            int contactId = contactInfoMapper.insertContactInfo(order.getCustomer(), connectionPool);
            preparedStatement.setInt(1, contactId);
            int specId = specificationMapper.insertSpecifications(order.getSpecifications(), connectionPool);
            preparedStatement.setInt(2, specId);
            if (hasWorkshop) {
                int workshopId = workshopMapper.insertWorkshop(order.getWorkshop(), connectionPool);
                preparedStatement.setInt(3, workshopId);
            } else { preparedStatement.setNull(3, Types.INTEGER);}
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

                Status status = Status.valueOf(stringStatus);

                return new OrderDetails(remark, status);
            } else throw new DatabaseException("Order not found");
        } catch (SQLException e) {
            System.err.println("[OrderMapper.getOrderDetailsById] " + e.getMessage());
            throw new DatabaseException("Something went wrong while getting order details");
        }
    }

    public Order getOrderById(Order order, ConnectionPool connectionPool) throws DatabaseException {

        return new Order.Builder()
                .contactInfo(contactInfoMapper.findContactInfoById(order.getCustomer().getId(), connectionPool))
                .specifications(specificationMapper.findSpecificationsById(order.getSpecifications().getId(), connectionPool))
                .workshop(workshopMapper.findWorkshopById(order.getWorkshop().getId(), connectionPool))
                .orderDetails(getOrderDetailsById(order.getId(), connectionPool))
                .build();
    }
}
