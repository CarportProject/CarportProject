package app.entities;

import org.postgresql.util.PGobject;

import java.sql.SQLException;

/**
 * Defines the structural roof types available for a carport.
 * <p>
 * The chosen type is stored in {@link Specifications} and affects
 * which materials and calculations are applied to the order.
 * </p>
 */
public enum RoofType {

    /**
     * A level roof with no pitch — simpler construction, lower cost.
     */
    FLAT,

    /**
     * A pitched roof — requires additional framing and a {@link Specifications#roofPitch} value.
     */
    RAISED;

    public PGobject getDatabaseEnum() throws SQLException {
        PGobject pGobject = new PGobject();
        pGobject.setType("roof_type");
        pGobject.setValue(this.name());
        return pGobject;
    }
}
