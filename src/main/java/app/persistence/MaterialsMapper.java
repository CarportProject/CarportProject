package app.persistence;

import app.entities.Material;
import app.entities.MaterialListEntry;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialsMapper {

    /**
     * Fetches all materials associated with a specific order from the {@code material_list} table.
     *
     * @param orderId        the ID of the order whose materials should be fetched
     * @param connectionPool the database connection pool
     * @return a list of {@link MaterialListEntry} objects belonging to the order; empty if none found
     * @throws DatabaseException if a SQL error occurs during the query
     */
    public List<MaterialListEntry> findMaterialListById(int orderId, ConnectionPool connectionPool)
            throws DatabaseException {

        List<MaterialListEntry> materialList = new ArrayList<>();

        String sql = "SELECT * FROM material_list WHERE order_id = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int materialId = resultSet.getInt("material_id");
                int amount = resultSet.getInt("amount");
                int sameOrderId = resultSet.getInt("order_id");
                String description = resultSet.getString("description");
                MaterialListEntry materialListEntry = new MaterialListEntry(getMaterialById(materialId, connectionPool), amount, sameOrderId, description);

                materialList.add(materialListEntry);
            }

            return materialList;

        } catch (SQLException e) {
            System.err.println("[MaterialsMapper.findMaterialListById] " + e.getMessage());
            throw new DatabaseException("An error occurred while fetching material list");
        }
    }

    /**
     * Fetches a single material from the {@code materials} table by its ID.
     *
     * @param id             the ID of the material to fetch
     * @param connectionPool the database connection pool
     * @return the {@link Material} with the given ID
     * @throws DatabaseException if no material is found with the given ID, or if a SQL error occurs
     */
    public Material getMaterialById(int id, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM materials WHERE id = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Material.Builder()
                        .id(resultSet.getInt("id"))
                        .name(resultSet.getString("name"))
                        .description(resultSet.getString("description"))
                        .price(resultSet.getDouble("price_per_m"))
                        .widthMm(resultSet.getInt("width_mm"))
                        .heightMm(resultSet.getInt("height_mm"))
                        .build();
            }

            throw new DatabaseException("No material found with id " + id);

        } catch (SQLException e) {
            System.err.println("[MaterialsMapper.getMaterialById] " + e.getMessage());
            throw new DatabaseException("An error occurred while fetching material with id " + id);
        }
    }

    /**
     * Fetches all materials from the {@code materials} table.
     *
     * @param connectionPool the database connection pool
     * @return a list of all {@link Material} objects; empty if none exist
     * @throws DatabaseException if a SQL error occurs during the query
     */
    public List<Material> getAllMaterials(ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM materials";
        List<Material> materialList = new ArrayList<>();

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                materialList.add(new Material.Builder()
                        .id(resultSet.getInt("id"))
                        .name(resultSet.getString("name"))
                        .description(resultSet.getString("description"))
                        .price(resultSet.getInt("price_per_m"))
                        .widthMm(resultSet.getInt("width_mm"))
                        .heightMm(resultSet.getInt("height_mm"))
                        .build()
                );
            }

            return materialList;

        } catch (SQLException e) {
            System.err.println("[MaterialsMapper.getAllMaterials] " + e.getMessage());
            throw new DatabaseException("An error occurred while fetching all materials");
        }
    }

    /**
     * Updates one or more fields on an existing material in the {@code materials} table.
     * Only non-null string fields and positive numeric fields are included in the update.
     * At least one field must be set, otherwise this method throws a {@link DatabaseException}.
     *
     * @param material       the material whose fields should be updated; must have a valid {@code id}
     * @param connectionPool the database connection pool
     * @throws DatabaseException if no fields are set for update, or if a SQL error occurs
     */
    public void updateMaterialInfo(Material material, ConnectionPool connectionPool) throws DatabaseException {
        StringBuilder sql = new StringBuilder("UPDATE materials SET ");
        List<Object> params = new ArrayList<>();

        if (material.getName() != null) {
            sql.append("name = ?, ");
            params.add(material.getName());
        }
        if (material.getDescription() != null) {
            sql.append("description = ?, ");
            params.add(material.getDescription());
        }
        if (material.getPrice() > 0) {
            sql.append("price_per_m = ?, ");
            params.add(material.getPrice());
        }
        if (material.getWidth() > 0) {
            sql.append("width_mm = ?, ");
            params.add(material.getWidth());
        }
        if (material.getHeight() > 0) {
            sql.append("height_mm = ?, ");
            params.add(material.getHeight());
        }

        if (params.isEmpty()) {
            throw new DatabaseException("No fields to update for material with id " + material.getId());
        }

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
            System.err.println("[MaterialsMapper.updateMaterialInfo] " + e.getMessage());
            throw new DatabaseException("Could not update material with id " + material.getId());
        }
    }

    /**
     * Inserts a row into the {@code material_list} table, linking a material to an order.
     *
     * @param orderId        the ID of the order the material belongs to
     * @param materialId     the ID of the material to associate with the order
     * @param amount         the quantity of the material required
     * @param description    a description of the material's use in this order
     * @param connectionPool the database connection pool
     * @throws DatabaseException if a SQL error occurs during the insert
     */
    public void insertMaterialList(int orderId, int materialId, int amount, String description, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO material_list (order_id, material_id, amount, description) VALUES (?, ?, ?, ?)";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, orderId);
            preparedStatement.setInt(2, materialId);
            preparedStatement.setInt(3, amount);
            preparedStatement.setString(4, description);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[MaterialsMapper.insertMaterialList] " + e.getMessage());
            throw new DatabaseException("Could not insert material " + materialId + " for order " + orderId);
        }
    }
}
