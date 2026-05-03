package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.exceptions.InvalidCredentialsException;
import app.exceptions.UserNotFoundException;
import app.persistence.ConnectionPool;
import app.service.UserService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller responsible for handling HTTP requests related to user accounts,
 * including login, logout, and user registration.
 */
public class UserController {

    private static final UserService userService = new UserService();

    /**
     * Registers all user-related routes on the Javalin application.
     *
     * @param app            the Javalin application instance
     * @param connectionPool the database connection pool passed to handlers
     */
    public static void addRouts(Javalin app, ConnectionPool connectionPool) {
        app.get("/Side1", ctx -> ctx.render("fog-carport.html"));
        app.get("/login-page", ctx -> ctx.render("login.html"));
        app.get("/create-user", ctx -> ctx.render("create-user.html"));
        app.post("/login", ctx -> login(ctx, connectionPool));
        app.post("/create-user", ctx -> createUser(ctx, connectionPool));
        app.post("/logout", UserController::logout);
    }

    /**
     * Handles POST /login. Reads email and password from the form,
     * delegates authentication to {@link UserService}, and on success
     * stores the user in the session and redirects to /Side1.
     * On failure, re-renders the login page with an error message.
     *
     * @param ctx            the Javalin request/response context
     * @param connectionPool the database connection pool
     */
    private static void login(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            User user = userService.login(email, password, connectionPool);
            // Store the authenticated user in the session for subsequent requests
            ctx.sessionAttribute("user", user);
            ctx.redirect("/Side1");
        } catch (UserNotFoundException | InvalidCredentialsException e) {
            // Show a generic message so we don't reveal whether the email exists
            ctx.attribute("error", "Username or password incorrect");
            ctx.render("login.html");
        } catch (Exception e) {
            ctx.attribute("error", "Something went wrong, try again later");
            ctx.render("login.html");
        }
    }

    /**
     * Handles POST /create-user. Reads registration details from the form,
     * delegates creation to {@link UserService}, then automatically logs the
     * new user in and redirects to /Side1.
     * On failure, re-renders the registration page with an appropriate error message.
     *
     * @param ctx            the Javalin request/response context
     * @param connectionPool the database connection pool
     */
    private static void createUser(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        String confirmPassword = ctx.formParam("confirmPassword");

        try {
            userService.createUser(email, password, confirmPassword, connectionPool);

            // Automatically log the user in after successful registration
            User user = userService.login(email, password, connectionPool);
            ctx.sessionAttribute("user", user);
            ctx.redirect("/Side1");
        } catch (InvalidCredentialsException e) {
            ctx.attribute("error", "Passwords do not match");
            ctx.render("create-user.html");
        } catch (DatabaseException | UserNotFoundException e) {
            ctx.attribute("error", "Something went wrong, please try again");
            ctx.render("create-user.html");
        }
    }

    /**
     * Handles POST /logout. Invalidates the current HTTP session,
     * removing all stored session attributes including the logged-in user.
     * Redirects back to the page the user came from, or to / if no
     * Referer header is present.
     *
     * @param ctx the Javalin request/response context
     */
    private static void logout(Context ctx) {
        // Invalidate the session to clear all user data, including the stored User object
        ctx.req().getSession().invalidate();

        // Redirect back to the referring page, falling back to the front page
        String ref = ctx.header("Referer");
        ctx.redirect(null != ref ? ref : "/");
    }
}
