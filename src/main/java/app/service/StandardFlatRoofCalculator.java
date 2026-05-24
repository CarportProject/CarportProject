package app.service;

import app.entities.MaterialType;
import app.entities.Specifications;

import java.util.List;

/**
 * Concrete material calculator for a standard flat-roof carport.
 * <p>
 * Calculates quantities for posts, side beams and rafters only.
 * Material types that are not applicable to this roof variant
 * ({@link MaterialType#WIDE_BOARD_FRONT}, {@link MaterialType#WIDE_BOARD_SIDE},
 * {@link MaterialType#NARROW_BOARD_FRONT}, {@link MaterialType#NARROW_BOARD_SIDE},
 * {@link MaterialType#REGULAR}) throw {@link UnsupportedOperationException} if called.
 * </p>
 */
public class StandardFlatRoofCalculator extends MaterialService {

    /**
     * Returns the material types used by a standard flat-roof carport.
     *
     * @return an ordered list of the material types to calculate and insert
     */
    @Override
    protected List<MaterialType> getMaterialTypes() {
        return List.of(
                MaterialType.POST,
                MaterialType.REM,
                MaterialType.RAFTER
        );
    }

    /**
     * Calculates the number of vertical support posts required.
     * <p>
     * Posts are placed at each end (1 m from the edge) and every 300 cm in between,
     * along both sides of the carport.
     * </p>
     *
     * @param specifications the carport dimensions and roof configuration
     * @return the total number of posts
     */
    @Override
    protected int calculatePosts(Specifications specifications) {
        int roomBetween = specifications.getLengthCm() - (2 * 100);
        int sections = (int) Math.ceil((double) roomBetween / 300);
        return (sections + 1) * 2;
    }

    /**
     * Calculates the number of side beams (remme) required.
     * <p>
     * Each beam spans up to 600 cm, so the number of beams per side is the
     * length divided by 600 cm rounded up. The result is doubled for both sides.
     * </p>
     *
     * @param specifications the carport dimensions and roof configuration
     * @return the total number of side beams
     */
    @Override
    protected int calculateRem(Specifications specifications) {
        return (int) Math.ceil((double) specifications.getLengthCm() / 600) * 2;
    }

    /**
     * Calculates the number of rafters (spær) required.
     * <p>
     * Rafters are placed every 60 cm along the length of the carport.
     * </p>
     *
     * @param specifications the carport dimensions and roof configuration
     * @return the number of rafters
     */
    @Override
    protected int calculateRafter(Specifications specifications) {
        return (int) Math.ceil((double) specifications.getLengthCm() / 60) +1;
    }

    /**
     * Not applicable to a flat-roof carport.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    protected int calculateWideBoardFront(Specifications specifications) {
        throw new UnsupportedOperationException("Wide front boards are not used in a flat-roof carport");
    }

    /**
     * Not applicable to a flat-roof carport.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    protected int calculateWideBoardSide(Specifications specifications) {
        throw new UnsupportedOperationException("Wide side boards are not used in a flat-roof carport");
    }

    /**
     * Not applicable to a flat-roof carport.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    protected int calculateNarrowBoardFront(Specifications specifications) {
        throw new UnsupportedOperationException("Narrow front boards are not used in a flat-roof carport");
    }

    /**
     * Not applicable to a flat-roof carport.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    protected int calculateNarrowBoardSide(Specifications specifications) {
        throw new UnsupportedOperationException("Narrow side boards are not used in a flat-roof carport");
    }

    /**
     * Not applicable to a flat-roof carport.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    protected int calculateRegular(Specifications specifications) {
        throw new UnsupportedOperationException("Regular boards are not used in a flat-roof carport");
    }
}
