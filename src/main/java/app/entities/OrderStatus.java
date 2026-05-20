package app.entities;

import org.postgresql.util.PGobject;

import java.sql.SQLException;

public enum OrderStatus {
    PENDING,
    OFFER_SENT,
    PAID,
    REJECTED,
    CANCELLED;

    public PGobject getDatabaseEnum() throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType("order_status");
        pgObject.setValue(this.name());
        return pgObject;
    }
}