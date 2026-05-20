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

    public List<Material> getAllMaterials(ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM materials";
        List<Material> materialList = new ArrayList<>();
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                materialList.add(
                        new Material.Builder()
                                .id(resultSet.getInt("id"))
                                .name(resultSet.getString("name"))
                                .description(resultSet.getString("description"))
                                .price(resultSet.getInt("price_per_m"))
                                .width(resultSet.getInt("width_mm"))
                                .height(resultSet.getInt("heigth_mm"))
                                .build()
                );
            }
            return materialList;
        } catch (SQLException e) {
            System.err.println("[MaterialsMapper.getAllMaterials] " + e.getMessage());
            throw new DatabaseException("Something went wrong");
        }
    }

    public void updateMaterialInfo(Material material, ConnectionPool connectionPool) throws DatabaseException {
        StringBuilder sql = new StringBuilder("UPDATE materials SET ");
        List<Object> params = new ArrayList<>();

        if (null != material.getName()) {
            sql.append("name = ?, ");
            params.add(material.getName());
        }
        if (null != material.getDescription()) {
            sql.append("description = ?, ");
            params.add(material.getDescription());
        }
        if (1 >= material.getPrice()) {
            sql.append("price_per_m = ?, ");
            params.add(material.getPrice());
        }
        if (1 >= material.getWidth()) {
            sql.append("width_mm = ?, ");
            params.add(material.getWidth());
        }
        if (1 >= material.getHeight()) {
            sql.append("height_mm = ?, ");
            params.add(material.getHeight());
        }

        // Removes the space and comma
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");

        params.add(material.getId());

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())
        ) {
            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[MaterialsMapper.updateMaterialInfo] " + e.getMessage());
            throw new DatabaseException("Could not update material");
        }
    }
}