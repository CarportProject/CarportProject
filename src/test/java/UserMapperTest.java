import app.entities.User;
import app.exceptions.UserNotFoundException;
import app.persistence.UserMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link UserMapper}.
 * <p>
 * Each test runs against the {@code test} schema via the shared connection pool
 * provided by {@link DatabaseTest}. The database is wiped before every test,
 * so tests are fully independent of each other.
 * </p>
 */
public class UserMapperTest extends DatabaseTest {

    private final UserMapper userMapper = new UserMapper();

    /**
     * Verifies that a newly inserted user can be retrieved by email,
     * and that the stored email and password match the inserted values.
     */
    @Test
    void shouldCreateUser() throws Exception {

        // Arrange
        String email = "CreateUser@example.com";
        String password = "password123";

        // Act
        userMapper.insertUser(email, password, connectionPool);

        // Assert
        User user = userMapper.findUserByEmail(email, connectionPool);
        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());

    }

    /**
     * Verifies that looking up an email that does not exist in the database
     * throws {@link UserNotFoundException} rather than returning {@code null}.
     */
    @Test
    void shouldReturnNullWhenUserNotFound() throws Exception {
        // Arrange
        String email = "UserNotFound@example.com";

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userMapper.findUserByEmail(email, connectionPool);
        });
    }

}
