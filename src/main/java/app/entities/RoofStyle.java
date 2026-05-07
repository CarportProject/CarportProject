package app.entities;

/**
 * Represents a roof style (cladding product) available for a carport.
 * <p>
 * Instances are created via the nested {@link Builder} class using the builder pattern,
 * which ensures all fields are set before the object is constructed.
 * </p>
 *
 * <pre>{@code
 * RoofStyle roofStyle = new RoofStyle.Builder()
 *     .id(1)
 *     .name("Classic tile")
 *     .color("Anthracite")
 *     .price(15000)
 *     .build();
 * }</pre>
 */
public class RoofStyle {

    /** Unique identifier for the roof style in the database. */
    private int id;

    /** Display name of the roof style (e.g. "Classic tile", "Standing seam"). */
    private String name;

    /** Colour of the roof cladding (e.g. "Anthracite", "Red"). */
    private String color;

    /** Price per square metre in øre (whole number, no decimals). */
    private int price;

    /** Private constructor — use {@link Builder} to create instances. */
    private RoofStyle() {
    }

    /**
     * Builder for constructing {@link RoofStyle} instances.
     * <p>
     * Each setter method returns the builder itself to allow method chaining.
     * Call {@link #build()} when all desired fields have been set.
     * </p>
     */
    public static class Builder {
        private int id;
        private String name;
        private String color;
        private int price;

        /**
         * Sets the roof style's database ID.
         *
         * @param id the unique identifier
         * @return this builder
         */
        public Builder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the display name of the roof style.
         *
         * @param name the roof style name
         * @return this builder
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the colour of the roof cladding.
         *
         * @param color the colour name
         * @return this builder
         */
        public Builder color(String color) {
            this.color = color;
            return this;
        }

        /**
         * Sets the price per square metre.
         *
         * @param price the price in øre
         * @return this builder
         */
        public Builder price(int price) {
            this.price = price;
            return this;
        }

        /**
         * Builds and returns a new {@link RoofStyle} with the values set on this builder.
         *
         * @return a fully constructed {@link RoofStyle} instance
         */
        public RoofStyle build() {
            RoofStyle roofStyle = new RoofStyle();
            roofStyle.id = this.id;
            roofStyle.name = this.name;
            roofStyle.color = this.color;
            roofStyle.price = this.price;
            return roofStyle;
        }
    }

}
