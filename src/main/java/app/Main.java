package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.UserController;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    //Erstat "database" i nedstående string
    private static final String URL = "jdbc:postgresql://database/%s?currentSchema=public";
    private static final String DB = "";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);
    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        // Routing

        app.get("/", ctx -> ctx.render("index.html"));
        UserController.addRouts(app, connectionPool);
    }
}