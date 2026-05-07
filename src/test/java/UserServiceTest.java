import app.entities.User;
import app.exceptions.InvalidCredentialsException;
import app.persistence.UserMapper;
import app.service.UserService;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest extends DatabaseTest{
    UserService userService = new UserService();
    UserMapper userMapper = new UserMapper();

    @Test
    void shouldLoginWithValidCredentials() throws Exception {

        // Arrange
        String email = "UserLogin@example.com";
        String plainPassword = "Password123";
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        userMapper.insertUser(email, hashedPassword, connectionPool);

        // Act
        User user = userService.login(email, plainPassword, connectionPool);

        // Assert
        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertEquals(hashedPassword, user.getPassword());

    }

    @Test
    void shouldThrowExceptionOnWrongPassword() throws Exception{

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
