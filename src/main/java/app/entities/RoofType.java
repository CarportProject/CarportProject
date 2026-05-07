package app.entities;

/**
 * Defines the structural roof types available for a carport.
 * <p>
 * The chosen type is stored in {@link Specifications} and affects
 * which materials and calculations are applied to the order.
 * </p>
 */
public enum RoofType {

    /** A level roof with no pitch — simpler construction, lower cost. */
    FLAT,

    /** A pitched roof — requires additional framing and a {@link Specifications#roofPitch} value. */
    RAISED
}
