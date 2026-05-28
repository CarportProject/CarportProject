package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.*;
import app.service.OrderService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class SalesController {
    private final static MaterialsMapper MATERIALS_MAPPER = new MaterialsMapper();

    /**
     * Registers all sales-related routes on the Javalin application.
     *
     * @param app            the Javalin application instance
     * @param connectionPool the database connection pool passed to handlers
     */
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/admin/orders", ctx -> renderOrderPage(ctx, connectionPool));
        app.get("/admin/order-filter", ctx -> statusFilter(ctx, connectionPool));
        app.get("/admin/material-list/{orderId}", ctx -> getMaterialList(ctx, connectionPool));
        app.get("/admin/materials", ctx -> getMaterials(ctx, connectionPool));
        app.post("/admin/reject-order", ctx -> rejectOrder(ctx, connectionPool));
        app.post("/admin/accept-order", ctx -> acceptOrder(ctx, connectionPool));
        app.post("/admin/materials/update", ctx -> updateMaterial(ctx, connectionPool));
    }

    public static void renderOrderPage(Context ctx, ConnectionPool connectionPool) {
        String statusFilter = ctx.queryParam("statusFilter") != null
                ? ctx.queryParam("statusFilter") : "PENDING";

        ctx.attribute("statusFilter", statusFilter);
        List<Order> orderList = null;
        try {
            orderList = OrderMapper.getAllOrders(connectionPool);
            ctx.attribute("orders", orderList);
        } catch (DatabaseException e) {
            System.err.println("[SalesController.getAllOrders] " + e.getMessage());
            ctx.attribute("errorMessage", "Noget gik galt mens ordrene blev hentet, prøv igen senere.");
        }
        ctx.render("/orders.html");
    }

    private static void statusFilter(Context ctx, ConnectionPool connectionPool) {
        String statusFilter = ctx.queryParam("statusFilter");
        try {
            ctx.attribute("orders", OrderMapper.getAllOrders(connectionPool));
            ctx.attribute("statusFilter", statusFilter);
            ctx.render("/orders.html");
        } catch (DatabaseException e) {
            System.err.println("[SalesController.statusFilter] " + e.getMessage());
            ctx.redirect("/admin/orders?error=Noget+gik+galt");
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
            OrderService.changeOrderStatus(order, OrderStatus.REJECTED, connectionPool);
            ctx.redirect("/admin/orders?success=Ordren+blev+afvist");
        } catch (Exception e) {
            System.err.println("[SalesController.rejectOrder] " + e.getMessage());
            ctx.redirect("/admin/orders?error=Noget+gik+galt,+kontakt+administrator");
        }
    }

    private static void acceptOrder(Context ctx, ConnectionPool connectionPool) {
        changeOrderPrice(ctx, connectionPool);

        try {
            int orderId = Integer.parseInt(ctx.formParam("orderId"));
            Order order = OrderMapper.getOrderById(orderId, connectionPool);


            OrderService.changeOrderStatus(order, OrderStatus.OFFER_SENT, connectionPool);
            ctx.redirect("/admin/orders?success=Ordren+blev+godkendt");
        } catch (Exception e) {

            System.err.println("[SalesController.acceptOrder] " + e.getMessage());
            ctx.redirect("/admin/orders?error=Noget+gik+galt,+kontakt+administrator");
        }
    }

    private static void changeOrderPrice(Context ctx, ConnectionPool connectionPool) {
        String stringUpdatedValue = ctx.formParam("price");
        double updatedValue;

        if (stringUpdatedValue != null && !stringUpdatedValue.isEmpty()) {
            updatedValue = Double.parseDouble(stringUpdatedValue);


            try {
                int orderId = Integer.parseInt(ctx.formParam("orderId"));
                OrderMapper.changeOrderDetails(orderId, new OrderDetails(updatedValue), connectionPool);

            } catch (Exception e) {
                System.err.println("[SalesController.changeOrderPrice]" + e.getMessage());
                ctx.redirect("/admin/orders?error=Noget+gik+galt,+kontakt+administrator");
            }
        }
    }

    public static void getMaterialList(Context ctx, ConnectionPool connectionPool) {


        int orderId = Integer.parseInt(ctx.pathParam("orderId"));
        try {
            List<MaterialListEntry> materialListEntries = MATERIALS_MAPPER.findMaterialListById(orderId, connectionPool);
            ctx.json(materialListEntries);
        } catch (DatabaseException e) {
            ctx.status(500).json(Map.of("error", "Noget gik galt"));
        }
    }

    private static void getMaterials(Context ctx, ConnectionPool connectionPool) {
        List<Material> materials;
        try {
            materials = MATERIALS_MAPPER.getAllMaterials(connectionPool);
            ctx.attribute("materials", materials);
        } catch (DatabaseException e) {
            ctx.redirect("/admin/materials?error=Noget+gik+galt,+prøv+igen+senere.");
        }

        ctx.render("/material-admin.html");
    }

    private static void updateMaterial(Context ctx, ConnectionPool connectionPool) {

        int materialId = Integer.parseInt(ctx.formParam("id"));
        String name = ctx.formParam("name");
        String description = ctx.formParam("description");
        double price = Double.parseDouble(ctx.formParam("price"));
        int minLength = Integer.parseInt(ctx.formParam("min-length"));
        int maxLength = Integer.parseInt(ctx.formParam("max-length"));

        Material material = new Material.Builder()
                .id(materialId)
                .name(name)
                .description(description)
                .price(price)
                .minLengthMm(minLength)
                .maxLengthMm(maxLength)
                .build();

        try {
            MATERIALS_MAPPER.updateMaterialInfo(material, connectionPool);
        } catch (DatabaseException e) {
            ctx.redirect("/admin/materials?error=Noget+gik+galt,+prøv+igen+senere.");
        }
        ctx.redirect("/admin/materials?success=Materialet+er+blevet+opdateret.");
    }


}


