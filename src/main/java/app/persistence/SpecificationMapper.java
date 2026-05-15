package app.persistence;

import app.entities.RoofStyle;
import app.entities.RoofType;
import app.entities.Specifications;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repository class responsible for all database operations related to carport specifications.
 * Communicates directly with the {@code specifications} table.
 */
public class SpecificationMapper {

    /**
     * Inserts a new set of carport specifications into the {@code specifications} table.
     *
     * @param specs          the specifications to insert
     * @param connectionPool the database connection pool
     * @throws DatabaseException if a SQL error occurs during the insert
     */
    public void insertSpecifications(Specifications specs, ConnectionPool connectionPool) throws DatabaseException {
        String roofType = specs.getRoofType().name();
        int roofStyleId = specs.getRoofStyle().getId();
        int widthCm = specs.getWidthCm();
        int lengthCm = specs.getLengthCm();
        int roofPitchDegree = specs.getRoofPitch();

        String sql = "INSERT INTO specifications (roof_type, roof_style, width_cm, length_cm, roof_pitch_degree) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, roofType);
            ps.setInt(2, roofStyleId);
            ps.setInt(3, widthCm);
            ps.setInt(4, lengthCm);
            ps.setInt(5, roofPitchDegree);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[SpecificationMapper.insertSpecifications] " + e.getMessage());
            throw new DatabaseException("An error occurred while creating the specifications");
        }
    }

    public Specifications findSpecificationsById(int id, ConnectionPool connectionPool) throws DatabaseException {

        String sql = "SELECT * FROM specifications WHERE id=?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                RoofType roofType = RoofType.valueOf(resultSet.getString("roof_type"));

                int roofStyleId = resultSet.getInt("roof_style");

                RoofStyle roofStyle = new RoofStyleMapper().findRoofStyleById(roofStyleId, connectionPool);

                return new Specifications.Builder()
                    .id(id)
                    .roofType(roofType)
                    .roofStyle(roofStyle)
                    .widthCm(resultSet.getInt("width_cm"))
                    .lengthCm(resultSet.getInt("length_cm"))
                    .roofPitch(resultSet.getInt("roof_pitch_degree"))
                    .build();
            } else {
                System.err.println("[SpecificationsMapper.findById] ");
                throw new DatabaseException("Specifications not found");
            }

        } catch (SQLException e) {
            System.err.println("[SpecificationsMapper.findById] " + e.getMessage());
            throw new DatabaseException("An error occurred while fetching specifications");
        }
    }
}
