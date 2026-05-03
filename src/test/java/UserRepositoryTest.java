import app.entities.User;
import app.persistence.UserRepository;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserRepositoryTest extends DatabaseTest{

    private UserRepository userRepository = new UserRepository();

    @Test
    void shouldCreateUser() throws Exception{
        // Arrange
        String email = "test@example.com";
        String password = "password123";

        // Act
        userRepository.insertUser(email, password, connectionPool);

        // Assert
        User user = userRepository.findUserByEmail(email, connectionPool);
        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());

    }

}
