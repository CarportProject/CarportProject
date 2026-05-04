package app.service;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.exceptions.InvalidCredentialsException;
import app.exceptions.UserNotFoundException;
import app.persistence.ConnectionPool;
import app.persistence.UserRepository;
import app.util.GmailEmailSender;
import io.javalin.http.Context;
import jakarta.mail.MessagingException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

/**
 * Service class responsible for user-related business logic,
 * such as authentication and user registration.
 */
public class UserService {

    private static final UserRepository userRepository = new UserRepository();

    /**
     * Authenticates a user by verifying their email and password.
     * Looks up the user by email, then checks the provided password
     * against the stored BCrypt hash.
     *
     * @param email          the user's email address
     * @param password       the plain-text password to verify
     * @param connectionPool the database connection pool
     * @return the authenticated {@link User}
     * @throws UserNotFoundException       if no user exists with the given email
     * @throws InvalidCredentialsException if the password does not match
     * @throws DatabaseException           if a database error occurs during lookup
     */
    public User login(String email, String password, ConnectionPool connectionPool) throws DatabaseException, InvalidCredentialsException, UserNotFoundException {
        User user = userRepository.findUserByEmail(email, connectionPool);

        // Compare the provided plain-text password with the stored BCrypt hash
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new InvalidCredentialsException("Password wrong in login");
        }

        return user;
    }

    /**
     * Creates a new user account after validating that the two provided
     * passwords match. The password is hashed with BCrypt before storage.
     *
     * @param email           the email address for the new account
     * @param plainPassword   the desired password in plain text
     * @param confirmPassword the confirmation password, must equal {@code plainPassword}
     * @param connectionPool  the database connection pool
     * @throws InvalidCredentialsException if the passwords do not match
     * @throws DatabaseException           if a database error occurs during insertion
     */
    public void createUser(String email, String plainPassword, String confirmPassword, ConnectionPool connectionPool) throws InvalidCredentialsException, DatabaseException {

        // Ensure both password fields are identical before proceeding
        if (!Objects.equals(plainPassword, confirmPassword)) {
            throw new InvalidCredentialsException("Passwords do not match in createUser");
        }

        // Hash the password before storing it in the database
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        userRepository.insertUser(email, hashedPassword, connectionPool);

        //Send welcome mail
        GmailEmailSender emailSender = new GmailEmailSender();
        try{
            emailSender.sendPlainTextEmail(email, "Velkommen til Fog!", "Hej!\n\nDin konto er nu oprettet.\n\nMed venlig hilsen\nFog");
        } catch (MessagingException e) {
            System.err.println("Could not send welcome email: " + e.getMessage());
        }
    }
}
