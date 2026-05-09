import app.persistence.ConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Abstract base class for all database integration tests.
 * <p>
 * Manages a shared {@link ConnectionPool} pointed at the {@code test} schema,
 * and truncates all relevant tables before each test to ensure a clean state.
 * Subclasses inherit the connection pool and the reset behaviour automatically.
 * </p>
 * <p>
 * Connection details are read from environment variables so that credentials
 * are never hard-coded in source control:
 * <ul>
 *   <li>{@code JDBC_USER}</li>
 *   <li>{@code JDBC_PASSWORD}</li>
 *   <li>{@code JDBC_CONNECTION_STRING}</li>
 *   <li>{@code JDBC_DB}</li>
 * </ul>
 * </p>
 */
public abstract class DatabaseTest {

    /** Shared connection pool used by all subclass tests. */
    protected static ConnectionPool connectionPool;

    private static final String USER = System.getenv("JDBC_USER");
    private static final String PASSWORD = System.getenv("JDBC_PASSWORD");
    private static final String URL = System.getenv("JDBC_CONNECTION_STRING");

    /** Fixed schema name used for all test runs to avoid touching production data. */
    private static final String SCHEMA = "test";

    private static final String DB = System.getenv("JDBC_DB");

    /**
     * Initialises the connection pool once before any test in the suite runs.
     * Resets any existing singleton instance first so each test run starts fresh.
     */
    @BeforeAll
    static void setup() {
        ConnectionPool.resetInstance();
        connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, SCHEMA, DB);
    }

    /**
     * Deletes all rows from every test table before each individual test.
     * Add more DELETE statements here as new tables are introduced.
     *
     * @throws SQLException if the cleanup query fails
     */
    @BeforeEach
    void cleanDatabase() throws SQLException {
        // Add more tables as needed
        String sql = "DELETE FROM users;";
        try (
                Connection connection = connectionPool.getConnection();
                Statement statement = connection.createStatement()
        ) {
            statement.execute(sql);
        }
    }
}
