package app.entities;

/**
 * Represents an optional workshop (shed) that can be attached to a carport.
 * <p>
 * Instances are created via the nested {@link Builder} class using the builder pattern,
 * which ensures all fields are set before the object is constructed.
 * </p>
 *
 * <pre>{@code
 * Workshop workshop = new Workshop.Builder()
 *     .id(1)
 *     .widthCm(240)
 *     .lengthCm(300)
 *     .build();
 * }</pre>
 */
public class Workshop {

    /** Unique identifier for the workshop in the database. */
    private int id;

    /** Width of the workshop in centimetres. */
    private int widthCm;

    /** Length of the workshop in centimetres. */
    private int lengthCm;

    /** Private constructor — use {@link Builder} to create instances. */
    private Workshop() {
    }

    /** @return the workshop's database ID */
    public int getId() { return id; }

    /** @return the width of the workshop in centimetres */
    public int getWidthCm() { return widthCm; }

    /** @return the length of the workshop in centimetres */
    public int getLengthCm() { return lengthCm; }

    /**
     * Builder for constructing {@link Workshop} instances.
     * <p>
     * Each setter method returns the builder itself to allow method chaining.
     * Call {@link #build()} when all desired fields have been set.
     * </p>
     */
    public static class Builder {
        private int id;
        private int widthCm;
        private int lengthCm;

        /**
         * Sets the workshop's database ID.
         *
         * @param id the unique identifier
         * @return this builder
         */
        public Builder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the width of the workshop.
         *
         * @param widthCm the width in centimetres
         * @return this builder
         */
        public Builder widthCm(int widthCm) {
            this.widthCm = widthCm;
            return this;
        }

        /**
         * Sets the length of the workshop.
         *
         * @param lengthCm the length in centimetres
         * @return this builder
         */
        public Builder lengthCm(int lengthCm) {
            this.lengthCm = lengthCm;
            return this;
        }

        /**
         * Builds and returns a new {@link Workshop} with the values set on this builder.
         *
         * @return a fully constructed {@link Workshop} instance
         */
        public Workshop build() {
            Workshop workshop = new Workshop();
            workshop.id = this.id;
            workshop.widthCm = this.widthCm;
            workshop.lengthCm = this.lengthCm;
            return workshop;
        }
    }

}
