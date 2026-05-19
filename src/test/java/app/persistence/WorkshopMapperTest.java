package app.persistence;

import app.entities.Workshop;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkshopMapperTest extends DatabaseTest {

    private final WorkshopMapper workshopMapper = new WorkshopMapper();

    @Test
    void insertWorkshop() throws DatabaseException {
        // WHAT: Test that I can save a workshop

        // Create a workshop
        Workshop workshop = new Workshop.Builder()
                .widthCm(200)
                .lengthCm(300)
                .build();

        // Save it
        int id = workshopMapper.insertWorkshop(workshop, connectionPool);

        // CHECK 1: Did I get a valid ID? (should be > 0)
        assertTrue(id > 0, "insertWorkshop should return a valid ID");

        // CHECK 2: Is the data saved correctly?
        Workshop saved = workshopMapper.findWorkshopById(id, connectionPool);
        assertEquals(workshop.getWidthCm(), saved.getWidthCm(), "Width should be 200");
        assertEquals(workshop.getLengthCm(), saved.getLengthCm(), "Length should be 300");
    }

    @Test
    void findWorkshopById() throws DatabaseException {
        // PART 1: Test finding a workshop that EXISTS

        // Create and save a workshop
        Workshop workshop = new Workshop.Builder()
                .widthCm(150)
                .lengthCm(250)
                .build();
        int id = workshopMapper.insertWorkshop(workshop, connectionPool);

        // Find it
        Workshop found = workshopMapper.findWorkshopById(id, connectionPool);

        // CHECK: All data matches
        assertEquals(id, found.getId(), "ID should match");
        assertEquals(workshop.getWidthCm(), found.getWidthCm(), "Width should be 150");
        assertEquals(workshop.getLengthCm(), found.getLengthCm(), "Length should be 250");

        // PART 2: Test finding a workshop that does NOT EXIST

        // Try to find a workshop with ID that doesn't exist
        assertThrows(DatabaseException.class, () -> {
            workshopMapper.findWorkshopById(99999, connectionPool);
        }, "Should throw DatabaseException when workshop not found");
    }
}