package app.persistence;

import app.entities.Material;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialsMapper {

    public List<Material> findMaterialListById(int orderId, ConnectionPool connectionPool)
            throws DatabaseException {

        List<Material> materialList = new ArrayList<>();

        String sql = "SELECT * FROM material_list WHERE order_id=?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, orderId);

            ResultSet resultSet = preparedStatement.executeQuery();


            while (resultSet.next()) {
                Material material = new Material.Builder()
                    .id(orderId)
                    .name(resultSet.getString("name"))
                    .description(resultSet.getString("description"))
                    .price(resultSet.getInt("price_per_m"))
                    .width(resultSet.getInt("width_mm"))
                    .height(resultSet.getInt("height_mm"))
                    .build();

                materialList.add(material);
            }

            return materialList;

        } catch (SQLException e) {
            System.err.println("[materialsMapper.findMaterialListById] " + e.getMessage());
            throw new DatabaseException("An error occurred while fetching material list");
        }
    }
}