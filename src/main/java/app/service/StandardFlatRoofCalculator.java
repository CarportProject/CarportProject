package app.service;

import app.entities.Material;
import app.entities.Specifications;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;


import java.util.List;

public class StandardFlatRoofCalculator extends MaterialService {



    @Override
    protected int calculatePosts(Specifications specifications) {

        // The free space between the two end posts (excluding 100cm at each end)
        int roomBetween = specifications.getLengthCm() - (2 * 100);

        // Number of 300cm sections the free space is divided into
        int sections = (int) Math.ceil((double) roomBetween / 300);

        // Posts needed (one post per section boundary, times two sides)

        return (sections + 1) * 2;
    }


    @Override
    protected int calculateRem(Specifications specifications) {

        return (int) Math.ceil((double) specifications.getLengthCm() / 600) * 2;
    }

    /**
     * Calculates the number of rafters (spær) required.
     * Rafters run across the width of the carport and rest on the side beams.
     *
     * @param specifications the carport dimensions and roof configuration
     * @return the number of rafters
     */
    @Override
    protected int calculateRafter(Specifications specifications) {
        return (int) Math.round((double) specifications.getLengthCm() / 60);
    }

    /**
     * Calculates the number of wide fascia boards (understernbrædder) for the front and back ends.
     *
     * @param specifications the carport dimensions and roof configuration
     * @return the number of wide fascia boards for the front and back ends
     */
    @Override
    protected int calculateWideBoardFront(Specifications specifications) {
        return 0;
    }

    /**
     * Calculates the number of wide fascia boards (understernbrædder) for the sides.
     *
     * @param specifications the carport dimensions and roof configuration
     * @return the number of wide fascia boards for the sides
     */
    @Override
    protected int calculateWideBoardSide(Specifications specifications) {
        return 0;
    }

    /**
     * Calculates the number of narrow fascia boards (oversternbrædder) for the front end.
     *
     * @param specifications the carport dimensions and roof configuration
     * @return the number of narrow fascia boards for the front end
     */
    @Override
    protected int calculateNarrowBoardFront(Specifications specifications) {
        return 0;
    }

    /**
     * Calculates the number of narrow fascia boards (oversternbrædder) for the sides.
     *
     * @param specifications the carport dimensions and roof configuration
     * @return the number of narrow fascia boards for the sides
     */
    @Override
    protected int calculateNarrowBoardSide(Specifications specifications) {
        return 0;
    }

    /**
     * Calculates the number of regular boards (løsholter) required for shed gable ends.
     * Only relevant when a workshop is attached to the carport.
     *
     * @param specifications the carport dimensions and roof configuration
     * @return the number of regular boards
     */
    @Override
    protected int calculateRegular(Specifications specifications) {
        return 0;
    }

}
