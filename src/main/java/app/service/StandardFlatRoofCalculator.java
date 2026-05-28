package app.service;

import app.entities.Material;
import app.entities.MaterialType;
import app.entities.Specifications;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    final int narrowBoardMaxLengthCm = 480;
    final int wideBoardMaxLengthCm = 480;
    final int widthOverlapCm = 10;
    final int lengthOverlapCm = 20;

    final Map<MaterialType, Material> materials;

    List<MaterialType> materialTypesUsed = new ArrayList<>();

    public StandardFlatRoofCalculator(ConnectionPool connectionPool) throws DatabaseException {
        this.materials = getMaterialTypeByMaterial(connectionPool);
    }

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
                MaterialType.RAFTER,
                MaterialType.NARROW_BOARD_SIDE,
                MaterialType.NARROW_BOARD_FRONT,
                MaterialType.WIDE_BOARD_SIDE,
                MaterialType.WIDE_BOARD_FRONT
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
    protected int[] calculateRem(Specifications specifications) {

        int minLength = materials.get(MaterialType.REM).getMinLength() / 10;
        int maxLength = materials.get(MaterialType.REM).getMaxLength() / 10;

        int count = 1;
        int length;
        do {
            double d = Math.ceil((double) specifications.getLengthCm() / count / 30);

            length = (int) d * 30;
            count++;
        }
        while (minLength < length || length > maxLength);

        count--;

        return new int[]{count, length};
    }

    /**
     * Calculates the number of rafters (spær) required.
     * <p>
     * Rafters are placed every 60 cm along the length of the carport.
     * </p>
     *
     * @param carportLengthMm the carport dimensions and roof configuration
     * @return the number of rafters
     */
    public int calculateRafterCount(int carportLengthMm, int rafterWidthMm) {
        int spanBetweenOuterRafters = carportLengthMm - rafterWidthMm;
        int intervalCount = (int) Math.ceil((double) spanBetweenOuterRafters / 600);
        if (intervalCount < 1) {
            intervalCount = 1;
        }
        return intervalCount + 1;
    }

    @Override
    protected int[] calculateRafter(Specifications specifications) {
        int carportLengthMm = specifications.getLengthCm() * 10;
        int rafterWidthMm = 45;

        return calculateRafterCount(carportLengthMm, rafterWidthMm);
    }

    /**
     * Not applicable to a flat-roof carport.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    protected int[] calculateWideBoardFront(Specifications specifications) {
        int carportWidth = specifications.getWidthCm();
        return (int) (Math.ceil((double) carportWidth / wideBoardMaxLengthCm));
    }

    /**
     * Not applicable to a flat-roof carport.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    protected int[] calculateWideBoardSide(Specifications specifications) {
        int carportLength = specifications.getLengthCm();
        return (int) (Math.ceil((double) carportLength / wideBoardMaxLengthCm) * 2);
    }

    /**
     * Not applicable to a flat-roof carport.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    protected int[] calculateNarrowBoardFront(Specifications specifications) {
        int carportWidth = specifications.getWidthCm();
        return (int) (Math.ceil((double) carportWidth / narrowBoardMaxLengthCm) * 2);
    }

    /**
     * Not applicable to a flat-roof carport.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    protected int[] calculateNarrowBoardSide(Specifications specifications) {
        int carportLength = specifications.getLengthCm();
        return (int) (Math.ceil((double) carportLength / narrowBoardMaxLengthCm));
    }

    /**
     * Not applicable to a flat-roof carport.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    protected int[] calculateRegular(Specifications specifications) {
        throw new UnsupportedOperationException("This material is not supported yet");
    }

    @Override
    protected int calculateRoof(Specifications specifications) {
        int carportWidth = specifications.getWidthCm();
        int carportLength = specifications.getLengthCm();

        double roofWidth = specifications.getRoofMaterial().getWidthCm();
        double roofLength = specifications.getRoofMaterial().getLengthCm();

        int amountInWidth = (int) Math.ceil(carportWidth / (roofWidth - widthOverlapCm));
        int amountInLength = (int) Math.ceil(carportLength / (roofLength - lengthOverlapCm));

        return amountInLength * amountInWidth;
    }
}