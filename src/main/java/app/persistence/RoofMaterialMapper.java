package app.persistence;

import app.entities.RoofMaterial;
import app.entities.RoofType;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class responsible for all database operations related to roof materials.
 * Communicates directly with the {@code roof_material} table.
 */
public class RoofMaterialMapper {

    /**
     * Retrieves all available roof materials from the database.
     * Each row is mapped to a {@link RoofMaterial} object, including conversion
     * of the stored roof type string to the {@link RoofType} enum.
     *
     * @param connectionPool the database connection pool
     * @return a list of all {@link RoofMaterial} objects in the database
     * @throws DatabaseException if a SQL error occurs, or if an unknown {@link RoofType} value is found in the database
     */
    public List<RoofMaterial> getAllRoofMaterial(ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM roof_material";
        List<RoofMaterial> roofMaterials = new ArrayList<>();
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {

            ResultSet resultSet = preparedStatement.executeQuery();
            // ResultSet closes automatically when PreparedStatement is closed with try-with-resources
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String color = resultSet.getString("color");
                int price = resultSet.getInt("price_per_m2");
                String roofType = resultSet.getString("roof_type");

                // Convert the roof type string from the database to the RoofType enum
                RoofType roof = null;
                try {
                    roof = RoofType.valueOf(roofType);
                } catch (IllegalArgumentException e) {
                    System.err.println("Unknown roof type in database : " + roofType);
                    throw new DatabaseException("An error occurred while fetching the roof material");
                }
                roofMaterials.add(new RoofMaterial.Builder()
                        .id(id)
                        .name(name)
                        .color(color)
                        .price(price)
                        .roofType(roof)
                        .build());


            }
            return roofMaterials;

        } catch (SQLException e) {
            System.err.println("[RoofMaterialMapper.getAllRoofMaterial] " + e.getMessage());
            throw new DatabaseException("An error occurred while fetching the roof material");
        }

    }
}
