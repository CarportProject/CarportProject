package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.UserController;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.UUID;

public class Main {
    private static final String USER = "carport_user";
    private static final String PASSWORD = "hqx*Jdwqf7j!-k3ZKEmqbczQGpqPMW";
    private static final String URL = "jdbc:postgresql://167.172.103.174:5432/%s?currentSchema=public";
    private static final String DB = "Carport";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);
    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/static");
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        // Routing

        app.get("/", ctx -> ctx.redirect("/Side1"));
        UserController.addRouts(app, connectionPool);
    }
}