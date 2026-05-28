package app.service;

import app.entities.Material;
import app.entities.MaterialType;
import app.entities.Specifications;
import app.entities.SvgPost;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialsMapper;

import java.util.ArrayList;
import java.util.List;

public class SvgCalculationService {

    private final int arrowSpaceLeft = 100;
    private final int innerWidth = 900;
    private final int rightArrowSpace = 100;
    private final int outerWidth = arrowSpaceLeft + innerWidth + rightArrowSpace;

    private final int topPadding = 40;
    private final int betweenDrawings = 100;

    private final int horizontalArrowOffset = 25;
    private final int verticalArrowX = 50;

    private static final int cmToMm = 10;

    private static final int frontOverhangMm = 1000;
    private static final int backOverhangMm = 1000;
    private static final int maxDistanceBetweenPostsMm = 3000;

    private static final int frontPostHeightMm = 2080;
    private static final int backPostHeightMm = 2020;
    private static final int postDistanceFromSideMm = 450;

    private ConnectionPool connectionPool = null;

    private final StandardFlatRoofCalculator standardFlatRoofCalculator = new StandardFlatRoofCalculator(connectionPool);

    public SvgCalculationService(ConnectionPool connectionPool) throws DatabaseException {
        this.connectionPool = connectionPool;
    }

    // ===== MATERIAL LOOKUP =====

    public Material findMaterialByMaterialType(MaterialType materialType) throws DatabaseException {
        List<Material> materials;
        try {
            materials = MaterialsMapper.getAllMaterials(ConnectionPool.instance);
        } catch (DatabaseException e) {
            System.err.println("[SvgCalculationService.findMaterialByMaterialType] " + e.getMessage());
            throw e;
        }
        for (Material material : materials) {
            if (material.getId() == materialType.getId()) {
                return material;
            }
        }
        throw new IllegalArgumentException("Material not found: " + materialType.name());
    }

    public int getMaterialWidthMm(MaterialType materialType) throws DatabaseException {
        return findMaterialByMaterialType(materialType).getWidth();
    }

    public int getMaterialHeightMm(MaterialType materialType) throws DatabaseException {
        return findMaterialByMaterialType(materialType).getHeight();
    }

    public int getPostWidthMm() throws DatabaseException {
        return getMaterialWidthMm(MaterialType.POST);
    }

    public int getBeamTopViewWidthMm() throws DatabaseException {
        return getMaterialHeightMm(MaterialType.REM);
    }

    public int getUnderSternSideViewHeightMm() throws DatabaseException {
        return findMaterialByMaterialType(MaterialType.WIDE_BOARD_FRONT).getWidth();
    }

    public int getSideViewExtraHeightMm() throws DatabaseException {
        int wideBoardMm = getMaterialWidthMm(MaterialType.WIDE_BOARD_FRONT);
        int narrowBoardMm = getMaterialWidthMm(MaterialType.NARROW_BOARD_FRONT);
        return wideBoardMm + narrowBoardMm - narrowBoardMm / 2;
    }

    public int getOverSternSideViewHeightMm() throws DatabaseException {
        return getMaterialWidthMm(MaterialType.NARROW_BOARD_FRONT);
    }

    public int getOverSternOverlapMm() throws DatabaseException {
        return getOverSternSideViewHeightMm() / 2;
    }

    // ===== LAYOUT CONSTANTS =====

    public int getOuterWidth() { return outerWidth; }
    public int getArrowSpaceLeft() { return arrowSpaceLeft; }
    public int getInnerWidth() { return innerWidth; }
    public int getTopPadding() { return topPadding; }
    public int getBetweenDrawings() { return betweenDrawings; }
    public int getHorizontalArrowOffset() { return horizontalArrowOffset; }
    public int getVerticalArrowX() { return verticalArrowX; }

    // ===== CARPORT DIMENSIONS =====

    public int getCarportLengthMm(Specifications specifications) {
        return specifications.getLengthCm() * cmToMm;
    }

    public int getCarportWidthMm(Specifications specifications) {
        return specifications.getWidthCm() * cmToMm;
    }

    // ===== POST & BEAM GEOMETRY =====

    public int getFrontPostHeightMm() { return frontPostHeightMm; }
    public int getBackPostHeightMm() { return backPostHeightMm; }

    public int getSideViewRoofFallMm() {
        return frontPostHeightMm - backPostHeightMm;
    }

    public int getSideViewRealHeightMm() { return frontPostHeightMm; }
    public int getSideViewGroundY() { return frontPostHeightMm; }

    public int getSideViewRoofTopYAtX(Specifications specifications, int xMm) {
        return (int) Math.round((double) getSideViewRoofFallMm() * xMm / getCarportLengthMm(specifications));
    }

    public int getTopPostY() { return postDistanceFromSideMm; }

    public int getBottomPostY(Specifications specifications) throws DatabaseException {
        return getCarportWidthMm(specifications) - postDistanceFromSideMm - getMaterialWidthMm(MaterialType.POST);
    }

    public int getBottomPostOuterEdgeY(Specifications specifications) throws DatabaseException {
        return getBottomPostY(specifications) + getMaterialWidthMm(MaterialType.POST);
    }

    public int getDistanceBetweenOuterPostEdgesMm(Specifications specifications) throws DatabaseException {
        return getBottomPostOuterEdgeY(specifications) - getTopPostY();
    }

    public int getTopBeamY() { return getTopPostY(); }

    public int getBottomBeamY(Specifications specifications) throws DatabaseException {
        int bottomPostY = getBottomPostY(specifications);
        int postWidthMm = getMaterialWidthMm(MaterialType.POST);
        int beamThicknessMm = getMaterialHeightMm(MaterialType.REM);
        return bottomPostY + postWidthMm - beamThicknessMm;
    }

    // ===== SCALING =====

    public double getScaledHeight(int realHeightMm, Specifications specifications) {
        return (double) realHeightMm * innerWidth / getCarportLengthMm(specifications);
    }

    public double getTopViewHeight(Specifications specifications) {
        return getScaledHeight(getCarportWidthMm(specifications), specifications);
    }

    public double getSideViewY() { return topPadding; }

    // ===== POST POSITIONS =====

    public List<SvgPost> calculatePostsForFlatRoofWithoutWorkshop(Specifications specifications) throws DatabaseException {
        List<Integer> positionsFromFront = calculatePostPositionsFromFront(specifications);
        int topSideY = getTopPostY();
        int bottomSideY = getBottomPostY(specifications);
        List<SvgPost> posts = new ArrayList<>();
        for (int xPosition : positionsFromFront) {
            posts.add(new SvgPost(xPosition, topSideY));
            posts.add(new SvgPost(xPosition, bottomSideY));
        }
        return posts;
    }

    public List<SvgPost> calculateSideViewPostsForFlatRoofWithoutWorkshop(Specifications specifications) throws DatabaseException {
        List<Integer> positionsFromFront = calculatePostPositionsFromFront(specifications);
        List<SvgPost> posts = new ArrayList<>();
        for (int xPosition : positionsFromFront) {
            posts.add(new SvgPost(xPosition, 0));
        }
        return posts;
    }

    public List<Integer> calculatePostPositionsFromFront(Specifications specifications) throws DatabaseException {
        int lengthMm = getCarportLengthMm(specifications);
        int postWidthMm = getMaterialWidthMm(MaterialType.POST);

        int firstPostOuterSide = frontOverhangMm;
        int lastPostOuterSide = lengthMm - backOverhangMm - postWidthMm;
        int innerDistance = lastPostOuterSide - firstPostOuterSide - postWidthMm;

        List<Integer> postPositions = new ArrayList<>();
        postPositions.add(firstPostOuterSide);

        if (innerDistance > maxDistanceBetweenPostsMm) {
            int middlePost = firstPostOuterSide + ((lastPostOuterSide - firstPostOuterSide) / 2);
            postPositions.add(middlePost);
        }

        postPositions.add(lastPostOuterSide);
        return postPositions;
    }

    public List<Integer> calculateSideViewPostMeasurementPositions(Specifications specifications) throws DatabaseException {
        int lengthMm = getCarportLengthMm(specifications);
        List<Integer> positions = new ArrayList<>();
        positions.add(0);
        positions.addAll(calculatePostPositionsFromFront(specifications));
        positions.add(lengthMm);
        return positions;
    }

    public List<Integer> calculateSideViewPostMeasurementDistances(Specifications specifications) throws DatabaseException {
        List<Integer> positions = calculateSideViewPostMeasurementPositions(specifications);
        List<Integer> distances = new ArrayList<>();
        for (int i = 0; i < positions.size() - 1; i++) {
            distances.add(positions.get(i + 1) - positions.get(i));
        }
        return distances;
    }

    // ===== RAFTER POSITIONS =====

    public int calculateRafterIntervalCount(Specifications specifications) throws DatabaseException {
        int carportLengthMm = getCarportLengthMm(specifications);
        int rafterThicknessMm = getMaterialHeightMm(MaterialType.RAFTER);
        return standardFlatRoofCalculator.calculateRafterCount(carportLengthMm, rafterThicknessMm) - 1;
    }

    public List<Integer> calculateRafterPositionsFromLeftSide(Specifications specifications) throws DatabaseException {
        int carportLengthMm = getCarportLengthMm(specifications);
        int rafterThicknessMm = getMaterialHeightMm(MaterialType.RAFTER);
        int spanBetweenOuterRafters = carportLengthMm - rafterThicknessMm;
        int intervalCount = calculateRafterIntervalCount(specifications);
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i <= intervalCount; i++) {
            int xPosition = (int) Math.round((double) i * spanBetweenOuterRafters / intervalCount);
            positions.add(xPosition);
        }
        return positions;
    }

    public List<Integer> calculateRafterDistancesMm(Specifications specifications) throws DatabaseException {
        List<Integer> positions = calculateRafterPositionsFromLeftSide(specifications);
        List<Integer> distances = new ArrayList<>();
        for (int i = 0; i < positions.size() - 1; i++) {
            distances.add(positions.get(i + 1) - positions.get(i));
        }
        return distances;
    }
}
