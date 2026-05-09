package app.entities;

/**
 * Represents a registered user of the carport system.
 * <p>
 * A user has a {@link Role} that controls what they are allowed to do in the system.
 * Instances are created via the nested {@link Builder} class using the builder pattern,
 * which ensures all fields are set before the object is constructed.
 * </p>
 *
 * <pre>{@code
 * User user = new User.Builder()
 *     .id(1)
 *     .email("jane@example.com")
 *     .password("hashed-password")
 *     .role(Role.CUSTOMER)
 *     .build();
 * }</pre>
 */
public class User {

    /** Unique identifier for the user in the database. */
    private int id;

    /** Email address used to log in. */
    private String email;

    /** Hashed password for authentication. */
    private String password;

    /** The role that determines what actions this user may perform. */
    private Role role;

    /** Private constructor — use {@link Builder} to create instances. */
    private User() {
    }

    /**
     * Returns the user's database ID.
     *
     * @return the unique identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the user's database ID.
     *
     * @param id the unique identifier
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the user's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email the email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the user's hashed password.
     *
     * @return the hashed password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's hashed password.
     *
     * @param password the hashed password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the role assigned to this user.
     *
     * @return the role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the role assigned to this user.
     *
     * @param role the role
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Builder for constructing {@link User} instances.
     * <p>
     * Each setter method returns the builder itself to allow method chaining.
     * Call {@link #build()} when all desired fields have been set.
     * </p>
     */
    public static class Builder {
        private int id;
        private String email;
        private String password;
        private Role role;

        /**
         * Sets the user's database ID.
         *
         * @param id the unique identifier
         * @return this builder
         */
        public Builder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the user's email address.
         *
         * @param email the email address
         * @return this builder
         */
        public Builder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * Sets the user's hashed password.
         *
         * @param password the hashed password
         * @return this builder
         */
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * Sets the role assigned to this user.
         *
         * @param role the role
         * @return this builder
         */
        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        /**
         * Builds and returns a new {@link User} with the values set on this builder.
         *
         * @return a fully constructed {@link User} instance
         */
        public User build() {
            User user = new User();
            user.id = this.id;
            user.email = this.email;
            user.password = this.password;
            user.role = this.role;
            return user;
        }
    }

}
