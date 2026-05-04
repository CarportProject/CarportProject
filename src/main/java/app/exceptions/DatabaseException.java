package app.exceptions;

public class DatabaseException extends Exception
{
    public DatabaseException(String userMessage)
    {
        super(userMessage);
        System.err.println("userMessage: " + userMessage);
    }

    public DatabaseException(String userMessage, String systemMessage)
    {
        super(userMessage);
        System.err.println("userMessage: " + userMessage);
        System.err.println("errorMessage: " + systemMessage);
    }
}
