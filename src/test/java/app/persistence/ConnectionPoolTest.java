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
        // Tester at en pool oprettet med forkerte credentials ikke er den samme instans
        // (Requires resetInstance first since it's a singleton)
        // Alternativt: test at getInstance med null-parametre kaster en exception
        assertThrows(Exception.class, () -> {
            ConnectionPool.resetInstance();
            ConnectionPool.getInstance(null, null, null, null, null);
        });
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

    @Test
    void resetInstance() throws SQLException {
        ConnectionPool oldPool = connectionPool;
        ConnectionPool.resetInstance();

        ConnectionPool newPool = ConnectionPool.getInstance(
                System.getenv("JDBC_USER"), System.getenv("JDBC_PASSWORD"),
                System.getenv("JDBC_CONNECTION_STRING"), "test", System.getenv("JDBC_DB")
        );
        connectionPool = newPool; // <-- TILFØJ: synkroniser det statiske felt

        assertNotSame(oldPool, newPool);
        try (Connection connection = newPool.getConnection()) {
            assertTrue(connection.isValid(2));
        }
    }

    @Test
    void close() throws SQLException {
        connectionPool.close();
        assertThrows(SQLException.class, () -> connectionPool.getConnection());

        // TILFØJ: genopret poolen så efterfølgende tests ikke knækker
        ConnectionPool.resetInstance();
        connectionPool = ConnectionPool.getInstance(
                System.getenv("JDBC_USER"), System.getenv("JDBC_PASSWORD"),
                System.getenv("JDBC_CONNECTION_STRING"), "test", System.getenv("JDBC_DB")
        );
        connectionPool.close();
        assertThrows(SQLException.class, () -> connectionPool.getConnection());

        // TILFØJ: genopret poolen så efterfølgende tests ikke knækker
        ConnectionPool.resetInstance();
        connectionPool = ConnectionPool.getInstance(
                System.getenv("JDBC_USER"), System.getenv("JDBC_PASSWORD"),
                System.getenv("JDBC_CONNECTION_STRING"), "test", System.getenv("JDBC_DB")
        );
    }

}