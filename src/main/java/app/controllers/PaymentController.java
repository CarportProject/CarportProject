package app.controllers;

import app.entities.Order;
import app.entities.OrderStatus;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.service.OrderService;
import app.util.ErrorRenderer;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.UUID;

public class PaymentController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/payment", ctx -> renderPaymentPage(ctx, connectionPool));
        app.post("/payment/pay", ctx -> payOrder(ctx, connectionPool));
        app.post("/payment/cancel", ctx -> declineOrder(ctx, connectionPool));
    }

    private static void renderPaymentPage(Context ctx, ConnectionPool connectionPool) {
        String token = ctx.queryParam("token");
        if (token == null || token.isEmpty()) {
            ErrorRenderer.renderError(ctx, 404, "Ugyldig forespørgsel", "Det angivne betalingslink er ugyldigt eller udløbet.");
            return;
        }
        try {
            UUID uuid = UUID.fromString(token);
            Order order = OrderMapper.getOrderByUuid(uuid, connectionPool);

            if (order.getOrderDetails().status() != OrderStatus.OFFER_SENT) {
                ErrorRenderer.renderError(ctx, 404, "Ugyldig forespørgsel", "Det angivne betalingslink er ugyldigt eller udløbet.");
                return;
            }
            ctx.attribute("order", order);
            ctx.render("/payment.html");
        } catch (IllegalArgumentException e) {
            ErrorRenderer.renderError(ctx, 400, "Ugyldig forespørgsel", "Det angivne token er ugyldigt.");
        } catch (DatabaseException e) {
            ErrorRenderer.renderError(ctx, 404, "Ikke fundet", "Ordren blev ikke fundet.");
        } catch (Exception e) {
            ErrorRenderer.renderError(ctx, 500, "Ugyldig forespørgsel", "Noget gik galt, prøv igen senere");
        }
    }

    private static void payOrder(Context ctx, ConnectionPool connectionPool) {
        try {
            String token = ctx.formParam("token");
            if (token == null || token.isEmpty()) {
                ErrorRenderer.renderError(ctx, 404, "Ugyldig forespørgsel", "Det angivne betalingslink er ugyldigt eller udløbet.");
                return;
            }
            UUID uuid = UUID.fromString(token);
            Order order = OrderMapper.getOrderByUuid(uuid, connectionPool);

            OrderService.changeOrderStatus(order, OrderStatus.PAID, connectionPool);
            ctx.redirect("/?success=Din+betaling+er+gennemført.+Vi+sender+dig+en+bekræftelse+på+mail.");
        } catch (Exception e) {
            ErrorRenderer.renderError(ctx, 500, "Betalingen mislykkedes",
                    "Noget gik galt under behandlingen af din betaling. Prøv igen senere eller kontakt os.");
        }
    }

    private static void declineOrder(Context ctx, ConnectionPool connectionPool) {
        try {
            String token = ctx.formParam("token");
            if (token == null || token.isEmpty()) {
                ErrorRenderer.renderError(ctx, 404, "Ugyldig forespørgsel", "Det angivne betalingslink er ugyldigt eller udløbet.");
                return;
            }
            UUID uuid = UUID.fromString(token);
            Order order = OrderMapper.getOrderByUuid(uuid, connectionPool);

            OrderService.changeOrderStatus(order, OrderStatus.CANCELLED, connectionPool);
            ctx.redirect("/?success=Du+har+afvist+tilbuddet.+Kontakt+os+hvis+du+fortryder.");
        } catch (Exception e) {
            ErrorRenderer.renderError(ctx, 500, "Afvisningen mislykkedes", "Noget gik galt. Prøv igen senere eller kontakt os.");
        }
    }

}