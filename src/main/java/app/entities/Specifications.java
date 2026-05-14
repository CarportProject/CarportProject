package app.entities;

/**
 * Represents the technical specifications for a carport order.
 * <p>
 * Instances are created via the nested {@link Builder} class using the builder pattern,
 * which ensures all fields are set before the object is constructed.
 * </p>
 *
 * <pre>{@code
 * Specifications specs = new Specifications.Builder()
 *     .id(1)
 *     .roofType(RoofType.RAISED)
 *     .roofMaterial(roofMaterial)
 *     .widthCm(600)
 *     .lengthCm(780)
 *     .roofPitch(25)
 *     .build();
 * }</pre>
 */
public class Specifications {

    /** Unique identifier for the specifications record in the database. */
    private int id;

    /** Whether the roof is flat or raised. */
    private RoofType roofType;

    /** The chosen roof cladding style and colour. */
    private RoofMaterial roofMaterial;

    /** Width of the carport in centimetres. */
    private int widthCm;

    /** Length of the carport in centimetres. */
    private int lengthCm;

    /** Pitch of the roof in degrees (only relevant for {@link RoofType#RAISED}). */
    private int roofPitch;

    /** Private constructor — use {@link Builder} to create instances. */
    private Specifications() {
    }

    /** @return the specifications' database ID */
    public int getId() { return id; }

    /** @return whether the roof is flat or raised */
    public RoofType getRoofType() { return roofType; }

    /** @return the chosen roof cladding style */
    public RoofMaterial getRoofMaterial() { return roofMaterial; }

    /** @return the width of the carport in centimetres */
    public int getWidthCm() { return widthCm; }

    /** @return the length of the carport in centimetres */
    public int getLengthCm() { return lengthCm; }

    /** @return the pitch of the roof in degrees */
    public int getRoofPitch() { return roofPitch; }

    /**
     * Builder for constructing {@link Specifications} instances.
     * <p>
     * Each setter method returns the builder itself to allow method chaining.
     * Call {@link #build()} when all desired fields have been set.
     * </p>
     */
    public static class Builder {
        private int id;
        private RoofType roofType;
        private RoofMaterial roofMaterial;
        private int widthCm;
        private int lengthCm;
        private int roofPitch;

        /**
         * Sets the specifications' database ID.
         *
         * @param id the unique identifier
         * @return this builder
         */
        public Builder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Sets whether the roof is flat or raised.
         *
         * @param roofType the roof type
         * @return this builder
         */
        public Builder roofType(RoofType roofType) {
            this.roofType = roofType;
            return this;
        }

        /**
         * Sets the chosen roof cladding style.
         *
         * @param roofMaterial the roof style
         * @return this builder
         */
        public Builder roofMaterial(RoofMaterial roofMaterial) {
            this.roofMaterial = roofMaterial;
            return this;
        }

        /**
         * Sets the width of the carport.
         *
         * @param widthCm the width in centimetres
         * @return this builder
         */
        public Builder widthCm(int widthCm) {
            this.widthCm = widthCm;
            return this;
        }

        /**
         * Sets the length of the carport.
         *
         * @param lengthCm the length in centimetres
         * @return this builder
         */
        public Builder lengthCm(int lengthCm) {
            this.lengthCm = lengthCm;
            return this;
        }

        /**
         * Sets the pitch of the roof in degrees.
         *
         * @param roofPitch the roof pitch in degrees
         * @return this builder
         */
        public Builder roofPitch(int roofPitch) {
            this.roofPitch = roofPitch;
            return this;
        }

        /**
         * Builds and returns a new {@link Specifications} with the values set on this builder.
         *
         * @return a fully constructed {@link Specifications} instance
         */
        public Specifications build() {
            Specifications specifications = new Specifications();
            specifications.id = this.id;
            specifications.roofType = this.roofType;
            specifications.roofMaterial = this.roofMaterial;
            specifications.widthCm = this.widthCm;
            specifications.lengthCm = this.lengthCm;
            specifications.roofPitch = this.roofPitch;
            return specifications;
        }
    }

}
