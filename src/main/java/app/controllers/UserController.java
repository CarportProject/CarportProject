package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.exceptions.InvalidCredentialsException;
import app.exceptions.UserNotFoundException;
import app.persistence.ConnectionPool;
import app.persistence.OrderDetails;
import app.persistence.OrderMapper;
import app.service.FormService;
import app.service.UserService;
import io.javalin.Javalin;
import io.javalin.http.Context;


/**
 * Controller responsible for handling HTTP requests related to user accounts,
 * including login, logout, and user registration.
 */
public class UserController {

    private static final UserService USER_SERVICE = new UserService();

    /**
     * Registers all user-related routes on the Javalin application.
     *
     * @param app            the Javalin application instance
     * @param connectionPool the database connection pool passed to handlers
     */
    public static void addRouts(Javalin app, ConnectionPool connectionPool) {
        app.get("/Side1", ctx -> ctx.render("fog-carport.html"));
        app.get("/login-page", ctx -> ctx.render("login.html"));
        app.get("/create-user", ctx -> ctx.render("create-user.html"));
        app.get("/carport/raised-roof", ctx -> getRaisedRoof(ctx, connectionPool));
        app.get("/carport/flat-roof", ctx -> getFlatRoof(ctx, connectionPool));
        app.post("/login", ctx -> login(ctx, connectionPool));
        app.post("/create-user", ctx -> createUser(ctx, connectionPool));
        app.post("/logout", UserController::logout);
        app.post("/send-form", ctx -> buildOrderWithForm(ctx, connectionPool));


    }

    /**
     * Handles POST /login. Reads email and password from the form,
     * delegates authentication to {@link UserService}, and on success
     * stores the user in the session and redirects to /Side1.
     * On failure, re-renders the login page with an error message.
     *
     * @param ctx            the Javalin request/response context
     * @param connectionPool the database connection pool
     */
    private static void login(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        if (validateLogin(ctx, email, password)) return;

        try {
            User user = USER_SERVICE.login(email, password, connectionPool);
            // Store the authenticated user in the session for subsequent requests
            ctx.sessionAttribute("user", user);
            ctx.redirect("/Side1");
        } catch (UserNotFoundException | InvalidCredentialsException e) {
            // Show a generic message so we don't reveal whether the email exists
            ctx.attribute("errorMessage", "Brugernavn eller adgangskode forkert.");
            ctx.render("login.html");
        } catch (Exception e) {
            ctx.attribute("errorMessage", "Noget gik galt, prøv igen senere.");
            ctx.render("login.html");
        }
    }

    /**
     * Validates that email and password form parameters are present and non-blank.
     * Renders the registration page with an error message and returns {@code true}
     * if validation fails, so the caller can return early.
     *
     * @param ctx      the Javalin request/response context
     * @param email    the email parameter from the form, may be {@code null}
     * @param password the password parameter from the form, may be {@code null}
     * @return {@code true} if validation failed, {@code false} if both fields are valid
     */
    private static boolean validateLogin(Context ctx, String email, String password) {
        if (email == null || email.isBlank()) {
            ctx.attribute("errorMessage", "Email mangler.");
            ctx.render("create-user.html");
            return true;
        }
        if (password == null || password.isBlank()) {
            ctx.attribute("errorMessage", "Adgangskode mangler.");
            ctx.render("create-user.html");
            return true;
        }
        return false;
    }

    /**
     * Handles POST /create-user. Reads registration details from the form,
     * delegates creation to {@link UserService}, then automatically logs the
     * new user in and redirects to /Side1.
     * On failure, re-renders the registration page with an appropriate error message.
     *
     * @param ctx            the Javalin request/response context
     * @param connectionPool the database connection pool
     */
    private static void createUser(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        String confirmPassword = ctx.formParam("confirmPassword");

        if (validateLogin(ctx, email, password)) return;

        try {
            USER_SERVICE.createUser(email, password, confirmPassword, connectionPool);

            // Automatically log the user in after successful registration
            User user = USER_SERVICE.login(email, password, connectionPool);
            ctx.sessionAttribute("user", user);
            ctx.redirect("/Side1");
        } catch (InvalidCredentialsException e) {
            ctx.attribute("errorMessage", "Adgangskoderne stemmer ikke overens.");
            ctx.render("create-user.html");
        } catch (DatabaseException | UserNotFoundException e) {
            System.err.println("[UserController.createUser] " + e.getMessage());
            ctx.attribute("errorMessage", "Noget gik galt, prøv igen senere.");
            ctx.render("create-user.html");
        }
    }

    /**
     * Handles POST /logout. Invalidates the current HTTP session,
     * removing all stored session attributes including the logged-in user.
     * Redirects back to the page the user came from, or to / if no
     * Referer header is present.
     *
     * @param ctx the Javalin request/response context
     */
    private static void logout(Context ctx) {
        // Invalidate the session to clear all user data, including the stored User object
        ctx.req().getSession().invalidate();

        // Redirect back to the referring page, falling back to the front page
        String ref = ctx.header("Referer");
        ctx.redirect(null != ref ? ref : "/");

    }

    /**
     * Populates the Javalin context with the model attributes required by the carport order forms.
     * Fetches roof materials filtered by {@code roofType} from the database and adds dimension
     * ranges for carport width/length and workshop width/length.
     *
     * @param ctx            the Javalin request/response context
     * @param connectionPool the database connection pool
     * @param roofType       the roof type used to filter which tiles are shown in the form
     */
    private static void setFormAttributes(Context ctx, ConnectionPool connectionPool, RoofType roofType) {
        FormService formService = new FormService();
        try {
            ctx.attribute("tiles", formService.getRoofByRoofType(roofType, connectionPool));
        } catch (DatabaseException e) {
            System.err.println("[UserController.getRaisedRoof] " + e.getMessage());
            ctx.attribute("errorMessage", "Noget gik galt, prøv igen senere.");
        }
        ctx.attribute("widths", formService.getRange(240, 600, 30));
        ctx.attribute("lengths", formService.getRange(240, 780, 30));
        ctx.attribute("workshopWidths", formService.getRange(150, 690, 30));
        ctx.attribute("workshopLengths", formService.getRange(210, 720, 30));
    }

    /**
     * Handles GET /carport/raised-roof. Loads form attributes for raised-roof carporten
     * and renders the raised-roof order page.
     *
     * @param ctx            the Javalin request/response context
     * @param connectionPool the database connection pool
     */
    private static void getRaisedRoof(Context ctx, ConnectionPool connectionPool) {
        setFormAttributes(ctx, connectionPool, RoofType.RAISED);
        ctx.render("raised-roof.html");
    }

    /**
     * Handles GET /carport/flat-roof. Loads form attributes for flat-roof carporten
     * and renders the flat-roof order page.
     *
     * @param ctx            the Javalin request/response context
     * @param connectionPool the database connection pool
     */
    private static void getFlatRoof(Context ctx, ConnectionPool connectionPool) {
        setFormAttributes(ctx, connectionPool, RoofType.FLAT);
        ctx.render("flat-roof.html");
    }

    /**
     * Handles POST /send-form. Reads all carport order fields from the submitted form,
     * constructs an {@link Order} and persists it via {@link OrderMapper}.
     * On success, sets a success message and redirects back to the referring page.
     * On any error, logs it, sets an error message, and redirects back.
     *
     * @param ctx            the Javalin request/response context
     * @param connectionPool the database connection pool
     */
    private static void buildOrderWithForm(Context ctx, ConnectionPool connectionPool) {
        String trueReferer = ctx.header("Referer") != null ? ctx.header("Referer") : "/";

        try {
            Order order = new Order.Builder()
                    .contactInfo(buildContactInfo(ctx))
                    .specifications(buildSpecifications(ctx))
                    .workshop(buildWorkshop(ctx))
                    .orderDetails(new OrderDetails(ctx.formParam("remarks"), Status.PENDING))
                    .build();

            new OrderMapper().insertOrder(order, connectionPool);
            ctx.attribute("successMessage", "Din ordre er nu bestilt.");
        } catch (DatabaseException e) {
            System.err.println("[UserController.buildOrderWithForm] " + e.getMessage());
            ctx.attribute("errorMessage", "Noget gik galt, prøv igen senere.");
        } catch (Exception e) {
            System.err.println("[UserController.buildOrderWithForm] " + e.getMessage());
            ctx.attribute("errorMessage", "Ugyldig forespørgsel.");
        }
        //TODO send email til kunde
        ctx.attribute("successMessage", "Din ordre er blevet oprettet.");
        ctx.redirect(trueReferer);
    }

    /**
     * Builds a {@link Specifications} object from the submitted form parameters.
     * {@code roofPitch} defaults to {@code 0} when not present (flat-roof orders).
     *
     * @param ctx the Javalin request/response context containing the form data
     * @return a fully populated {@link Specifications} instance
     */
    private static Specifications buildSpecifications(Context ctx) {
        RoofType roofType = RoofType.valueOf(ctx.formParam("roofType").toUpperCase());
        int roofMaterialId = Integer.parseInt(ctx.formParam("roofMaterial"));
        String roofPitchParam = ctx.formParam("roofPitch");
        int roofPitch = roofPitchParam != null ? Integer.parseInt(roofPitchParam) : 0;

        return new Specifications.Builder()
                .roofType(roofType)
                .roofMaterial(new RoofMaterial.Builder().id(roofMaterialId).build())
                .widthCm(Integer.parseInt(ctx.formParam("widthCm")))
                .lengthCm(Integer.parseInt(ctx.formParam("lengthCm")))
                .roofPitch(roofPitch)
                .build();
    }

    /**
     * Builds a {@link Workshop} from the submitted form parameters, or returns {@code null}
     * if the user selected "WITHOUT" workshop.
     *
     * @param ctx the Javalin request/response context containing the form data
     * @return a {@link Workshop} instance, or {@code null} if no workshop was requested
     */
    private static Workshop buildWorkshop(Context ctx) {
        if (!"WITH".equals(ctx.formParam("workshop"))) {
            return null;
        }
        return new Workshop.Builder()
                .widthCm(Integer.parseInt(ctx.formParam("workshop-width")))
                .lengthCm(Integer.parseInt(ctx.formParam("workshop-length")))
                .build();
    }

    /**
     * Builds a {@link ContactInfo} object from the submitted form parameters.
     *
     * @param ctx the Javalin request/response context containing the form data
     * @return a fully populated {@link ContactInfo} instance
     */
    private static ContactInfo buildContactInfo(Context ctx) {
        return new ContactInfo.Builder()
                .firstName(ctx.formParam("firstName"))
                .lastName(ctx.formParam("lastName"))
                .address(ctx.formParam("address"))
                .postalCode(Integer.parseInt(ctx.formParam("postalCode")))
                .city(ctx.formParam("city"))
                .email(ctx.formParam("email"))
                .phoneNumber(ctx.formParam("phoneNumber"))
                .build();
    }

}
