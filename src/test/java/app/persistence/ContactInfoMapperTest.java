package app.persistence;

import app.entities.ContactInfo;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link ContactInfoMapper}.
 * <p>
 * Each test runs against the {@code test} schema via the shared connection pool
 * provided by {@link DatabaseTest}. The database is wiped before every test,
 * so tests are fully independent of each other.
 * </p>
 */
class ContactInfoMapperTest extends DatabaseTest {

    private final ContactInfoMapper contactInfoMapper = new ContactInfoMapper();

    /**
     * Tests that a ContactInfo can be inserted into the database and retrieved correctly.
     *
     * Steps:
     * 1. Create a ContactInfo object with test data
     * 2. Call insertContactInfo() to save it to the database
     * 3. Verify that a valid ID ( > 0 ) is returned
     * 4. Retrieve the saved ContactInfo using findContactInfoById()
     * 5. Assert that all fields match the original data
     */
    @Test
    void insertContactInfo() throws DatabaseException {
        // ARRANGE: Create a ContactInfo object with test data
        ContactInfo contactInfo = new ContactInfo.Builder()
                .email("test@example.com")
                .address("Testvej 123")
                .firstName("Test")
                .lastName("Testesen")
                .postalCode(1234)
                .city("Testby")
                .phoneNumber("12345678")
                .build();

        // ACT: Insert the ContactInfo into the database and get the generated ID
        int id = contactInfoMapper.insertContactInfo(contactInfo, connectionPool);

        // ASSERT 1: Verify that we received a valid ID (database generated ID should be > 0)
        assertTrue(id > 0, "insertContactInfo should return a valid ID");

        // ACT: Retrieve the saved ContactInfo from the database using the ID
        ContactInfo saved = contactInfoMapper.findContactInfoById(id, connectionPool);

        // ASSERT 2: Verify that all fields match the original data
        assertEquals("test@example.com", saved.getEmail(), "Email should match");
        assertEquals("Testvej 123", saved.getAddress(), "Address should match");
        assertEquals("Test", saved.getFirstName(), "First name should match");
        assertEquals("Testesen", saved.getLastName(), "Last name should match");
        assertEquals(1234, saved.getPostalCode(), "Postal code should match");
        assertEquals("Testby", saved.getCity(), "City should match");
        assertEquals("12345678", saved.getPhoneNumber(), "Phone number should match");
    }

    /**
     * Tests that findContactInfoById() works correctly for both existing and non-existing IDs.
     *
     * Part 1: Find a ContactInfo that EXISTS
     * - Create and insert a ContactInfo
     * - Retrieve it by ID
     * - Verify all data matches
     *
     * Part 2: Find a ContactInfo that does NOT EXIST
     * - Try to find a ContactInfo with an ID that doesn't exist (99999)
     * - Expect a DatabaseException to be thrown
     */
    @Test
    void findContactInfoById() throws DatabaseException {

        // PART 1: Test finding a ContactInfo that EXISTS
        // ARRANGE: Create and save a ContactInfo
        ContactInfo contactInfo = new ContactInfo.Builder()
                .email("find@example.com")
                .address("Findvej 456")
                .firstName("Find")
                .lastName("Findesen")
                .postalCode(5678)
                .city("Findby")
                .phoneNumber("87654321")
                .build();

        int id = contactInfoMapper.insertContactInfo(contactInfo, connectionPool);

        // ACT: Find the ContactInfo by its ID
        ContactInfo found = contactInfoMapper.findContactInfoById(id, connectionPool);

        // ASSERT: Verify all data matches what we saved
        assertNotNull(found, "Found contact info should not be null");
        assertEquals(id, found.getId(), "ID should match");
        assertEquals("find@example.com", found.getEmail(), "Email should match");
        assertEquals("Findvej 456", found.getAddress(), "Address should match");
        assertEquals("Find", found.getFirstName(), "First name should match");
        assertEquals("Findesen", found.getLastName(), "Last name should match");
        assertEquals(5678, found.getPostalCode(), "Postal code should match");
        assertEquals("Findby", found.getCity(), "City should match");
        assertEquals("87654321", found.getPhoneNumber(), "Phone number should match");

        //PART 2: Test finding a ContactInfo that does NOT EXIST
        // ARRANGE: Use an ID that does not exist in the database
        int nonExistentId = 99999;
        // ACT & ASSERT: Expect a DatabaseException when trying to find non-existent ID
        assertThrows(DatabaseException.class, () -> {
            contactInfoMapper.findContactInfoById(nonExistentId, connectionPool);
        }, "Should throw DatabaseException when contact info not found");
    }
}