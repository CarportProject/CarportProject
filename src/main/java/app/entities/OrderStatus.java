package app.entities;

import app.persistence.ConnectionPool;
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
        String schema = ConnectionPool.getSchema();
        pgObject.setType(schema + ".order_status");
        pgObject.setValue(this.name());
        return pgObject;
    }
}