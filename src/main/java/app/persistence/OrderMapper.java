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

    public void insertOrder(Order order, ConnectionPool connectionPool) {

        try {
            contactInfoMapper.insertContactInfo(order.getCustomer(), connectionPool);
            specificationMapper.insertSpecifications(order.getSpecifications(), connectionPool);
            workshopMapper.insertWorkshop(order.getWorkshop(), connectionPool);
            insertRemark(order, connectionPool);
        } catch (DatabaseException e) {
            //TODO Throw up the chain to give user errorMsg
            e.getMessage();
        }

    }


    private void insertRemark(Order order, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "INSERT INTO order (remarks) VALUES (?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[OrderMapper.insertRemark] " + e.getMessage());
            throw new DatabaseException("An error occurred while adding remarks to order");
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
