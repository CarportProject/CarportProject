package app.util;

import io.javalin.http.Context;

public class ErrorRenderer {

    public static void renderError(Context ctx, int status, String title, String message) {
        ctx.status(status);
        ctx.attribute("errorCode", status);
        ctx.attribute("errorTitle", title);
        ctx.attribute("errorMessage", message);
        ctx.render("error.html");
    }
}