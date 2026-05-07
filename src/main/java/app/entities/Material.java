package app.entities;

/**
 * Represents a material used in the construction of a carport order.
 * <p>
 * Instances are created via the nested {@link Builder} class using the builder pattern,
 * which ensures all fields are set before the object is constructed.
 * </p>
 *
 * <pre>{@code
 * Material material = new Material.Builder()
 *     .id(1)
 *     .name("Wooden beam")
 *     .description("4x4 pressure-treated pine, 3 metres")
 *     .price(299)
 *     .build();
 * }</pre>
 */
// TODO: consider subclasses once the full material catalogue is defined
public class Material {

    /** Unique identifier for the material in the database. */
    private int id;

    /** Display name of the material. */
    private String name;

    /** Detailed description of the material (dimensions, type, etc.). */
    private String description;

    /** Unit price of the material in øre (whole number, no decimals). */
    private int price;

    /** Private constructor — use {@link Builder} to create instances. */
    private Material() {
    }

    /**
     * Builder for constructing {@link Material} instances.
     * <p>
     * Each setter method returns the builder itself to allow method chaining.
     * Call {@link #build()} when all desired fields have been set.
     * </p>
     */
    public static class Builder {
        private int id;
        private String name;
        private String description;
        private int price;

        /**
         * Sets the material's database ID.
         *
         * @param id the unique identifier
         * @return this builder
         */
        public Builder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the display name of the material.
         *
         * @param name the material name
         * @return this builder
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the description of the material.
         *
         * @param description the detailed description
         * @return this builder
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Sets the unit price of the material.
         *
         * @param price the price in øre
         * @return this builder
         */
        public Builder price(int price) {
            this.price = price;
            return this;
        }

        /**
         * Builds and returns a new {@link Material} with the values set on this builder.
         *
         * @return a fully constructed {@link Material} instance
         */
        public Material build() {
            Material material = new Material();
            material.id = this.id;
            material.name = this.name;
            material.description = this.description;
            material.price = this.price;
            return material;
        }
    }

}
