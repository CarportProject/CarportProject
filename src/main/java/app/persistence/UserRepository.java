package app.persistence;

import app.entities.Role;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.exceptions.UserNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repository class responsible for all database operations related to users.
 * Communicates directly with the {@code public.users} table.
 */
public class UserRepository {

    /**
     * Looks up a user in the database by their email address.
     *
     * @param email          the email address to search for
     * @param connectionPool the database connection pool
     * @return the matching {@link User}
     * @throws UserNotFoundException if no user with the given email exists
     * @throws DatabaseException     if a SQL error occurs during the query
     */
    public User findUserByEmail(String email, ConnectionPool connectionPool) throws DatabaseException, UserNotFoundException {

        String sql = "SELECT * FROM users WHERE email=?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userID = resultSet.getInt("user_id");
                String password = resultSet.getString("password");
                String roleColumn = resultSet.getString("role");

                // Convert the role string from the database to the Role enum
                Role role = Role.valueOf(roleColumn);

                return new User(email, role, userID, password);
            } else {
                throw new UserNotFoundException("No user found in findUserByEmail");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error in findUserByEmail: " + e.getMessage());
        }
    }

    /**
     * Inserts a new user into the database with the given email and
     * already-hashed password. The role is assigned by the database default.
     *
     * @param email          the new user's email address
     * @param password       the BCrypt-hashed password
     * @param connectionPool the database connection pool
     * @throws DatabaseException if a SQL error occurs during the insert
     */
    public void insertUser(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO users (email, password) VALUES (?, ?)";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Database error in insertUser: " + e.getMessage());
        }
    }
}
