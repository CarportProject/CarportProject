package app.entities;

/**
 * Represents a customer in the carport system.
 * <p>
 * Instances are created via the nested {@link Builder} class using the builder pattern,
 * which ensures all fields are set before the object is constructed.
 * </p>
 *
 * <pre>{@code
 * ContactInfo customer = new ContactInfo.Builder()
 *     .id(1)
 *     .firstName("John")
 *     .lastName("Doe")
 *     .email("john@example.com")
 *     .build();
 * }</pre>
 */
public class ContactInfo {

    /**
     * Unique identifier for the customer in the database.
     */
    private int id;

    /**
     * ContactInfo's email address, used for login and communication.
     */
    private String email;

    /**
     * Street address of the customer.
     */
    private String address;

    /**
     * ContactInfo's first name.
     */
    private String firstName;

    /**
     * ContactInfo's last name.
     */
    private String lastName;

    /**
     * Danish postal code (postnummer) for the customer's address.
     */
    private int postalCode;

    /**
     * City corresponding to the customer's postal code.
     */
    private String city;

    /**
     * ContactInfo's phone number.
     */
    private String phoneNumber;

    /**
     * Potential user connected to the order
     */
    User user;

    /**
     * Private constructor — use {@link Builder} to create instances.
     */
    private ContactInfo() {
    }

    /** @return the customer's database ID */
    public int getId() { return id; }

    /** @return the customer's email address */
    public String getEmail() { return email; }

    /** @return the customer's street address */
    public String getAddress() { return address; }

    /** @return the customer's first name */
    public String getFirstName() { return firstName; }

    /** @return the customer's last name */
    public String getLastName() { return lastName; }

    /** @return the customer's postal code */
    public int getPostalCode() { return postalCode; }

    /** @return the city corresponding to the customer's postal code */
    public String getCity() { return city; }

    /** @return the customer's phone number */
    public String getPhoneNumber() { return phoneNumber; }

    /** @return the user linked to this customer, or {@code null} if none */
    public User getUser() { return user; }

    /**
     * Builder for constructing {@link ContactInfo} instances.
     * <p>
     * Each setter method returns the builder itself to allow method chaining.
     * Call {@link #build()} when all desired fields have been set.
     * </p>
     */
    public static class Builder {
        private int id;
        private String email;
        private String address;
        private String firstName;
        private String lastName;
        private int postalCode;
        private String city;
        private String phoneNumber;
        private User user;

        /**
         * Sets the customer's database ID.
         *
         * @param id the unique identifier
         * @return this builder
         */
        public Builder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the customer's email address.
         *
         * @param email the email address
         * @return this builder
         */
        public Builder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * Sets the customer's street address.
         *
         * @param address the street address
         * @return this builder
         */
        public Builder address(String address) {
            this.address = address;
            return this;
        }

        /**
         * Sets the customer's first name.
         *
         * @param firstName the first name
         * @return this builder
         */
        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        /**
         * Sets the customer's last name.
         *
         * @param lastName the last name
         * @return this builder
         */
        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        /**
         * Sets the customer's postal code.
         *
         * @param postalCode the Danish postal code (postnummer)
         * @return this builder
         */
        public Builder postalCode(int postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        /**
         * Sets the city corresponding to the customer's postal code.
         *
         * @param city the city name
         * @return this builder
         */
        public Builder city(String city) {
            this.city = city;
            return this;
        }

        /**
         * Sets the customer's phone number.
         *
         * @param phoneNumber the phone number
         * @return this builder
         */
        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        /**
         * Links the customer to a user
         *
         * @param user the user
         * @return this builder
         */
        public Builder user(User user) {
            this.user = user;
            return this;
        }

        /**
         * Builds and returns a new {@link ContactInfo} with the values set on this builder.
         *
         * @return a fully constructed {@link ContactInfo} instance
         */
        public ContactInfo build() {
            ContactInfo contactInfo = new ContactInfo();
            contactInfo.id = this.id;
            contactInfo.email = this.email;
            contactInfo.address = this.address;
            contactInfo.firstName = this.firstName;
            contactInfo.lastName = this.lastName;
            contactInfo.postalCode = this.postalCode;
            contactInfo.city = this.city;
            contactInfo.phoneNumber = this.phoneNumber;
            contactInfo.user = this.user;
            return contactInfo;
        }
    }

}
