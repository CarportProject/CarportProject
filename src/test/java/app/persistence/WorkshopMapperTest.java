package app.persistence;

import app.entities.Workshop;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkshopMapperTest extends DatabaseTest {

    private final WorkshopMapper workshopMapper = new WorkshopMapper();

    @Test
    void insertWorkshop() throws DatabaseException {
        // Arrange - Create a workshop to save
        Workshop workshop = new Workshop.Builder()
                .widthCm(200)
                .lengthCm(300)
                .build();

        // Act - Save the workshop to the database
        int id = workshopMapper.insertWorkshop(workshop, connectionPool);

        // Assert - Verify the insertion was successful
        assertTrue(id > 0, "insertWorkshop should return a valid ID");

        // Act - Retrieve the saved workshop
        Workshop saved = workshopMapper.findWorkshopById(id, connectionPool);

        // Assert - Verify the data was saved correctly
        assertEquals(workshop.getWidthCm(), saved.getWidthCm(), "Width should be 200");
        assertEquals(workshop.getLengthCm(), saved.getLengthCm(), "Length should be 300");
    }

    @Test
    void findWorkshopById() throws DatabaseException {
        // Part 1: Test finding a workshop that EXISTS

        // Arrange - Create and save a workshop
        Workshop workshop = new Workshop.Builder()
                .widthCm(150)
                .lengthCm(250)
                .build();
        int id = workshopMapper.insertWorkshop(workshop, connectionPool);

        // Act - Find the workshop by its ID
        Workshop found = workshopMapper.findWorkshopById(id, connectionPool);

        // Assert - Verify all data matches
        assertEquals(id, found.getId(), "ID should match");
        assertEquals(workshop.getWidthCm(), found.getWidthCm(), "Width should be 150");
        assertEquals(workshop.getLengthCm(), found.getLengthCm(), "Length should be 250");

        // Part 2: Test finding a workshop that does NOT EXIST

        // Act - Try to find a workshop with an invalid ID
        Workshop notFound = workshopMapper.findWorkshopById(-1, connectionPool);

        // Assert - Verify that null is returned
        assertNull(notFound, "Finding a non-existent workshop should return null");
    }
}