package app.persistence;

import app.entities.Order;
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
    }}

   /* public Order getOrderById(int id) {
        String remark = getRemarkById(id);
        return new Order.Builder()
                .contactInfo(contactInfo)
                .specifications(specs)
                .build();
    }
} */
