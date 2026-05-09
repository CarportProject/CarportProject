package app.entities;

/**
 * Defines the access roles available in the carport system.
 * <p>
 * A {@link User} is assigned exactly one role, which controls what
 * actions they are permitted to perform.
 * </p>
 */
public enum Role {

    /** Full system access — can manage users, orders, and materials. */
    ADMIN,

    /** Can view and process orders on behalf of customers. */
    SALES,

    /** Can place and view their own orders. */
    CUSTOMER
}
