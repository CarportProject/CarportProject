package app.persistence;

import app.entities.RoofStyle;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoofStyleMapper {

    public RoofStyle findRoofStyleById(int id, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "SELECT * FROM roof_style WHERE id=?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                return new RoofStyle.Builder()
                    .id(id)
                    .name(resultSet.getString("name"))
                    .color(resultSet.getString("color"))
                    .price(resultSet.getInt("price_per_m2"))
                    .build();
            } else {
                System.err.println("[RoofStyleMapper.findById] ");
                throw new DatabaseException("Roof style not found");
            }

        } catch (SQLException e) {
            System.err.println("[RoofStyleMapper.findById] " + e.getMessage());
            throw new DatabaseException("An error occurred while fetching roof style");
        }
    }
}
