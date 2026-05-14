package app.entities;

/**
 * Represents a roof style (cladding product) available for a carport.
 * <p>
 * Instances are created via the nested {@link Builder} class using the builder pattern,
 * which ensures all fields are set before the object is constructed.
 * </p>
 *
 * <pre>{@code
 * RoofMaterial roofStyle = new RoofMaterial.Builder()
 *     .id(1)
 *     .name("Classic tile")
 *     .color("Anthracite")
 *     .price(15000)
 *     .build();
 * }</pre>
 */
public class RoofMaterial {

    /**
     * Unique identifier for the roof style in the database.
     */
    private int id;

    /**
     * Display name of the roof style (e.g. "Classic tile", "Standing seam").
     */
    private String name;

    /**
     * Colour of the roof cladding (e.g. "Anthracite", "Red").
     */
    private String color;

    /**
     * Price per square metre in øre (whole number, no decimals).
     */
    private int price;

    /**
     * Type of roof the tiles can be applied on
     */
    private RoofType roofType;

    private String displayName;

    /**
     * Private constructor — use {@link Builder} to create instances.
     */
    private RoofMaterial() {
    }

    /**
     * @return the roof style's database ID
     */
    public int getId() {
        return id;
    }

    /**
     * @return the display name of the roof style
     */
    public String getName() {
        return name;
    }

    /**
     * @return the colour of the roof cladding
     */
    public String getColor() {
        return color;
    }

    /**
     * @return the price per square metre in dkk
     */
    public int getPrice() {
        return price;
    }

    /**
     * @return the type of roof tile can be used on
     */
    public RoofType getRoofType() {
        return roofType;
    }

    public String getDisplayName() {
        String display = null;
        if(color == null || color.isBlank()){
            display = name;
        } else {
            display = name + " - " + color;
        }
        return display;
    }

    /**
     * Builder for constructing {@link RoofMaterial} instances.
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
        private RoofType roofType;
        private String displayName;

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
         * Sets the {@link RoofType}.
         *
         * @param roofType tile can be used on
         * @return this builder
         */
        public Builder roofType(RoofType roofType) {
            this.roofType = roofType;
            return this;
        }

        /**
         * Builds and returns a new {@link RoofMaterial} with the values set on this builder.
         *
         * @return a fully constructed {@link RoofMaterial} instance
         */
        public RoofMaterial build() {
            RoofMaterial roofMaterial = new RoofMaterial();
            roofMaterial.id = this.id;
            roofMaterial.name = this.name;
            roofMaterial.color = this.color;
            roofMaterial.price = this.price;
            roofMaterial.roofType = this.roofType;
            return roofMaterial;
        }
    }

}
