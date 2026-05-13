package app.persistence;

import app.entities.Workshop;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Repository class responsible for all database operations related to workshops.
 * Communicates directly with the {@code Workshop} table.
 */
public class WorkshopMapper {

    /**
     * Inserts a new workshop into the {@code Workshop} table.
     *
     * @param workshop       the workshop to insert
     * @param connectionPool the database connection pool
     * @throws DatabaseException if a SQL error occurs during the insert
     */
    public void insertWorkshop(Workshop workshop, ConnectionPool connectionPool) throws DatabaseException {
        int widthCm  = workshop.getWidthCm();
        int lengthCm = workshop.getLengthCm();

        String sql = "INSERT INTO workshop (width_cm, length_cm) VALUES (?, ?)";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, widthCm);
            ps.setInt(2, lengthCm);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[WorkshopMapper.insertWorkshop] " + e.getMessage());
            throw new DatabaseException("An error occurred while creating the workshop");
        }
    }
}
