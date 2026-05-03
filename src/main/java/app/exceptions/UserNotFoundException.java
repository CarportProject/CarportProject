package app.exceptions;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String msg) {
        super(msg);
        System.out.println(msg);
    }
}
