package app.controllers;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

public class UserController {

    public static void addRouts(Javalin app, ConnectionPool connectionPool) {
        app.get("/Side1", ctx -> ctx.render("fog-carport.html"));
    }

}