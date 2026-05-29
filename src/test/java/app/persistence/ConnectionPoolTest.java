package app.persistence;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link ConnectionPool}.
 * <p>
 * Each test runs against the {@code test} schema via the shared connection pool
 * provided by {@link DatabaseTest}. The database is wiped before every test,
 * so tests are fully independent of each other.
 * </p>
 */
class ConnectionPoolTest extends DatabaseTest {

    /**
     * Verifies that {@link ConnectionPool#getInstance} returns the SAME instance
     * when called multiple times (Singleton pattern).
     */
    @Test
    void successGetInstance() {
        // Arrange - Get the existing pool instance from DatabaseTest
        ConnectionPool firstPool = connectionPool;

        // Act - Request the instance again with the same credentials
        ConnectionPool secondPool = ConnectionPool.getInstance(
                System.getenv("JDBC_USER"),
                System.getenv("JDBC_PASSWORD"),
                System.getenv("JDBC_CONNECTION_STRING"),
                "test",
                System.getenv("JDBC_DB")
        );

        // Assert - Both references should point to the same object
        assertSame(firstPool, secondPool, "getInstance should return the same instance (Singleton)");
    }

    /**
     * Verifies that {@link ConnectionPool#getInstance} throws an exception when
     * called with invalid parameters after the instance has been reset.
     */
    @Test
    void failGetInstance() {
        // Arrange - Reset the singleton instance before testing invalid input
        ConnectionPool.resetInstance();

        // Act & Assert - Calling getInstance with null parameters should throw an exception
        assertThrows(Exception.class, () -> {
            ConnectionPool.getInstance(null, null, null, null, null);
        });
    }

    /**
     * Verifies that {@link ConnectionPool#getConnection} returns a valid,
     * open connection that can communicate with the database.
     */
    @Test
    void getConnection() throws SQLException {
        // Arrange - Use the connection pool from DatabaseTest

        // Act - Obtain a connection from the pool
        try (Connection connection = connectionPool.getConnection()) {

            // Assert - Connection should be valid, open, and able to communicate
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
            assertTrue(connection.isValid(2), "Connection should be able to communicate with the database");
        }
    }

    /**
     * Verifies that {@link ConnectionPool#resetInstance} creates a completely new
     * pool instance, breaking the singleton reference.
     */
    @Test
    void resetInstance() throws SQLException {
        // Arrange - Store the original pool reference
        ConnectionPool oldPool = connectionPool;

        // Act - Reset the singleton and create a new instance
        ConnectionPool.resetInstance();
        ConnectionPool newPool = ConnectionPool.getInstance(
                System.getenv("JDBC_USER"),
                System.getenv("JDBC_PASSWORD"),
                System.getenv("JDBC_CONNECTION_STRING"),
                "test",
                System.getenv("JDBC_DB")
        );
        connectionPool = newPool; // Synchronize the static field for subsequent tests

        // Assert - The new pool should be a different instance and functional
        assertNotSame(oldPool, newPool, "Reset instance should create a completely new pool");

        try (Connection connection = newPool.getConnection()) {
            assertTrue(connection.isValid(2), "New pool should provide valid connections");
        }
    }

    /**
     * Verifies that {@link ConnectionPool#close} properly shuts down the pool,
     * making it impossible to obtain new connections.
     */
    @Test
    void close() throws SQLException {
        // Arrange - Ensure we have a fresh pool to test closing
        connectionPool.close();

        // Act & Assert - Attempting to get a connection from a closed pool should throw SQLException
        assertThrows(SQLException.class, () -> connectionPool.getConnection(),
                "Getting a connection from a closed pool should throw an exception");

        // Restore the pool for subsequent tests
        ConnectionPool.resetInstance();
        connectionPool = ConnectionPool.getInstance(
                System.getenv("JDBC_USER"),
                System.getenv("JDBC_PASSWORD"),
                System.getenv("JDBC_CONNECTION_STRING"),
                "test",
                System.getenv("JDBC_DB")
        );

        // Act & Assert - Close again and verify behavior
        connectionPool.close();
        assertThrows(SQLException.class, () -> connectionPool.getConnection(),
                "Getting a connection from a closed pool should throw an exception");

        // Final restoration for other tests in the suite
        ConnectionPool.resetInstance();
        connectionPool = ConnectionPool.getInstance(
                System.getenv("JDBC_USER"),
                System.getenv("JDBC_PASSWORD"),
                System.getenv("JDBC_CONNECTION_STRING"),
                "test",
                System.getenv("JDBC_DB")
        );
    }
}