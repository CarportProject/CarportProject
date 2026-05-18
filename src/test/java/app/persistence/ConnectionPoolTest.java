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

        // Arrange - Use connectionPool from DatabaseTest
        ConnectionPool firstPool = connectionPool;

        // Act - Get new instance via getInstance
        ConnectionPool secondPool = ConnectionPool.getInstance(
                System.getenv("JDBC_USER"),
                System.getenv("JDBC_PASSWORD"),
                System.getenv("JDBC_CONNECTION_STRING"),
                "test",
                System.getenv("JDBC_DB")
        );

        // Assert - Whether they are the same object
        assertSame(firstPool, secondPool, "getInstance should return the same instance (Singleton)");

    }

    @Test
    void failGetInstance() {

        //Arrange
        ConnectionPool firstPool = connectionPool;

        // Act & Assert
        ConnectionPool secondPool = null;
        assertNotSame(secondPool, firstPool, "getInstance should return the same instance (Singleton)");
    }

    /**
     * Verifies that {@link ConnectionPool#getConnection} returns a valid,
     * open connection that can communicate with the database.
     */
    @Test
    void getConnection() throws SQLException {

        // Act - Get a connection
        try (Connection connection = connectionPool.getConnection()) {

            // Assert - Connection should be valid
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
            assertTrue(connection.isValid(2), "Connection should be able to communicate with the database");
        }
    }

    /**
     * Verifies that after calling {@link ConnectionPool#close}, no more connections
     * can be obtained – calling {@link ConnectionPool#getConnection} throws an exception.
     */
    @Test
    void close() throws SQLException {

        // Act - Close the ConnectionPool
        connectionPool.close();

        // Assert - After close, the pool should not be able to get any new connections
        assertThrows(SQLException.class, () -> {
            connectionPool.getConnection();
        }, "After close() it should not be possible to get a connection");
    }

    /**
     * Verifies that {@link ConnectionPool#resetInstance} creates a BRAND NEW pool
     * instance, and that the new pool works correctly.
     */
    @Test
    void resetInstance() throws SQLException {

        // Arrange - Save the old pool
        ConnectionPool oldPool = connectionPool;

        // Act - Reset ConnectionPool
        ConnectionPool.resetInstance();

        // Get the new pool
        ConnectionPool newPool = ConnectionPool.getInstance(
                System.getenv("JDBC_USER"),
                System.getenv("JDBC_PASSWORD"),
                System.getenv("JDBC_CONNECTION_STRING"),
                "test",
                System.getenv("JDBC_DB")
        );

        // Assert - They need to not be the same object
        assertNotSame(oldPool, newPool, "resetInstance should create a NEW pool instance");

        // Ekstra assert - New pool needs to be able to get a connection
        try (Connection connection = newPool.getConnection()) {
            assertTrue(connection.isValid(2), "The new pool should work correctly");
        }
    }
}