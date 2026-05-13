package app.persistence;

import app.entities.ContactInfo;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Repository class responsible for all database operations related to customers.
 * Communicates directly with the {@code contact_info} table.
 */
public class ContactInfoMapper {

    /**
     * Inserts a new contactInfo into the {@code contact_info} table.
     *
     * @param contactInfo       the contactInfo to insert
     * @param connectionPool the database connection pool
     * @throws DatabaseException if a SQL error occurs during the insert
     */
    public void insertContactInfo(ContactInfo contactInfo, ConnectionPool connectionPool) throws DatabaseException {

        String email = contactInfo.getEmail();
        String address = contactInfo.getAddress();
        String firstname = contactInfo.getFirstName();
        String lastname = contactInfo.getLastName();
        int postalCode = contactInfo.getPostalCode();
        String city = contactInfo.getCity();
        String phoneNumber = contactInfo.getPhoneNumber();

        String sql = "INSERT INTO contact_info (email, address, firstname, lastname, postal_code, city, phone_number) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, email);
            ps.setString(2, address);
            ps.setString(3, firstname);
            ps.setString(4, lastname);
            ps.setInt(5, postalCode);
            ps.setString(6, city);
            ps.setString(7, phoneNumber);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ContactInfoMapper.insertCustomer] " + e.getMessage());
            throw new DatabaseException("An error occurred while creating the contactInfo");
        }
    }

    ContactInfo getCustomerById(int id) {
        // TODO: implement customer lookup by ID
        return null;
    }
}
