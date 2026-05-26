package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link MaterialsMapper}.
 * Each test runs against the {@code test} schema via DatabaseTest.
 * Database is wiped before every test.
 */
class MaterialsMapperTest extends DatabaseTest {

    private final MaterialsMapper materialsMapper = new MaterialsMapper();
    private final RoofMaterialMapper roofMaterialMapper = new RoofMaterialMapper();

    // ========== HELPER METHODS ==========

    /**
     * Helper: Get an existing roof material ID.
     */
    private int getExistingRoofMaterialId() throws DatabaseException {
        List<RoofMaterial> materials = roofMaterialMapper.getAllRoofMaterial(connectionPool);
        if (materials.isEmpty()) {
            fail("No roof materials found in database. Please insert test data first.");
        }
        return materials.get(0).getId();
    }

    /**
     * Helper: Create a test order for material list tests.
     */
    private int createTestOrder() throws DatabaseException {
        int roofMaterialId = getExistingRoofMaterialId();

        ContactInfo contactInfo = new ContactInfo.Builder()
                .email("materialtest@example.com")
                .address("Testvej 123")
                .firstName("Test")
                .lastName("Testesen")
                .postalCode(1234)
                .city("Testby")
                .phoneNumber("12345678")
                .build();

        Workshop workshop = new Workshop.Builder()
                .widthCm(200)
                .lengthCm(300)
                .build();

        RoofMaterial roofMaterial = new RoofMaterial.Builder()
                .id(roofMaterialId)
                .build();

        Specifications specifications = new Specifications.Builder()
                .roofType(RoofType.FLAT)
                .roofMaterial(roofMaterial)
                .widthCm(500)
                .lengthCm(600)
                .roofPitch(0)
                .build();

        Order order = new Order.Builder()
                .contactInfo(contactInfo)
                .workshop(workshop)
                .specifications(specifications)
                .orderDetails(new OrderDetails("Test order", OrderStatus.PENDING))
                .build();

        return OrderMapper.insertOrder(order, connectionPool);
    }

    // ========== TESTS ==========

    /**
     * TEST 1: Get material by ID.
     * Verifies that a material can be fetched from the database.
     */
    @Test
    void getMaterialById() throws DatabaseException {
        // ARRANGE: Get all materials to find an existing ID
        List<Material> allMaterials = materialsMapper.getAllMaterials(connectionPool);

        // Skip test if no materials exist
        if (allMaterials.isEmpty()) {
            System.out.println("Skipping test - no materials found in database");
            return;
        }

        int existingId = allMaterials.get(0).getId();

        // ACT: Get material by ID
        Material material = materialsMapper.getMaterialById(existingId, connectionPool);

        // ASSERT: Verify material is found
        assertNotNull(material, "Material should be found");
        assertEquals(existingId, material.getId(), "ID should match");
        assertNotNull(material.getName(), "Name should not be null");
        assertTrue(material.getPrice() >= 0, "Price should be 0 or positive");
    }

    /**
     * TEST 2: Get all materials.
     * Verifies that the method returns a list of materials (may be empty).
     */
    @Test
    void getAllMaterials() throws DatabaseException {
        // ACT: Get all materials
        List<Material> materials = materialsMapper.getAllMaterials(connectionPool);

        // ASSERT: List should not be null
        assertNotNull(materials, "Material list should not be null");

        // If there are materials, verify first one has valid data
        if (!materials.isEmpty()) {
            Material first = materials.get(0);
            assertTrue(first.getId() > 0, "ID should be positive");
            assertNotNull(first.getName(), "Name should not be null");
        }
    }

    /**
     * TEST 3: Find material list by order ID.
     * Verifies that materials associated with an order can be fetched.
     */
    @Test
    void findMaterialListById() throws DatabaseException {
        // PART 1: Test with non-existent order - should return empty list
        int nonExistentOrderId = -1;

        // ACT: Get material list for non-existent order
        List<MaterialListEntry> materialList = materialsMapper.findMaterialListById(nonExistentOrderId, connectionPool);

        // ASSERT: Should return empty list (not null)
        assertNotNull(materialList, "Material list should not be null");
        assertTrue(materialList.isEmpty(), "Material list should be empty for non-existent order");

        // PART 2: Create an order with materials and verify we can find them
        List<Material> allMaterials = materialsMapper.getAllMaterials(connectionPool);
        if (allMaterials.isEmpty()) {
            System.out.println("Skipping part 2 - no materials found");
            return;
        }

        // Create order and add material
        int orderId = createTestOrder();
        int materialId = allMaterials.get(0).getId();
        materialsMapper.insertMaterialList(orderId, materialId, 5, "Test material", connectionPool);

        // ACT: Get material list for the order
        List<MaterialListEntry> foundList = materialsMapper.findMaterialListById(orderId, connectionPool);

        // ASSERT: Should contain the inserted material
        assertNotNull(foundList, "Material list should not be null");
        assertFalse(foundList.isEmpty(), "Material list should contain the inserted material");

        boolean found = foundList.stream()
                .anyMatch(entry -> entry.material().getId() == materialId);
        assertTrue(found, "Inserted material should be in the list");
    }

    /**
     * TEST 4: Update material information.
     * Verifies that material fields can be updated.
     */
    @Test
    void updateMaterialInfo() throws DatabaseException {
        // ARRANGE: Get an existing material
        List<Material> allMaterials = materialsMapper.getAllMaterials(connectionPool);
        if (allMaterials.isEmpty()) {
            System.out.println("Skipping test - no materials found");
            return;
        }

        Material originalMaterial = allMaterials.get(0);
        int materialId = originalMaterial.getId();
        String originalName = originalMaterial.getName();
        String newName = originalName + "_UPDATED";

        // Create updated material (only change the name)
        Material updatedMaterial = new Material.Builder()
                .id(materialId)
                .name(newName)
                .build();

        // ACT: Update the material
        materialsMapper.updateMaterialInfo(updatedMaterial, connectionPool);

        // ASSERT: Verify the update worked
        Material fetchedMaterial = materialsMapper.getMaterialById(materialId, connectionPool);
        assertEquals(newName, fetchedMaterial.getName(), "Material name should be updated");

        // Clean up: revert the name back to original
        Material revertMaterial = new Material.Builder()
                .id(materialId)
                .name(originalName)
                .build();
        materialsMapper.updateMaterialInfo(revertMaterial, connectionPool);
    }

    /**
     * TEST 5: Insert a material into material_list.
     * Verifies that a material can be associated with an order.
     */
    @Test
    void insertMaterialList() throws DatabaseException {
        // ARRANGE: Get an existing material
        List<Material> allMaterials = materialsMapper.getAllMaterials(connectionPool);
        if (allMaterials.isEmpty()) {
            System.out.println("Skipping test - no materials found");
            return;
        }

        int materialId = allMaterials.get(0).getId();
        int amount = 10;
        String description = "Test material for order";

        // Create an order
        int orderId = createTestOrder();

        // ACT: Insert material into material_list
        materialsMapper.insertMaterialList(orderId, materialId, amount, description, connectionPool);

        // ASSERT: Verify material was inserted
        List<MaterialListEntry> materialList = materialsMapper.findMaterialListById(orderId, connectionPool);
        assertNotNull(materialList, "Material list should not be null");
        assertFalse(materialList.isEmpty(), "Material list should contain at least one entry");

        // Verify the specific material is in the list with correct amount
        boolean found = materialList.stream()
                .anyMatch(entry -> entry.material().getId() == materialId
                        && entry.amount() == amount
                        && entry.description().equals(description));
        assertTrue(found, "Inserted material should be found with correct amount and description");
    }
}