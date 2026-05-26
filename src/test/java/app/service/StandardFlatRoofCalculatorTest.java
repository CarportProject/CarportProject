package app.service;

import app.entities.MaterialType;
import app.entities.RoofMaterial;
import app.entities.RoofType;
import app.entities.Specifications;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StandardFlatRoofCalculatorTest {

    @Test
    void getMaterialTypes_shouldReturnCorrectOrderedList() {
        // Arrange
        StandardFlatRoofCalculator calculator = new StandardFlatRoofCalculator();

        // Act
        List<MaterialType> actualTypes = calculator.getMaterialTypes();

        // Assert
        List<MaterialType> expectedTypes = List.of(
                MaterialType.POST,
                MaterialType.REM,
                MaterialType.RAFTER
        );
        assertEquals(expectedTypes, actualTypes,
                "The material types list must be exactly [POST, REM, RAFTER] in that order");
    }

    @Test
    void calculatePosts_shouldAlwaysFollowTheRules() {
        StandardFlatRoofCalculator calculator = new StandardFlatRoofCalculator();

        for (int lengthCm = 200; lengthCm <= 2000; lengthCm++) {

            Specifications specs = new Specifications.Builder()
                    .lengthCm(lengthCm)
                    .widthCm(300)
                    .roofType(RoofType.FLAT)
                    .roofMaterial(new RoofMaterial.Builder()
                            .id(1)
                            .name("Test Material")
                            .color("Black")
                            .price(1000)
                            .roofType(RoofType.FLAT)
                            .build())
                    .build();

            int totalPosts = calculator.calculatePosts(specs);

            // RULE 1: Number of posts must be even
            assertTrue(totalPosts % 2 == 0,
                    "Length " + lengthCm + " cm gave odd number of posts: " + totalPosts);

            int postsPerSide = totalPosts / 2;
            int roomBetween = lengthCm - 200;
            int gaps = postsPerSide - 1;

            // ONLY check spacing if there are at least 2 posts per side (gaps > 0)
            if (gaps > 0) {
                double spacing = (double) roomBetween / gaps;

                // RULE 2: Max allowed spacing is 300 cm
                assertTrue(spacing <= 300.0,
                        "Length " + lengthCm + " cm gave spacing " + spacing + " cm > 300 cm");

                // RULE 3: Spacing must be positive
                assertTrue(spacing > 0,
                        "Length " + lengthCm + " cm gave spacing <= 0");
            }
        }
    }
    @Test
    void calculateRem() { StandardFlatRoofCalculator calculator = new StandardFlatRoofCalculator();

        // Test ALL lengths from 1 cm to 2000 cm
        for (int lengthCm = 1; lengthCm <= 2000; lengthCm++) {

            Specifications specs = new Specifications.Builder()
                    .lengthCm(lengthCm)
                    .widthCm(300)
                    .roofType(RoofType.FLAT)
                    .roofMaterial(new RoofMaterial.Builder()
                            .id(1)
                            .name("Test Material")
                            .color("Black")
                            .price(1000)
                            .roofType(RoofType.FLAT)
                            .build())
                    .build();

            int totalBeams = calculator.calculateRem(specs);

            // RULE 1: Number of beams must be even (two sides: left and right)
            assertTrue(totalBeams % 2 == 0,
                    "Length " + lengthCm + " cm gave odd number of beams: " + totalBeams);

            // Calculate beams per side
            int beamsPerSide = totalBeams / 2;

            // RULE 2: Each beam can cover max 600 cm
            // So beams per side = ceil(lengthCm / 600)
            int expectedBeamsPerSide = (int) Math.ceil((double) lengthCm / 600.0);

            // RULE 3: Actual must match expected
            assertEquals(expectedBeamsPerSide, beamsPerSide,
                    "Length " + lengthCm + " cm: expected " + expectedBeamsPerSide +
                            " beams per side, but got " + beamsPerSide);
        }
    }

    @Test
    void calculateRafterCount() {
        // Arrange
        StandardFlatRoofCalculator calculator = new StandardFlatRoofCalculator();
        int rafterWidthMm = 45;

        // Act & Assert – test for ALL lengths
        for (int lengthMm = 100; lengthMm <= 10000; lengthMm++) {
            // Act
            int rafterCount = calculator.calculateRafterCount(lengthMm, rafterWidthMm);

            // Assert
            // Rule 1: At least 2 rafters
            assertTrue(rafterCount >= 2, "Length " + lengthMm + " mm -> rafters < 2");

            // Rule 2: Spacing between centers must be <= 600 mm
            int centerDistance = lengthMm - rafterWidthMm;
            int gaps = rafterCount - 1;
            double spacing = (double) centerDistance / gaps;

            assertTrue(spacing <= 600.0, "Length " + lengthMm + " mm spacing " + spacing + " > 600");
            assertTrue(spacing > 0, "Length " + lengthMm + " mm spacing <= 0");

            // Rule 3: Formula correctness
            int expected = (int) Math.max(2, Math.ceil((double)(lengthMm - rafterWidthMm) / 600.0) + 1);
            assertEquals(expected, rafterCount, "Length " + lengthMm + " mm mismatch");
        }
    }

    @Test
    void calculateRafter() {
        // Arrange
        StandardFlatRoofCalculator calculator = new StandardFlatRoofCalculator();
        int rafterWidthMm = 45;

        for (int lengthCm = 10; lengthCm <= 1000; lengthCm++) {
            // Arrange
            int lengthMm = lengthCm * 10;
            Specifications specs = new Specifications.Builder()
                    .lengthCm(lengthCm)
                    .widthCm(300)
                    .roofType(RoofType.FLAT)
                    .roofMaterial(new RoofMaterial.Builder().id(1).name("Test").color("Black").price(1000).roofType(RoofType.FLAT).build())
                    .build();

            // Act
            int rafterCount = calculator.calculateRafter(specs);
            int expected = calculator.calculateRafterCount(lengthMm, rafterWidthMm);

            // Assert
            assertEquals(expected, rafterCount, "Length " + lengthCm + " cm mismatch");
        }
    }
}