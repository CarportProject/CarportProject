import app.entities.User;
import app.exceptions.UserNotFoundException;
import app.persistence.UserMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest extends DatabaseTest {

    private final UserMapper userMapper = new UserMapper();

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
