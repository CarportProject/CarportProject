package app.util;

import app.controllers.SalesController;
import app.controllers.UserController;
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
                UserController.renderError(ctx, 403, "Ingen adgang", "Du har ikke adgang til denne side.");
                ctx.skipRemainingHandlers();
            }
        });
    }
}
