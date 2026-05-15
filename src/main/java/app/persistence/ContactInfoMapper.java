package app.persistence;

import app.entities.ContactInfo;
import app.exceptions.DatabaseException;

import java.sql.*;

/**
 * Repository class responsible for all database operations related to customers.
 * Communicates directly with the {@code contact_info} table.
 */
public class ContactInfoMapper {

    /**
     * Inserts a new contactInfo into the {@code contact_info} table.
     *
     * @param contactInfo    the contactInfo to insert
     * @param connectionPool the database connection pool
     * @return
     * @throws DatabaseException if a SQL error occurs during the insert
     */
    public int insertContactInfo(ContactInfo contactInfo, ConnectionPool connectionPool) throws DatabaseException {

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
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, firstname);
            preparedStatement.setString(4, lastname);
            preparedStatement.setInt(5, postalCode);
            preparedStatement.setString(6, city);
            preparedStatement.setString(7, phoneNumber);
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next()){
                return resultSet.getInt(1);
            }
            throw new DatabaseException("Could not fetch generated id");
        } catch (SQLException e) {
            System.err.println("[ContactInfoMapper.insertCustomer] " + e.getMessage());
            throw new DatabaseException("An error occurred while creating the contactInfo");
        }
    }


    public ContactInfo findContactInfoById(int id, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "SELECT * FROM contact_info WHERE id=?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                return new ContactInfo.Builder()
                        .id(id)
                        .email(resultSet.getString("email"))
                        .address(resultSet.getString("address"))
                        .firstName(resultSet.getString("firstname"))
                        .lastName(resultSet.getString("lastname"))
                        .postalCode(resultSet.getInt("postal_code"))
                        .city(resultSet.getString("city"))
                        .phoneNumber(resultSet.getString("phone_number"))
                        .build();
            } else {
                System.err.println("[ContactInfoMapper.findById] ");
                throw new DatabaseException("No record found with id " + id);
            }

        } catch (SQLException e) {

            System.err.println("[ContactInfoMapper.findById] " + e.getMessage());

            throw new DatabaseException("An error occurred while fetching contact info " + e.getMessage());
        }
    }
}
