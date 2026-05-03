package app.persistence;

import app.entities.Role;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.exceptions.UserNotFoundException;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public User findUserByEmail(String email, ConnectionPool connectionPool) throws DatabaseException, UserNotFoundException {

        String sql = "SELECT * FROM public.users WHERE email=?";
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

                Role role = Role.valueOf(roleColumn);

                return new User(email, role, userID, password);
            } else {
                throw new UserNotFoundException("No user found in findUserByEmail");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error in findUserByEmail: " + e.getMessage());
        }
    }

    public void insertUser(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO public.users (email, password) VALUES (?, ?)";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            throw new DatabaseException("Database error in insertUser: " + e.getMessage());
        }
    }
}
