package app.util;

import app.entities.User;
import io.javalin.Javalin;

public class AuthFilter {
    public static void registerFilters(Javalin app) {
        app.before(ctx -> {
            ctx.attribute("user", ctx.sessionAttribute("user"));
            ctx.attribute("errorMessage", ctx.queryParam("error"));
            ctx.attribute("successMessage", ctx.queryParam("success"));
        });

        app.before("/admin/*", ctx -> {
            User user = ctx.attribute("user");
            if (user == null || !user.getRole().name().equals("EMPLOYEE")) {
                ctx.status(403);
                ctx.render("error.html");
                ctx.skipRemainingHandlers();
            }
        });
    }
}
