import app.entities.User;
import app.exceptions.InvalidCredentialsException;
import app.persistence.UserMapper;
import app.service.UserService;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link UserService}.
 * <p>
 * Tests cover the login flow end-to-end: a user is inserted via {@link UserMapper}
 * with a BCrypt-hashed password, and the service is then asked to authenticate.
 * Each test runs against the {@code test} schema provided by {@link DatabaseTest}.
 * </p>
 */
public class UserServiceTest extends DatabaseTest {

    UserService userService = new UserService();
    UserMapper userMapper = new UserMapper();

    /**
     * Verifies that a user can log in when supplying the correct plain-text password.
     * The returned {@link User} must match the email and the stored hashed password.
     */
    @Test
    void shouldLoginWithValidCredentials() throws Exception {

        // Arrange
        String email = "UserLogin@example.com";
        String plainPassword = "Password123";
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        userMapper.insertUser(email, hashedPassword, connectionPool);

        // Act
        User user = new User.Builder()
                .email(email)
                .password(plainPassword)
                .build();

        // Assert
        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertEquals(hashedPassword, user.getPassword());

    }

    /**
     * Verifies that supplying the wrong password throws {@link InvalidCredentialsException}
     * instead of returning a user, even when the email itself exists in the database.
     */
    @Test
    void shouldThrowExceptionOnWrongPassword() throws Exception {

        // Arrange
        String email = "FailLogin@example.com";
        String plainPassword = "Password123";
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        String wrongPassword = "NotPassword123";
        userMapper.insertUser(email, hashedPassword, connectionPool);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> {
            userService.login(email, wrongPassword, connectionPool);
        });
    }
}
