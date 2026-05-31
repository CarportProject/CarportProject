package app.service;

/*

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
    void calculatePosts_shouldReturnCorrectNumberOfPostsForGivenLengths() {
        // Arrange
        StandardFlatRoofCalculator calculator = new StandardFlatRoofCalculator();

        // Test case 1: length 300 cm
        Specifications specs300 = new Specifications.Builder()
                .lengthCm(300)
                .widthCm(300)
                .roofType(RoofType.FLAT)
                .roofMaterial(new RoofMaterial.Builder()
                        .id(1).name("Test").color("Black").price(1000).roofType(RoofType.FLAT).build())
                .build();
        // Act
        int posts300 = calculator.calculatePosts(specs300);
        // Assert
        assertEquals(4, posts300, "300 cm carport should have 4 posts");

        // Test case 2: length 600 cm
        Specifications specs600 = new Specifications.Builder()
                .lengthCm(600)
                .widthCm(300)
                .roofType(RoofType.FLAT)
                .roofMaterial(new RoofMaterial.Builder()
                        .id(1).name("Test").color("Black").price(1000).roofType(RoofType.FLAT).build())
                .build();
        int posts600 = calculator.calculatePosts(specs600);
        assertEquals(6, posts600, "600 cm carport should have 6 posts");

        // Test case 3: length 900 cm
        Specifications specs900 = new Specifications.Builder()
                .lengthCm(900)
                .widthCm(300)
                .roofType(RoofType.FLAT)
                .roofMaterial(new RoofMaterial.Builder()
                        .id(1).name("Test").color("Black").price(1000).roofType(RoofType.FLAT).build())
                .build();
        int posts900 = calculator.calculatePosts(specs900);
        assertEquals(8, posts900, "900 cm carport should have 8 posts");

        // Test case 4: length 1200 cm
        Specifications specs1200 = new Specifications.Builder()
                .lengthCm(1200)
                .widthCm(300)
                .roofType(RoofType.FLAT)
                .roofMaterial(new RoofMaterial.Builder()
                        .id(1).name("Test").color("Black").price(1000).roofType(RoofType.FLAT).build())
                .build();
        int posts1200 = calculator.calculatePosts(specs1200);
        assertEquals(10, posts1200, "1200 cm carport should have 10 posts");
    }
    @Test
    void calculateRem() {
        // Arrange
        StandardFlatRoofCalculator calculator = new StandardFlatRoofCalculator();

        // Helper to build Specifications
        Specifications specs300 = new Specifications.Builder()
                .lengthCm(300)
                .widthCm(300)
                .roofType(RoofType.FLAT)
                .roofMaterial(new RoofMaterial.Builder()
                        .id(1).name("Test").color("Black").price(1000).roofType(RoofType.FLAT).build())
                .build();
        Specifications specs600 = new Specifications.Builder()
                .lengthCm(600)
                .widthCm(300)
                .roofType(RoofType.FLAT)
                .roofMaterial(new RoofMaterial.Builder()
                        .id(1).name("Test").color("Black").price(1000).roofType(RoofType.FLAT).build())
                .build();
        Specifications specs700 = new Specifications.Builder()
                .lengthCm(700)
                .widthCm(300)
                .roofType(RoofType.FLAT)
                .roofMaterial(new RoofMaterial.Builder()
                        .id(1).name("Test").color("Black").price(1000).roofType(RoofType.FLAT).build())
                .build();
        Specifications specs1200 = new Specifications.Builder()
                .lengthCm(1200)
                .widthCm(300)
                .roofType(RoofType.FLAT)
                .roofMaterial(new RoofMaterial.Builder()
                        .id(1).name("Test").color("Black").price(1000).roofType(RoofType.FLAT).build())
                .build();

        // Act
        int beams300 = calculator.calculateRem(specs300);
        int beams600 = calculator.calculateRem(specs600);
        int beams700 = calculator.calculateRem(specs700);
        int beams1200 = calculator.calculateRem(specs1200);

        // Assert
        assertEquals(2, beams300, "300 cm → 2 beams (1 per side)");
        assertEquals(2, beams600, "600 cm → 2 beams (1 per side)");
        assertEquals(4, beams700, "700 cm → 4 beams (2 per side)");
        assertEquals(4, beams1200, "1200 cm → 4 beams (2 per side)");
    }

    @Test
    void calculateRafterCount() {
        // Arrange
        StandardFlatRoofCalculator calculator = new StandardFlatRoofCalculator();
        int rafterWidthMm = 45;
        int lengthShort = 100;      // 100 mm
        int lengthMedium = 6000;    // 6000 mm
        int lengthLong = 12000;     // 12000 mm

        // Act
        int countShort = calculator.calculateRafterCount(lengthShort, rafterWidthMm);
        int countMedium = calculator.calculateRafterCount(lengthMedium, rafterWidthMm);
        int countLong = calculator.calculateRafterCount(lengthLong, rafterWidthMm);

        // Assert
        assertEquals(2, countShort, "100 mm → 2 rafters");
        assertEquals(11, countMedium, "6000 mm → 11 rafters");
        assertEquals(21, countLong, "12000 mm → 21 rafters");
    }

    @Test
    void calculateRafter() {
        // Arrange
        StandardFlatRoofCalculator calculator = new StandardFlatRoofCalculator();

        Specifications specsShort = new Specifications.Builder()
                .lengthCm(10)
                .widthCm(300)
                .roofType(RoofType.FLAT)
                .roofMaterial(new RoofMaterial.Builder()
                        .id(1).name("Test").color("Black").price(1000).roofType(RoofType.FLAT).build())
                .build();
        Specifications specsMedium = new Specifications.Builder()
                .lengthCm(600)
                .widthCm(300)
                .roofType(RoofType.FLAT)
                .roofMaterial(new RoofMaterial.Builder()
                        .id(1).name("Test").color("Black").price(1000).roofType(RoofType.FLAT).build())
                .build();
        Specifications specsLong = new Specifications.Builder()
                .lengthCm(1200)
                .widthCm(300)
                .roofType(RoofType.FLAT)
                .roofMaterial(new RoofMaterial.Builder()
                        .id(1).name("Test").color("Black").price(1000).roofType(RoofType.FLAT).build())
                .build();

        // Act
        int countShort = calculator.calculateRafter(specsShort);
        int countMedium = calculator.calculateRafter(specsMedium);
        int countLong = calculator.calculateRafter(specsLong);

        // Assert
        assertEquals(2, countShort, "10 cm → 2 rafters");
        assertEquals(11, countMedium, "600 cm → 11 rafters");
        assertEquals(21, countLong, "1200 cm → 21 rafters");
    }
}
*/