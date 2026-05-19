package app.entities;

import org.postgresql.util.PGobject;

import java.sql.SQLException;

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
    EMPLOYEE,

    /** Can place and view their own orders. */
    CUSTOMER;

    public PGobject getDatabaseEnum() throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType("user_role");
        pgObject.setValue(this.name());
        return pgObject;
    }
}
