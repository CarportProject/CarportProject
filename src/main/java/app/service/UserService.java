package app.service;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.exceptions.InvalidCredentialsException;
import app.exceptions.UserNotFoundException;
import app.persistence.ConnectionPool;
import app.persistence.UserRepository;
import io.javalin.http.Context;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public class UserService {

    private static final UserRepository userRepository = new UserRepository();


    public User login(String email, String password, ConnectionPool connectionPool) throws DatabaseException, InvalidCredentialsException, UserNotFoundException {
        User user = userRepository.findUserByEmail(email, connectionPool);

        if (null == user || !BCrypt.checkpw(password, user.getPassword())) {
            throw new InvalidCredentialsException("Password wrong in login");
        }

        return user;
    }

    public void createUser(String email, String plainPassword, String confirmPassword, ConnectionPool connectionPool) throws InvalidCredentialsException, DatabaseException {


        if (!Objects.equals(plainPassword, confirmPassword)) {
            throw new InvalidCredentialsException("Passwords do not match in createUser");
        }
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        userRepository.insertUser(email, hashedPassword, connectionPool);
    }
}
