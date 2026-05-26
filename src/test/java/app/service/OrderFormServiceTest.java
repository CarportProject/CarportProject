package app.service;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class OrderFormServiceTest {

    private final OrderFormService service = new OrderFormService();

    @Test
    void getRangeNormal() {
        // Arrange
        int min = 0;
        int max = 20;
        int interval = 5;

        // Act
        List<Integer> result = service.getRange(min, max, interval);

        // Assert
        assertEquals(List.of(0, 5, 10, 15, 20), result);
    }

    @Test
    void getRangeMinEqualsMax() {
        // Arrange
        int min = 10;
        int max = 10;
        int interval = 3;

        // Act
        List<Integer> result = service.getRange(min, max, interval);

        // Assert
        assertEquals(List.of(10), result);
    }

    @Test
    void getRangeMinGreaterThanMax() {
        // Arrange
        int min = 20;
        int max = 10;
        int interval = 5;

        // Act
        List<Integer> result = service.getRange(min, max, interval);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getRangeNegativeValues() {
        // Arrange
        int min = -10;
        int max = 10;
        int interval = 5;

        // Act
        List<Integer> result = service.getRange(min, max, interval);

        // Assert
        assertEquals(List.of(-10, -5, 0, 5, 10), result);
    }

    @Test
    void getRangeIntervalOne() {
        // Arrange
        int min = 1;
        int max = 3;
        int interval = 1;

        // Act
        List<Integer> result = service.getRange(min, max, interval);

        // Assert
        assertEquals(List.of(1, 2, 3), result);
    }
}