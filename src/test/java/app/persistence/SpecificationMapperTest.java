package app.persistence;

import app.entities.RoofMaterial;
import app.entities.RoofType;
import app.entities.Specifications;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link SpecificationMapper}.
 * <p>
 * Each test runs against the {@code test} schema via the shared connection pool
 * provided by {@link DatabaseTest}. The database is wiped before every test,
 * so tests are fully independent of each other.
 * </p>
 */
class SpecificationMapperTest extends DatabaseTest {

    private final SpecificationMapper specificationMapper = new SpecificationMapper();
    private final RoofMaterialMapper roofMaterialMapper = new RoofMaterialMapper();

    // Get an existing roof material ID from database
    private int getExistingRoofMaterialId() throws DatabaseException {
        List<RoofMaterial> materials = roofMaterialMapper.getAllRoofMaterial(connectionPool);
        if (materials.isEmpty()) {
            fail("No roof materials found in database. Please insert test data first.");
        }
        return materials.get(0).getId();
    }

    // Create a test Specifications object
    private Specifications createTestSpecifications(int roofMaterialId) {
        RoofMaterial roofMaterial = new RoofMaterial.Builder()
                .id(roofMaterialId)
                .build();

        return new Specifications.Builder()
                .roofType(RoofType.FLAT)
                .roofMaterial(roofMaterial)
                .widthCm(300)
                .lengthCm(400)
                .roofPitch(0)
                .build();
    }

    @Test
    void insertSpecifications() throws DatabaseException {
        // ARRANGE: Get an existing roof material ID and create specifications
        int roofMaterialId = getExistingRoofMaterialId();
        Specifications specs = createTestSpecifications(roofMaterialId);

        // ACT: Insert specifications into database
        int generatedId = specificationMapper.insertSpecifications(specs, connectionPool);

        // ASSERT 1: Verify we got a valid ID
        assertTrue(generatedId > 0, "insertSpecifications should return a valid ID");

        // ASSERT 2: Verify the data was saved correctly
        Specifications saved = specificationMapper.findSpecificationsById(generatedId, connectionPool);
        assertNotNull(saved, "Saved specifications should not be null");
        assertEquals(RoofType.FLAT, saved.getRoofType(), "Roof type should match");
        assertEquals(roofMaterialId, saved.getRoofMaterial().getId(), "Roof material ID should match");
        assertEquals(300, saved.getWidthCm(), "Width should match");
        assertEquals(400, saved.getLengthCm(), "Length should match");
        assertEquals(0, saved.getRoofPitch(), "Roof pitch should match");
    }

    @Test
    void findSpecificationsById() throws DatabaseException {
        // PART 1: Test finding specifications that EXISTS

        // ARRANGE: Insert a specifications first
        int roofMaterialId = getExistingRoofMaterialId();
        Specifications specs = createTestSpecifications(roofMaterialId);
        int insertedId = specificationMapper.insertSpecifications(specs, connectionPool);

        // ACT: Find specifications by ID
        Specifications found = specificationMapper.findSpecificationsById(insertedId, connectionPool);

        // ASSERT: Verify all data matches
        assertNotNull(found, "Specifications should be found");
        assertEquals(insertedId, found.getId(), "ID should match");
        assertEquals(RoofType.FLAT, found.getRoofType(), "Roof type should match");
        assertEquals(roofMaterialId, found.getRoofMaterial().getId(), "Roof material ID should match");
        assertEquals(300, found.getWidthCm(), "Width should match");
        assertEquals(400, found.getLengthCm(), "Length should match");
        assertEquals(0, found.getRoofPitch(), "Roof pitch should match");

        // PART 2: Test finding specifications that does NOT EXIST

        // ARRANGE: Use an ID that doesn't exist
        int nonExistentId = -1;

        // ACT & ASSERT: Expect a DatabaseException
        assertThrows(DatabaseException.class, () -> {
            specificationMapper.findSpecificationsById(nonExistentId, connectionPool);
        }, "Should throw DatabaseException when specifications not found");
    }
}