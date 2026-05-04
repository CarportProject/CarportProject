import app.entities.User;
import app.exceptions.UserNotFoundException;
import app.persistence.UserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest extends DatabaseTest {

    private final UserRepository userRepository = new UserRepository();

    @Test
    void shouldCreateUser() throws Exception {

        // Arrange
        String email = "CreateUser@example.com";
        String password = "password123";

        // Act
        userRepository.insertUser(email, password, connectionPool);

        // Assert
        User user = userRepository.findUserByEmail(email, connectionPool);
        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());

    }

    @Test
    void shouldReturnNullWhenUserNotFound() throws Exception {
        // Arrange
        String email = "UserNotFound@example.com";

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userRepository.findUserByEmail(email, connectionPool);
        });
    }

}
