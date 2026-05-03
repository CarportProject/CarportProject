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

public class UserController {

    private static final UserService userService = new UserService();

    public static void addRouts(Javalin app, ConnectionPool connectionPool) {
        app.get("/Side1", ctx -> ctx.render("fog-carport.html"));
        app.get("/login-page", ctx -> ctx.render("login.html"));
        app.get("/create-user", ctx -> ctx.render("create-user.html"));
        app.post("/login", ctx -> login(ctx, connectionPool));
        app.post("/create-user", ctx -> createUser(ctx, connectionPool));
    }

    private static void login(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        try {
            User user = userService.login(email, password, connectionPool);
            ctx.sessionAttribute("user", user);
            ctx.redirect("/Side1");
        } catch (UserNotFoundException | InvalidCredentialsException e){
            ctx.attribute("error", "Username or password incorrect");
            ctx.render("login.html");
        }
        catch (Exception e) {
            ctx.attribute("error", "Something went wrong, try again later");
            ctx.render("login.html");
        }

    }
    private static void createUser(Context ctx, ConnectionPool connectionPool){
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        String confirmPassword = ctx.formParam("confirmPassword");

        try{
            userService.createUser(email, password, confirmPassword, connectionPool);

            //Automatically log in
            User user = userService.login(email,password, connectionPool);

        } catch (InvalidCredentialsException e) {
            ctx.attribute("error", "Passwords do not match");
            ctx.render("create-user");
        }
        catch (DatabaseException e) {
            ctx.attribute("error", "Something went wrong, please try again");
            ctx.render("create-user");

        } catch (UserNotFoundException e) {
            ctx.attribute("error", "Something went wrong, please try again" );
        }
    }

}