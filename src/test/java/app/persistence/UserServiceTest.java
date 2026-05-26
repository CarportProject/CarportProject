package app.persistence;

import app.entities.User;
import app.exceptions.InvalidCredentialsException;
import app.exceptions.UserNotFoundException;
import app.service.UserService;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest extends DatabaseTest {

    private final UserService userService = new UserService();
    private final UserMapper userMapper = new UserMapper();

    @Test
    void loginSuccess() throws Exception {
        // Arrange
        String email = "logintest@example.com";
        String plain = "Secret123";
        userMapper.insertUser(email, BCrypt.hashpw(plain, BCrypt.gensalt()), connectionPool);

        // Act
        User user = userService.login(email, plain, connectionPool);

        // Assert
        assertNotNull(user);
        assertEquals(email, user.getEmail());
    }

    @Test
    void loginFailsWrongPassword() throws Exception {
        // Arrange
        String email = "wrongpass@example.com";
        String plain = "CorrectPass";
        userMapper.insertUser(email, BCrypt.hashpw(plain, BCrypt.gensalt()), connectionPool);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class,
                () -> userService.login(email, "WrongPass", connectionPool));
    }

    @Test
    void loginFailsUserNotFound() {

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> userService.login("missing@example.com", "any", connectionPool));
    }

    @Test
    void createUserSuccess() throws Exception {
        // Arrange
        String email = "newuser@example.com";
        String pass = "MyPassword123";

        // Act
        userService.createUser(email, pass, pass, connectionPool);

        // Assert
        User user = userMapper.findUserByEmail(email, connectionPool);
        assertNotNull(user);
        assertTrue(BCrypt.checkpw(pass, user.getPassword()));
    }

    @Test
    void createUserFailsPasswordMismatch() {

        // Act & Assert
        assertThrows(InvalidCredentialsException.class,
                () -> userService.createUser("fail@example.com", "pass1", "pass2", connectionPool));
    }
}