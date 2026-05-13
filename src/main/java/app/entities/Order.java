package app.entities;

import java.util.List;

/**
 * Represents a carport order placed by a contactInfo.
 * <p>
 * Instances are created via the nested {@link Builder} class using the builder pattern,
 * which ensures all fields are set before the object is constructed.
 * </p>
 *
 * <pre>{@code
 * Order order = new Order.Builder()
 *     .id(1)
 *     .contactInfo(contactInfo)
 *     .specifications(specs)
 *     .workshop(workshop)
 *     .remarks("Delivery before noon")
 *     .build();
 * }</pre>
 */
public class Order {

    /** Unique identifier for the order in the database. */
    private int id;

    /** The contactInfo who placed this order. */
    private ContactInfo contactInfo;

    /** List of materials required to fulfil this order. */
    private List<Material> materialList;

    /** The carport dimensions and roof configuration chosen by the contactInfo. */
    private Specifications specifications;

    /** Optional workshop (shed) attached to the carport, if requested. */
    private Workshop workshop;

    /** Free-text remarks or special instructions for this order. */
    private String remarks;

    /** Private constructor — use {@link Builder} to create instances. */
    private Order() {
    }

    /** @return the order's database ID */
    public int getId() { return id; }

    /** @return the contactInfo who placed this order */
    public ContactInfo getCustomer() { return contactInfo; }

    /** @return the list of materials required for this order */
    public List<Material> getMaterialList() { return materialList; }

    /** @return the carport specifications for this order */
    public Specifications getSpecifications() { return specifications; }

    /** @return the attached workshop, or {@code null} if none */
    public Workshop getWorkshop() { return workshop; }

    /** @return any free-text remarks for this order */
    public String getRemarks() { return remarks; }

    /**
     * Builder for constructing {@link Order} instances.
     * <p>
     * Each setter method returns the builder itself to allow method chaining.
     * Call {@link #build()} when all desired fields have been set.
     * </p>
     */
    public static class Builder {
        private int id;
        private ContactInfo contactInfo;
        private List<Material> materialList;
        private Specifications specifications;
        private Workshop workshop;
        private String remarks;

        /**
         * Sets the order's database ID.
         *
         * @param id the unique identifier
         * @return this builder
         */
        public Builder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the contactInfo who placed the order.
         *
         * @param contactInfo the contactInfo
         * @return this builder
         */
        public Builder contactInfo(ContactInfo contactInfo) {
            this.contactInfo = contactInfo;
            return this;
        }

        /**
         * Sets the list of materials required for the order.
         *
         * @param materialList the list of materials
         * @return this builder
         */
        public Builder materialList(List<Material> materialList) {
            this.materialList = materialList;
            return this;
        }

        /**
         * Sets the carport specifications for the order.
         *
         * @param specifications the specifications
         * @return this builder
         */
        public Builder specifications(Specifications specifications) {
            this.specifications = specifications;
            return this;
        }

        /**
         * Sets the optional workshop attached to the carport.
         *
         * @param workshop the workshop, or {@code null} if none
         * @return this builder
         */
        public Builder workshop(Workshop workshop) {
            this.workshop = workshop;
            return this;
        }

        /**
         * Sets any free-text remarks or special instructions for the order.
         *
         * @param remarks the remarks
         * @return this builder
         */
        public Builder remarks(String remarks) {
            this.remarks = remarks;
            return this;
        }

        /**
         * Builds and returns a new {@link Order} with the values set on this builder.
         *
         * @return a fully constructed {@link Order} instance
         */
        public Order build() {
            Order order = new Order();
            order.id = this.id;
            order.contactInfo = this.contactInfo;
            order.materialList = this.materialList;
            order.specifications = this.specifications;
            order.workshop = this.workshop;
            order.remarks = this.remarks;
            return order;
        }
    }

}
