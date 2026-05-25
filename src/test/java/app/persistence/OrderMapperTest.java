package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link OrderMapper}.
 * Each test runs against the {@code test} schema via DatabaseTest.
 * Database is wiped before every test.
 */
class OrderMapperTest extends DatabaseTest {

    private final OrderMapper orderMapper = new OrderMapper();
    private final ContactInfoMapper contactInfoMapper = new ContactInfoMapper();
    private final RoofMaterialMapper roofMaterialMapper = new RoofMaterialMapper();
    private final SpecificationMapper specificationMapper = new SpecificationMapper();
    private final WorkshopMapper workshopMapper = new WorkshopMapper();

    // ========== HELPER METHODS (reusable test data) ==========

    /**
     * Get an existing roof material ID from database.
     * Required for creating Specifications.
     */
    private int getExistingRoofMaterialId() throws DatabaseException {
        List<RoofMaterial> materials = roofMaterialMapper.getAllRoofMaterial(connectionPool);
        if (materials.isEmpty()) {
            fail("No roof materials found in database. Please insert test data first.");
        }
        return materials.get(0).getId();
    }

    /**
     * Create a test ContactInfo object.
     */
    private ContactInfo createTestContactInfo() {
        return new ContactInfo.Builder()
                .email("order@example.com")
                .address("Ordervej 123")
                .firstName("Order")
                .lastName("Testesen")
                .postalCode(1234)
                .city("Orderby")
                .phoneNumber("12345678")
                .build();
    }

    /**
     * Create a test Workshop object.
     */
    private Workshop createTestWorkshop() {
        return new Workshop.Builder()
                .id(10)
                .widthCm(200)
                .lengthCm(300)
                .build();
    }

    /**
     * Create a test Specifications object.
     */
    private Specifications createTestSpecifications(int roofMaterialId) {
        RoofMaterial roofMaterial = new RoofMaterial.Builder()
                .id(roofMaterialId)
                .build();

        return new Specifications.Builder()
                .roofType(RoofType.FLAT)
                .roofMaterial(roofMaterial)
                .widthCm(500)
                .lengthCm(600)
                .roofPitch(0)
                .build();
    }

    /**
     * Create a complete test Order.
     */
    private Order createTestOrder() throws DatabaseException {
        int roofMaterialId = getExistingRoofMaterialId();
        ContactInfo contactInfo = createTestContactInfo();
        Workshop workshop = createTestWorkshop();
        Specifications specifications = createTestSpecifications(roofMaterialId);
        OrderDetails orderDetails = new OrderDetails("Test remark", OrderStatus.PENDING, 0.0, UUID.randomUUID());

        return new Order.Builder()
                .contactInfo(contactInfo)
                .workshop(workshop)
                .specifications(specifications)
                .orderDetails(orderDetails)
                .build();
    }

    // ========== TESTS ==========

    /**
     * TEST 1: Insert an order and find it by ID.
     * Verifies: insert, find, and all related objects (ContactInfo, Specifications, Workshop)
     */
    @Test
    void insertAndFindOrder() throws DatabaseException {
        // ARRANGE: Create a test order
        Order order = createTestOrder();

        // ACT: Insert order and get the generated ID
        int orderId = orderMapper.insertOrder(order, connectionPool);
        assertTrue(orderId > 0, "Order ID should be positive");

        // ACT: Retrieve the order by ID
        Order foundOrder = orderMapper.getOrderById(orderId, connectionPool);

        // ASSERT: Verify order exists and has correct data
        assertNotNull(foundOrder, "Order should be found");
        assertEquals(orderId, foundOrder.getId(), "Order ID should match");
        assertEquals("Test remark", foundOrder.getOrderDetails().remark(), "Remark should match");
        assertEquals(OrderStatus.PENDING, foundOrder.getOrderDetails().status(), "Status should match");

        // ASSERT: Verify related objects are saved correctly
        assertNotNull(foundOrder.getContactInfo(), "ContactInfo should not be null");
        assertEquals("order@example.com", foundOrder.getContactInfo().getEmail(), "Contact email should match");

        assertNotNull(foundOrder.getSpecifications(), "Specifications should not be null");
        assertEquals(500, foundOrder.getSpecifications().getWidthCm(), "Width should match");

        assertNotNull(foundOrder.getWorkshop(), "Workshop should not be null");
        assertEquals(200, foundOrder.getWorkshop().getWidthCm(), "Workshop width should match");
    }

    /**
     * TEST 2: Try to find an order that does NOT exist.
     * Expected: DatabaseException
     */
    @Test
    void findOrderByIdNotFound() {
        // ARRANGE: Use an ID that does not exist
        int nonExistentId = -1;

        // ACT & ASSERT: Expect DatabaseException
        assertThrows(DatabaseException.class, () -> {
            orderMapper.getOrderById(nonExistentId, connectionPool);
        }, "Should throw DatabaseException when order does not exist");
    }

    /**
     * TEST 3: Get all orders from database.
     * Verifies: List is not null, orders can be retrieved
     */
    @Test
    void getAllOrders() throws DatabaseException {
        // ARRANGE: Insert two orders first
        Order order1 = createTestOrder();
        Order order2 = createTestOrder();

        int id1 = orderMapper.insertOrder(order1, connectionPool);
        int id2 = orderMapper.insertOrder(order2, connectionPool);

        // ACT: Get all orders
        List<Order> orders = orderMapper.getAllOrders(connectionPool);

        // ASSERT: List should contain at least our two orders
        assertNotNull(orders, "Order list should not be null");
        assertTrue(orders.size() >= 2, "Should have at least 2 orders");

        // Verify our orders are in the list (by checking IDs)
        boolean foundId1 = orders.stream().anyMatch(o -> o.getId() == id1);
        boolean foundId2 = orders.stream().anyMatch(o -> o.getId() == id2);
        assertTrue(foundId1, "Order 1 should be in the list");
        assertTrue(foundId2, "Order 2 should be in the list");
    }

    /**
     * TEST 4: Change order status.
     * Verifies: Status update works correctly
     */
    /**
     * TEST 4: Change order status from PENDING to APPROVED.
     * Verifies: Status update works correctly
     */
    /**
     * TEST 4: Change order status from PENDING to OFFER_SENT.
     * Verifies: Status update works correctly
     */
    @Test
    void changeOrderStatus() throws DatabaseException {
        // ARRANGE: Insert an order with PENDING status
        Order order = createTestOrder();
        int orderId = orderMapper.insertOrder(order, connectionPool);

        // Verify initial status is PENDING
        Order initialOrder = orderMapper.getOrderById(orderId, connectionPool);
        assertEquals(OrderStatus.PENDING, initialOrder.getOrderDetails().status(), "Initial status should be PENDING");

        // ACT: Change status to OFFER_SENT
        orderMapper.changeOrderStatus(connectionPool, OrderStatus.OFFER_SENT, initialOrder);

        // ASSERT: Verify status has changed to OFFER_SENT
        Order updatedOrder = orderMapper.getOrderById(orderId, connectionPool);
        assertEquals(OrderStatus.OFFER_SENT, updatedOrder.getOrderDetails().status(), "Status should be updated to OFFER_SENT");
    }
}