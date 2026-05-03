import app.persistence.ConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DatabaseTest {
    protected static ConnectionPool connectionPool;
    private static final String USER = "carport_user";
    private static final String PASSWORD = "hqx*Jdwqf7j!-k3ZKEmqbczQGpqPMW";
    private static final String URL = "jdbc:postgresql://167.172.103.174:5432/%s?currentSchema=test";
    private static final String DB = "Carport";

    @BeforeAll
    static void setup() {
        ConnectionPool.resetInstance();
        connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);
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
