package app.persistence;

import app.entities.Order;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Repository class responsible for all database operations related to orders.
 * Orchestrates inserts across {@code contact_info}, {@code specifications},
 * {@code workshop} and {@code "order"} tables.
 */
public class OrderMapper {

    private final ContactInfoMapper contactInfoMapper = new ContactInfoMapper();
    private final SpecificationMapper specificationMapper = new SpecificationMapper();
    private final WorkshopMapper workshopMapper = new WorkshopMapper();

    /**
     * Persists a complete order by inserting all related sub-entities
     * and then the order record itself.
     * If a workshop is not present on the order, that insert is skipped.
     *
     * @param order          the order to persist
     * @param connectionPool the database connection pool
     * @throws DatabaseException if any database operation fails
     */
    public void insertOrder(Order order, ConnectionPool connectionPool) throws DatabaseException {
        contactInfoMapper.insertContactInfo(order.getCustomer(), connectionPool);
        specificationMapper.insertSpecifications(order.getSpecifications(), connectionPool);

        // Only insert workshop if one was requested
        if (order.getWorkshop() != null) {
            workshopMapper.insertWorkshop(order.getWorkshop(), connectionPool);
        }

        insertOrderRecord(order, connectionPool);
    }

    /**
     * Inserts the core order record — remarks and status — into the {@code "order"} table.
     *
     * @param order          the order whose record to insert
     * @param connectionPool the database connection pool
     * @throws DatabaseException if a SQL error occurs during the insert
     */
    private void insertOrderRecord(Order order, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO orders (remarks, order_status) VALUES (?, ?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, order.getRemarks());
            preparedStatement.setString(2, order.getOrderStatus().name());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[OrderMapper.insertOrderRecord] " + e.getMessage());
            throw new DatabaseException("An error occurred while creating the order");
        }
    }
}
