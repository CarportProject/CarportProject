package app.persistence;

import app.entities.RoofMaterial;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoofMaterialMapperTest extends DatabaseTest {

    private final RoofMaterialMapper roofMaterialMapper = new RoofMaterialMapper();

    @Test
    void getAllRoofMaterial() throws DatabaseException {

        // AARANGE and ACT: Call the method to get all roof materials
        List<RoofMaterial> materials = roofMaterialMapper.getAllRoofMaterial(connectionPool);

        // ASSERT: Verify the results
        assertNotNull(materials, "List should not be null");
        assertTrue(materials.size() > 0, "Should have at least one roof material");

        RoofMaterial first = materials.get(0);
        assertTrue(first.getId() > 0, "ID should be positive");
        assertNotNull(first.getName(), "Name should not be null");

        // just think about colors after.
        if (first.getColor() == null) {
            System.out.println("⚠️ Warning: Color is null for " + first.getName());
        }

        assertTrue(first.getPrice() >= 0, "Price should be 0 or positive");
    }
    @Test
    void findRoofMaterialById() throws DatabaseException {
        // PART 1: Get a REAL ID from the database
        List<RoofMaterial> allMaterials = roofMaterialMapper.getAllRoofMaterial(connectionPool);

        if (allMaterials.isEmpty()) {
            fail("No roof materials found in database");
        }

        int existingId = allMaterials.get(0).getId();
        System.out.println("Testing with existing ID: " + existingId);

        // Act: Find material by ID
        RoofMaterial found = roofMaterialMapper.findRoofMaterialById(existingId, connectionPool);

        // Assert: Verify material is correct
        assertNotNull(found, "Material should be found");
        assertEquals(existingId, found.getId(), "ID should match");
        assertNotNull(found.getName(), "Name should not be null");

        // mist color!!!!!
        if (found.getColor() == null) {
            System.out.println("⚠️ Warning: Color is null for " + found.getName());
        }

        assertTrue(found.getPrice() >= 0, "Price should be 0 or positive");

        // PART 2: Find material that does NOT EXIST
        int nonExistentId = 99999;
        assertThrows(DatabaseException.class, () -> {
            roofMaterialMapper.findRoofMaterialById(nonExistentId, connectionPool);
        }, "Should throw DatabaseException when material not found");
    }
}