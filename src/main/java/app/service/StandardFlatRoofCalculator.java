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
 * Calculates quantities for posts, side beams, rafters, wide boards and narrow boards.
 * {@link MaterialType#REGULAR} is not supported and will throw
 * {@link UnsupportedOperationException} if called.
 * </p>
 */
public class StandardFlatRoofCalculator extends MaterialService {

    final int widthOverlapCm = 10;
    final int lengthOverlapCm = 20;

    final Map<MaterialType, Material> materials;

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
    protected int[] calculatePosts(Specifications specifications) {
        int roomBetween = specifications.getLengthCm() - (2 * 100);
        int sections = (int) Math.ceil((double) roomBetween / 300);
        return new int[] {(sections + 1) * 2, materials.get(MaterialType.POST).getMaxLength() / 10};
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
        while (minLength > length || length > maxLength);

        count--;

        count = count * 2;

        return new int[]{count, length};
    }

    /**
     * Calculates the number of rafters (spær) required.
     * <p>
     * The outer rafters are placed at each end; inner rafters are spaced at most 600 mm apart.
     * </p>
     *
     * @param carportLengthMm the total length of the carport in millimetres
     * @param rafterWidthMm   the width of a single rafter in millimetres
     * @return the total number of rafters
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

        int carportLengthCm = specifications.getLengthCm();

        int intervalCount = (int) Math.ceil((double) carportLengthCm / 60);
        int actualInterval = carportLengthCm / intervalCount;

        // If interval is under 50, add more rafter
        if (actualInterval < 50) {
            intervalCount++;
        }

        int count = intervalCount + 1;
        int length = specifications.getWidthCm();


        return new int[]{count, length};
    }


    /**
     * Calculates the number of wide boards required for the front of the carport.
     * <p>
     * The board length is rounded up to the nearest 30 cm and must stay within
     * the material's min/max length bounds. Only the front side (one face) is covered.
     * </p>
     *
     * @param specifications the carport dimensions and roof configuration
     * @return an array where index 0 is the board count and index 1 is the board length in cm
     */
    @Override
    protected int[] calculateWideBoardFront(Specifications specifications) {
        int minLength = materials.get(MaterialType.WIDE_BOARD_FRONT).getMinLength() / 10;
        int maxLength = materials.get(MaterialType.WIDE_BOARD_FRONT).getMaxLength() / 10;

        int count = 1;
        int length;
        do {
            double d = Math.ceil((double) specifications.getWidthCm() / count / 30);
            length = (int) d * 30;
            count++;
        } while (length > maxLength);

        count--;
        length = Math.max(length, minLength);

        return new int[]{count, length};
    }

    /**
     * Calculates the number of wide boards required for the sides of the carport.
     * <p>
     * The board length is rounded up to the nearest 30 cm and must stay within
     * the material's min/max length bounds. The count is doubled to cover both sides.
     * </p>
     *
     * @param specifications the carport dimensions and roof configuration
     * @return an array where index 0 is the board count and index 1 is the board length in cm
     */
    @Override
    protected int[] calculateWideBoardSide(Specifications specifications) {
        int minLength = materials.get(MaterialType.WIDE_BOARD_SIDE).getMinLength() / 10;
        int maxLength = materials.get(MaterialType.WIDE_BOARD_SIDE).getMaxLength() / 10;

        int count = 1;
        int length;
        do {
            double d = Math.ceil((double) specifications.getLengthCm() / count / 30);
            length = (int) d * 30;
            count++;
        } while (length > maxLength);

        count--;
        length = Math.max(length, minLength);
        count = count * 2;

        return new int[]{count, length};
    }

    /**
     * Calculates the number of narrow boards required for the front of the carport.
     * <p>
     * The board length is rounded up to the nearest 30 cm and must stay within
     * the material's min/max length bounds. Only the front side (one face) is covered.
     * </p>
     *
     * @param specifications the carport dimensions and roof configuration
     * @return an array where index 0 is the board count and index 1 is the board length in cm
     */
    @Override
    protected int[] calculateNarrowBoardFront(Specifications specifications) {
        int minLength = materials.get(MaterialType.NARROW_BOARD_FRONT).getMinLength() / 10;
        int maxLength = materials.get(MaterialType.NARROW_BOARD_FRONT).getMaxLength() / 10;

        int count = 1;
        int length;
        do {
            double d = Math.ceil((double) specifications.getWidthCm() / count / 30);
            length = (int) d * 30;
            count++;
        } while (length > maxLength);

        count--;
        length = Math.max(length, minLength);

        return new int[]{count, length};
    }

    /**
     * Calculates the number of narrow boards required for the sides of the carport.
     * <p>
     * The board length is rounded up to the nearest 30 cm and must stay within
     * the material's min/max length bounds. The count is doubled to cover both sides.
     * </p>
     *
     * @param specifications the carport dimensions and roof configuration
     * @return an array where index 0 is the board count and index 1 is the board length in cm
     */
    @Override
    protected int[] calculateNarrowBoardSide(Specifications specifications) {
        int minLength = materials.get(MaterialType.NARROW_BOARD_SIDE).getMinLength() / 10;
        int maxLength = materials.get(MaterialType.NARROW_BOARD_SIDE).getMaxLength() / 10;

        int count = 1;
        int length;
        do {
            double d = Math.ceil((double) specifications.getLengthCm() / count / 30);
            length = (int) d * 30;
            count++;
        } while (length > maxLength);

        count--;
        length = Math.max(length, minLength);
        count = count * 2;

        return new int[]{count, length};
    }

    /**
     * Not supported for this carport type.
     *
     * @param specifications the carport dimensions and roof configuration
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