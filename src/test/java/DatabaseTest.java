import app.persistence.ConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DatabaseTest {
    protected static ConnectionPool connectionPool;
    private static final String USER = System.getenv("JDBC_USER");
    private static final String PASSWORD = System.getenv("JDBC_PASSWORD");
    private static final String URL = System.getenv("JDBC_CONNECTION_STRING");
    private static final String SCHEMA = "test";
    private static final String DB = System.getenv("JDBC_DB");

    @BeforeAll
    static void setup() {
        ConnectionPool.resetInstance();
        connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, SCHEMA, DB);
    }

    @BeforeEach
    void cleanDatabase() throws SQLException {
        //Add more tabels as needed
        String sql = "DELETE FROM users;";
        try (
                Connection connection = connectionPool.getConnection();
                Statement statement = connection.createStatement();
        ) {
            statement.execute(sql);
        }
    }
}
