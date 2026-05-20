package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialsMapper;
import app.persistence.OrderMapper;
import app.service.OrderService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class SalesController {

    /**
     * Registers all sales-related routes on the Javalin application.
     *
     * @param app            the Javalin application instance
     * @param connectionPool the database connection pool passed to handlers
     */
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/orders", ctx -> renderOrderPage(ctx, connectionPool));
        app.get("/admin/orders", ctx -> statusFilter(ctx, connectionPool));
        app.post("/admin/reject-order", ctx -> rejectOrder(ctx, connectionPool));
        app.post("/admin/accept-order", ctx -> acceptOrder(ctx, connectionPool));
    }

    public static void renderOrderPage(Context ctx, ConnectionPool connectionPool) {
        OrderMapper orderMapper = new OrderMapper();
        User user = ctx.attribute("user");
        if (null == user || !user.getRole().equals(Role.EMPLOYEE)) {
            ctx.redirect("/error-page");
            return;
        }

        ctx.attribute("successMessage", ctx.formParam("success"));
        ctx.attribute("errorMessage", ctx.formParam("error"));

        List<Order> orderList = null;
        try {
            orderList = orderMapper.getAllOrders(connectionPool).reversed();
        } catch (DatabaseException e) {
            System.err.println("[SalesController.getAllOrders] " + e.getMessage());
            ctx.attribute("errorMessage", "Noget gik galt mens ordrene blev hentet, prøv igen senere.");
        }

        ctx.attribute("orders", orderList);
        ctx.render("/orders.html");

    }

    public static void setFormAttributes(Context ctx, ConnectionPool connectionPool) {

    }

    private static void statusFilter(Context ctx, ConnectionPool connectionPool) {
        String statusFilter = ctx.queryParam("statusFilter");
        OrderMapper orderMapper = new OrderMapper();
        try {
            ctx.attribute("orders", orderMapper.getAllOrders(connectionPool));
            ctx.attribute("statusFilter", statusFilter);
            ctx.render("/orders.html");
        } catch (DatabaseException e) {
            System.err.println("[SalesController.statusFilter] " + e.getMessage());
            ctx.redirect("/orders?error=Noget+gik+galt");
        }
    }

    private static void rejectOrder(Context ctx, ConnectionPool connectionPool) {
        try {
            Order order = new Order.Builder()
                    .id(Integer.parseInt(ctx.formParam("orderId")))
                    .contactInfo(new ContactInfo.Builder()
                            .email(ctx.formParam("email"))
                            .build())
                    .build();
            OrderService.rejectOrder(order, connectionPool);
            ctx.redirect("/orders?success=Ordren+blev+afvist");
        } catch (Exception e) {
            System.err.println("[SalesController.rejectOrder] " + e.getMessage());
            ctx.redirect("/orders?error=Noget+gik+galt,+kontakt+administrator");
        }
    }

    private static void acceptOrder(Context ctx, ConnectionPool connectionPool) {
        changeOrderPrice(ctx, connectionPool);
        try {
            Order order = new Order.Builder()
                    .id(Integer.parseInt(ctx.formParam("orderId")))
                    .build();
            OrderService.acceptOrder(order, connectionPool);
            ctx.redirect("/orders?success=Ordren+blev+godkendt");
        } catch (Exception e) {

            System.err.println("[SalesController.acceptOrder] " + e.getMessage());
            ctx.redirect("/orders?error=Noget+gik+galt,+kontakt+administrator");
        }
    }

    private static void changeOrderPrice(Context ctx, ConnectionPool connectionPool) {
        String stringUpdatedValue = ctx.attribute("total-price-input");
        int updatedValue;
        if (stringUpdatedValue != null) {
            updatedValue = Integer.parseInt(stringUpdatedValue);

            MaterialsMapper materialsMapper = new MaterialsMapper();
            Material material = new Material.Builder()
                    .price(updatedValue)
                    .build();

            try {
                materialsMapper.updateMaterialInfo(material, connectionPool);

            } catch (DatabaseException e) {
                ctx.redirect("/orders?error=Noget+gik+galt,+kontakt+administrator");
            }
        }
    }
}


