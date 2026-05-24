package app.service;

import app.entities.Material;
import app.entities.Specifications;
import app.entities.SvgPost;

import java.util.ArrayList;
import java.util.List;

public class SvgCalculationService {

    private final int arrowSpaceLeft = 100;
    private final int innerWidth = 900;
    private final int rightArrowSpace = 100;
    private final int outerWidth = arrowSpaceLeft + innerWidth + rightArrowSpace; // 1100

    private final int topPadding = 40;
    private final int betweenDrawings = 100;
    private final int bottomPadding = 60;

    private final int horizontalArrowOffset = 25;
    private final int verticalArrowX = 50;

    private static final int cmToMm = 10;

    private static final int frontOverhangMm = 1000;
    private static final int backOverhangMm = 1000;

    private static final int maxDistanceBetweenPostsMm = 3000;

    private static final int frontPostHeightMm = 2080;
    private static final int backPostHeightMm = 2020;

    private static final int postDistanceFromSideMm = 450;

    public Material findMaterialByName(List<Material> materials, String name) {

        for (Material material : materials) {
            if (material.getName().equalsIgnoreCase(name)) {
                return material;
            }
        }

        throw new IllegalArgumentException("Material not found: " + name);
    }

    public int getPostWidthMm(List<Material> materials) {

        Material post = findMaterialByName(materials, "Stolpe");

        return post.getWidth();
    }

    public int getPostHeightMm(List<Material> materials) {

        Material post = findMaterialByName(materials, "Stolpe");

        return post.getHeight();
    }

    public int getBeamTopViewWidthMm(List<Material> materials) {

        Material beam = findMaterialByName(materials, "Spærtræ ubh.");

        return beam.getHeight();
    }

    public int getBeamSideViewHeightMm(List<Material> materials) {

        Material beam = findMaterialByName(materials, "Spærtræ ubh.");

        return beam.getWidth();
    }

    public int getRafterWidthMm(List<Material> materials) {

        Material rafter = findMaterialByName(materials, "Spærtræ ubh.");

        return rafter.getHeight();
    }

    public Material getUnderSternMaterial(List<Material> materials) {

        for (Material material : materials) {
            if (material.getWidth() == 200 && material.getHeight() == 25) { return material; }
        }

        throw new IllegalArgumentException("Understern material not found");
    }

    public int getUnderSternSideViewHeightMm(List<Material> materials) {

        return getUnderSternMaterial(materials).getWidth();
    }

    public Material getOverSternMaterial(List<Material> materials) {

        for (Material material : materials) {
            if (material.getWidth() == 125 && material.getHeight() == 25) { return material; }
        }

        throw new IllegalArgumentException("Overstern material not found");
    }

    public int getOverSternSideViewHeightMm(List<Material> materials) {

        return getOverSternMaterial(materials).getWidth();
    }

    public int getOverSternOverlapMm(List<Material> materials) {

        return getOverSternSideViewHeightMm(materials) / 2;
    }

    public int getSideViewExtraHeightMm(List<Material> materials) {

        return getUnderSternSideViewHeightMm(materials) + getOverSternSideViewHeightMm(materials)
            - getOverSternOverlapMm(materials);
    }

    public int getOuterWidth() {
        return outerWidth;
    }

    public int getArrowSpaceLeft() {
        return arrowSpaceLeft;
    }

    public int getInnerWidth() {
        return innerWidth;
    }

    public int getTopPadding() {
        return topPadding;
    }

    public int getBetweenDrawings() {
        return betweenDrawings;
    }

    public int getBottomPadding() {
        return bottomPadding;
    }

    public int getHorizontalArrowOffset() {
        return horizontalArrowOffset;
    }

    public int getVerticalArrowX() {
        return verticalArrowX;
    }

    public int getCarportLengthMm(Specifications specifications) {
        return specifications.getLengthCm() * cmToMm;
    }

    public int getRightArrowSpace() {
        return rightArrowSpace;
    }

    public int getCarportWidthMm(Specifications specifications) {
        return specifications.getWidthCm() * cmToMm;
    }

    public int getFrontPostHeightMm() {
        return frontPostHeightMm;
    }

    public int getBackPostHeightMm() {
        return backPostHeightMm;
    }

    public int getSideViewRoofFallMm() {
        return frontPostHeightMm - backPostHeightMm;
    }

    public int getSideViewRealHeightMm() {
        return frontPostHeightMm;
    }

    public int getSideViewGroundY() {
        return frontPostHeightMm;
    }

    public int getSideViewRoofTopYAtX(Specifications specifications, int xMm) {

        return (int) Math.round((double) getSideViewRoofFallMm() * xMm / getCarportLengthMm(specifications));
    }

    public int getTopPostY() {
        return postDistanceFromSideMm;
    }

    public int getBottomPostY(Specifications specifications, List<Material> materials) {
        return getCarportWidthMm(specifications) - postDistanceFromSideMm - getPostWidthMm(materials);
    }

    public int getBottomPostOuterEdgeY(Specifications specifications, List<Material> materials) {
        return getBottomPostY(specifications, materials) + getPostWidthMm(materials);
    }

    public int getDistanceBetweenOuterPostEdgesMm(
        Specifications specifications, List<Material> materials) {

        return getBottomPostOuterEdgeY(specifications, materials) - getTopPostY();
    }

    public int getTopBeamY() {
        return getTopPostY();
    }

    public int getBottomBeamY(Specifications specifications, List<Material> materials) {

        int bottomPostY = getBottomPostY(specifications, materials);
        int postWidthMm = getPostWidthMm(materials);
        int beamWidthMm = getBeamTopViewWidthMm(materials);

        return bottomPostY + postWidthMm - beamWidthMm;
    }

    public double getScaledHeight(int realHeightMm, Specifications specifications) {

        return (double) realHeightMm * innerWidth / getCarportLengthMm(specifications);
    }

    public double getSideViewHeight(Specifications specifications) {

        return getScaledHeight(getSideViewRealHeightMm(), specifications);
    }

    public double getTopViewHeight(Specifications specifications) {

        return getScaledHeight(getCarportWidthMm(specifications), specifications);
    }

    public double getSideViewY() {
        return topPadding;
    }

    public double getTopViewY(Specifications specifications) {
        return getSideViewY() + getSideViewHeight(specifications) + betweenDrawings;
    }

    public double getTopViewBottomY(Specifications specifications) {
        return getTopViewY(specifications) + getTopViewHeight(specifications);
    }

    public double getTopViewWidthArrowY(Specifications specifications) {
        return getTopViewBottomY(specifications) + horizontalArrowOffset;
    }

    public double getOuterHeight(Specifications specifications) {
        return getTopViewBottomY(specifications) + bottomPadding;
    }

    public List<SvgPost> calculatePostsForFlatRoofWithoutWorkshop(
        Specifications specifications, List<Material> materials) {

        List<Integer> positionsFromFront = calculatePostPositionsFromFront(specifications, materials);

        int topSideY = getTopPostY();
        int bottomSideY = getBottomPostY(specifications, materials);

        List<SvgPost> posts = new ArrayList<>();

        for (int xPosition : positionsFromFront) {
            posts.add(new SvgPost(xPosition, topSideY));
            posts.add(new SvgPost(xPosition, bottomSideY));
        }

        return posts;
    }

    public List<SvgPost> calculateSideViewPostsForFlatRoofWithoutWorkshop(
        Specifications specifications, List<Material> materials) {

        List<Integer> positionsFromFront = calculatePostPositionsFromFront(specifications, materials);

        List<SvgPost> posts = new ArrayList<>();

        for (int xPosition : positionsFromFront) {
            posts.add(new SvgPost(xPosition, 0));
        }

        return posts;
    }

    public List<Integer> calculatePostPositionsFromFront(
        Specifications specifications, List<Material> materials) {

        int lengthMm = getCarportLengthMm(specifications);
        int postWidthMm = getPostWidthMm(materials);

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

    public List<Integer> calculateSideViewPostMeasurementPositions(
        Specifications specifications, List<Material> materials) {

        int lengthMm = getCarportLengthMm(specifications);

        List<Integer> positions = new ArrayList<>();
        positions.add(0);

        positions.addAll(calculatePostPositionsFromFront(specifications, materials));

        positions.add(lengthMm);

        return positions;
    }

    public List<Integer> calculateSideViewPostMeasurementDistances(
        Specifications specifications, List<Material> materials) {

        List<Integer> positions = calculateSideViewPostMeasurementPositions(specifications, materials);

        List<Integer> distances = new ArrayList<>();

        for (int i = 0; i < positions.size() - 1; i++) {
            distances.add(positions.get(i + 1) - positions.get(i));
        }

        return distances;
    }

    public int calculateRafterIntervalCount(
        Specifications specifications, List<Material> materials) {

        int carportLengthMm = getCarportLengthMm(specifications);
        int rafterWidthMm = getRafterWidthMm(materials);

        int spanBetweenOuterRafters = carportLengthMm - rafterWidthMm;

        int intervalCount = (int) Math.floor((double) spanBetweenOuterRafters / 500);

        if (intervalCount < 1) { intervalCount = 1; }

        while ((double) spanBetweenOuterRafters / intervalCount > 600) { intervalCount++; }

        return intervalCount;
    }

    public List<Integer> calculateRafterPositionsFromLeftSide(
        Specifications specifications, List<Material> materials) {

        int carportLengthMm = getCarportLengthMm(specifications);
        int rafterWidthMm = getRafterWidthMm(materials);

        int spanBetweenOuterRafters = carportLengthMm - rafterWidthMm;

        int intervalCount = calculateRafterIntervalCount(specifications, materials);

        List<Integer> positions = new ArrayList<>();

        for (int i = 0; i <= intervalCount; i++) {

            int xPosition = (int) Math.round((double) i * spanBetweenOuterRafters / intervalCount);
            positions.add(xPosition);
        }

        return positions;
    }

    public List<Integer> calculateRafterDistancesMm(
        Specifications specifications, List<Material> materials) {

        List<Integer> positions = calculateRafterPositionsFromLeftSide(specifications, materials);

        List<Integer> distances = new ArrayList<>();

        for (int i = 0; i < positions.size() - 1; i++) {
            distances.add(positions.get(i + 1) - positions.get(i));
        }

        return distances;
    }
}