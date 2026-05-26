package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link UserMapper}.
 * Each test runs against the {@code test} schema via DatabaseTest.
 * Database is wiped before every test.
 */
class UserMapperTest extends DatabaseTest {

    private final UserMapper userMapper = new UserMapper();

    /**
     * TEST 1: Insert a user and find it by email.
     * Verifies: insert, find, email match, password hash, auto-generated ID, role enum
     */
    @Test
    void insertAndFindUser() throws DatabaseException, UserNotFoundException {
        // ARRANGE: Create test user with BCrypt hashed password
        String email = "test@example.com";
        String plainPassword = "Secret123!";
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        // ACT: Insert user, then retrieve it
        userMapper.insertUser(email, hashedPassword, connectionPool);
        User foundUser = userMapper.findUserByEmail(email, connectionPool);

        // ASSERT: Verify all data matches
        assertNotNull(foundUser, "User should be found");
        assertEquals(email, foundUser.getEmail(), "Email should match");
        assertEquals(hashedPassword, foundUser.getPassword(), "Password hash should match");
        assertTrue(foundUser.getId() > 0, "ID should be auto-generated and positive");
        assertNotNull(foundUser.getRole(), "Role enum should be populated from database");
    }

    /**
     * TEST 2: Try to find a user with an email that does NOT exist.
     * Expected: UserNotFoundException
     */
    @Test
    void findUserByEmailNotFound() {
        // ARRANGE: Use an email that is guaranteed not to exist
        String nonExistentEmail = "nonexistent@example.com";

        // ACT & ASSERT: Expect UserNotFoundException to be thrown
        assertThrows(UserNotFoundException.class, () -> {
            userMapper.findUserByEmail(nonExistentEmail, connectionPool);
        }, "Should throw UserNotFoundException when email does not exist");
    }

    /**
     * TEST 3: Try to insert the same email twice.
     * Verifies the UNIQUE constraint on email column works.
     * Expected: DatabaseException
     */
    @Test
    void insertDuplicateEmail() throws DatabaseException {
        // ARRANGE: Insert a user first (this should succeed)
        String email = "duplicate@example.com";
        String hashedPassword = BCrypt.hashpw("Password123", BCrypt.gensalt());
        userMapper.insertUser(email, hashedPassword, connectionPool);

        // ACT & ASSERT: Second insert with same email should fail
        assertThrows(DatabaseException.class, () -> {
            userMapper.insertUser(email, hashedPassword, connectionPool);
        }, "Should throw DatabaseException due to unique constraint violation");
    }

    /**
     * TEST 4: Verify passwords are stored as BCrypt hashes, NOT plain text.
     * Critical security test.
     * Checks:
     * 1. Stored password is not the plain text
     * 2. Stored password is a valid BCrypt hash (starts with $2a$ or $2b$)
     * 3. BCrypt.checkpw() can verify the plain password against the hash
     */
    @Test
    void passwordIsStoredAsBcryptHash() throws DatabaseException, UserNotFoundException {
        // ARRANGE: Create user with BCrypt hashed password
        String email = "security@example.com";
        String plainPassword = "MySecret123!";
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        // ACT: Insert and retrieve the user
        userMapper.insertUser(email, hashedPassword, connectionPool);
        User foundUser = userMapper.findUserByEmail(email, connectionPool);
        String storedPassword = foundUser.getPassword();

        // ASSERT 1: Database should NEVER store plain text passwords
        assertNotEquals(plainPassword, storedPassword, "Database should never store plain text passwords");

        // ASSERT 2: Stored password should be a valid BCrypt hash
        assertTrue(storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$"),
                "Stored password should be a BCrypt hash (starts with $2a$ or $2b$)");

        // ASSERT 3: BCrypt should verify plain password against stored hash
        assertTrue(BCrypt.checkpw(plainPassword, storedPassword),
                "BCrypt should verify that plain password matches the stored hash");
    }
}