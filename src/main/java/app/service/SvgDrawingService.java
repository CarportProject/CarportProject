package app.service;

import app.entities.MaterialType;
import app.entities.Specifications;
import app.exceptions.DatabaseException;

public class SvgDrawingService {

    private final SvgCalculationService svgCalculationService = new SvgCalculationService();

    public Svg createFlatRoofWithoutWorkshopSvg(Specifications specifications) throws DatabaseException {
        int carportLengthMm = svgCalculationService.getCarportLengthMm(specifications);
        int carportWidthMm = svgCalculationService.getCarportWidthMm(specifications);
        int sideViewRealHeightMm = svgCalculationService.getSideViewRealHeightMm();

        int outerWidth = svgCalculationService.getOuterWidth();
        int innerWidth = svgCalculationService.getInnerWidth();
        int innerSvgX = svgCalculationService.getArrowSpaceLeft();

        int sideViewExtraHeightMm = svgCalculationService.getSideViewExtraHeightMm();
        int sideViewBoxY = -sideViewExtraHeightMm;
        int sideViewBoxHeightMm = sideViewRealHeightMm + sideViewExtraHeightMm;

        double sideViewHeight = svgCalculationService.getScaledHeight(sideViewBoxHeightMm, specifications);
        double topViewHeight = svgCalculationService.getTopViewHeight(specifications);

        int sideViewY = (int) Math.round(svgCalculationService.getSideViewY());
        int sideViewGroundY = scaleSideViewYToOuterSvg(sideViewRealHeightMm, specifications, sideViewY, sideViewBoxY);
        int sideViewWidthArrowY = sideViewGroundY + svgCalculationService.getHorizontalArrowOffset();

        int topViewY = sideViewY + (int) Math.round(sideViewHeight) + svgCalculationService.getBetweenDrawings();
        int topViewBottomY = topViewY + (int) Math.round(topViewHeight);
        int topViewWidthArrowY = topViewBottomY + svgCalculationService.getHorizontalArrowOffset();

        double outerHeight = topViewWidthArrowY + 45;

        Svg outerSvg = new Svg(0, 0, String.valueOf(outerWidth), formatNumber(outerHeight),
                "0 0 " + outerWidth + " " + formatNumber(outerHeight));

        Svg sideViewSvg = new Svg(innerSvgX, sideViewY, String.valueOf(innerWidth), formatNumber(sideViewHeight),
                "0 " + sideViewBoxY + " " + carportLengthMm + " " + sideViewBoxHeightMm);

        Svg topViewSvg = new Svg(innerSvgX, topViewY, String.valueOf(innerWidth), formatNumber(topViewHeight),
                "0 0 " + carportLengthMm + " " + carportWidthMm);

        topViewSvg.addRectangle(0, 0, carportLengthMm, carportWidthMm, "normal");

        drawSideViewPosts(sideViewSvg, specifications);
        drawSideViewBeam(sideViewSvg, specifications);
        drawSideViewUnderStern(sideViewSvg, specifications);
        drawSideViewOverStern(sideViewSvg, specifications);

        drawTopViewPosts(topViewSvg, specifications);
        drawTopViewBeams(topViewSvg, specifications);
        drawTopViewRafters(topViewSvg, specifications);

        outerSvg.addSvg(sideViewSvg);
        outerSvg.addSvg(topViewSvg);

        drawVerticalDimensionArrow(outerSvg, svgCalculationService.getVerticalArrowX(),
                innerSvgX - 10, sideViewY, sideViewGroundY, getFullSideViewHeightText(specifications));

        int frontPostArrowX = (svgCalculationService.getVerticalArrowX() + innerSvgX) / 2;
        int frontPostTopY = scaleSideViewYToOuterSvg(0, specifications, sideViewY, sideViewBoxY);

        drawVerticalDimensionArrow(outerSvg, frontPostArrowX, innerSvgX - 10,
                frontPostTopY, sideViewGroundY, getFrontPostHeightText());

        int backFullHeightTopY = scaleSideViewYToOuterSvg(
                svgCalculationService.getSideViewRoofTopYAtX(specifications, carportLengthMm)
                        - svgCalculationService.getSideViewExtraHeightMm(),
                specifications, sideViewY, sideViewBoxY);

        int rightFullHeightArrowX = innerSvgX + innerWidth + 45;
        int rightFullHeightTickEndX = innerSvgX + innerWidth + 10;

        drawRightSideVerticalDimensionArrow(outerSvg, rightFullHeightArrowX, rightFullHeightTickEndX,
                backFullHeightTopY, sideViewGroundY, getBackFullSideViewHeightText());

        drawSideViewPostMeasurements(outerSvg, specifications, sideViewWidthArrowY);
        drawTopViewRafterMeasurements(outerSvg, specifications, topViewY);

        drawVerticalDimensionArrow(outerSvg, svgCalculationService.getVerticalArrowX(),
                innerSvgX - 10, topViewY, topViewY + (int) Math.round(topViewHeight),
                getWidthText(specifications));

        drawTopViewInnerPostDistanceMeasurement(outerSvg, specifications, innerSvgX, topViewY);

        drawHorizontalDimensionArrow(outerSvg, innerSvgX, innerSvgX + innerWidth,
                topViewWidthArrowY, getLengthText(specifications));

        return outerSvg;
    }

    // ===== DIMENSION ARROWS =====

    private void drawVerticalDimensionArrow(Svg svg, int arrowX, int tickEndX, int y1, int y2, String text) {
        svg.addLine(arrowX, y1, arrowX, y2, true, true, "normal");
        svg.addLine(arrowX, y1, tickEndX, y1, false, false, "normal");
        svg.addLine(arrowX, y2, tickEndX, y2, false, false, "normal");
        int textX = arrowX - 10;
        int textY = y1 + ((y2 - y1) / 2);
        svg.addText(14, "middle", textX, textY, -90, text);
    }

    private void drawRightSideVerticalDimensionArrow(Svg svg, int arrowX, int tickEndX, int y1, int y2, String text) {
        svg.addLine(arrowX, y1, arrowX, y2, true, true, "normal");
        svg.addLine(tickEndX, y1, arrowX, y1, false, false, "normal");
        svg.addLine(tickEndX, y2, arrowX, y2, false, false, "normal");
        int textX = arrowX + 12;
        int textY = y1 + ((y2 - y1) / 2);
        svg.addText(14, "middle", textX, textY, -90, text);
    }

    private void drawHorizontalDimensionArrow(Svg svg, int x1, int x2, int arrowY, String text) {
        int tickTopY = arrowY - 10;
        int tickBottomY = arrowY + 10;
        svg.addLine(x1, arrowY, x2, arrowY, true, true, "normal");
        svg.addLine(x1, tickTopY, x1, tickBottomY, false, false, "normal");
        svg.addLine(x2, tickTopY, x2, tickBottomY, false, false, "normal");
        int textX = x1 + ((x2 - x1) / 2);
        int textY = arrowY + 20;
        svg.addText(14, "middle", textX, textY, 0, text);
    }

    // ===== TOP VIEW DRAWING =====

    private void drawTopViewPosts(Svg svg, Specifications specifications) throws DatabaseException {
        int postWidthMm = svgCalculationService.getPostWidthMm();
        for (var post : svgCalculationService.calculatePostsForFlatRoofWithoutWorkshop(specifications)) {
            svg.addRectangle(post.xMm(), post.yMm(), postWidthMm, postWidthMm, "normal");
        }
    }

    private void drawTopViewBeams(Svg svg, Specifications specifications) throws DatabaseException {
        int carportLengthMm = svgCalculationService.getCarportLengthMm(specifications);
        int beamWidthMm = svgCalculationService.getBeamTopViewWidthMm();
        int topBeamY = svgCalculationService.getTopBeamY();
        int bottomBeamY = svgCalculationService.getBottomBeamY(specifications);
        svg.addRectangle(0, topBeamY, carportLengthMm, beamWidthMm, "normal");
        svg.addRectangle(0, bottomBeamY, carportLengthMm, beamWidthMm, "normal");
    }

    private void drawTopViewRafters(Svg svg, Specifications specifications) throws DatabaseException {
        int rafterWidthMm = svgCalculationService.getMaterialHeightMm(MaterialType.RAFTER);
        int carportWidthMm = svgCalculationService.getCarportWidthMm(specifications);
        for (int xPosition : svgCalculationService.calculateRafterPositionsFromLeftSide(specifications)) {
            svg.addRectangle(xPosition, 0, rafterWidthMm, carportWidthMm, "normal");
        }
    }

    // ===== SIDE VIEW DRAWING =====

    private void drawSideViewPosts(Svg svg, Specifications specifications) throws DatabaseException {
        int postWidthMm = svgCalculationService.getMaterialWidthMm(MaterialType.POST);
        int groundY = svgCalculationService.getSideViewGroundY();
        for (var post : svgCalculationService.calculateSideViewPostsForFlatRoofWithoutWorkshop(specifications)) {
            int postTopY = svgCalculationService.getSideViewRoofTopYAtX(specifications, post.xMm());
            int postHeight = groundY - postTopY;
            svg.addRectangle(post.xMm(), postTopY, postWidthMm, postHeight, "normal");
        }
    }

    private void drawSideViewBeam(Svg svg, Specifications specifications) throws DatabaseException {
        int carportLengthMm = svgCalculationService.getCarportLengthMm(specifications);
        int beamHeightMm = svgCalculationService.getMaterialWidthMm(MaterialType.REM);
        int frontTopY = svgCalculationService.getSideViewRoofTopYAtX(specifications, 0);
        int backTopY = svgCalculationService.getSideViewRoofTopYAtX(specifications, carportLengthMm);
        int frontBottomY = frontTopY + beamHeightMm;
        int backBottomY = backTopY + beamHeightMm;
        String points = "0," + frontTopY + " " + carportLengthMm + "," + backTopY + " " +
                carportLengthMm + "," + backBottomY + " " + "0," + frontBottomY;
        svg.addPolygon(points, "normal");
    }

    private void drawSideViewUnderStern(Svg svg, Specifications specifications) throws DatabaseException {
        int carportLengthMm = svgCalculationService.getCarportLengthMm(specifications);
        int underSternHeightMm = svgCalculationService.getUnderSternSideViewHeightMm();
        int frontBottomY = svgCalculationService.getSideViewRoofTopYAtX(specifications, 0);
        int backBottomY = svgCalculationService.getSideViewRoofTopYAtX(specifications, carportLengthMm);
        int frontTopY = frontBottomY - underSternHeightMm;
        int backTopY = backBottomY - underSternHeightMm;
        String points = "0," + frontTopY + " " + carportLengthMm + "," + backTopY + " " +
                carportLengthMm + "," + backBottomY + " " + "0," + frontBottomY;
        svg.addPolygon(points, "normal");
    }

    private void drawSideViewOverStern(Svg svg, Specifications specifications) throws DatabaseException {
        int carportLengthMm = svgCalculationService.getCarportLengthMm(specifications);
        int underSternHeightMm = svgCalculationService.getUnderSternSideViewHeightMm();
        int overSternHeightMm = svgCalculationService.getOverSternSideViewHeightMm();
        int overSternOverlapMm = svgCalculationService.getOverSternOverlapMm();
        int frontRoofTopY = svgCalculationService.getSideViewRoofTopYAtX(specifications, 0);
        int backRoofTopY = svgCalculationService.getSideViewRoofTopYAtX(specifications, carportLengthMm);
        int frontUnderSternTopY = frontRoofTopY - underSternHeightMm;
        int backUnderSternTopY = backRoofTopY - underSternHeightMm;
        int frontBottomY = frontUnderSternTopY + overSternOverlapMm;
        int backBottomY = backUnderSternTopY + overSternOverlapMm;
        int frontTopY = frontBottomY - overSternHeightMm;
        int backTopY = backBottomY - overSternHeightMm;
        String points = "0," + frontTopY + " " + carportLengthMm + "," + backTopY + " " +
                carportLengthMm + "," + backBottomY + " " + "0," + frontBottomY;
        svg.addPolygon(points, "normal");
    }

    // ===== MEASUREMENTS =====

    private void drawTopViewInnerPostDistanceMeasurement(Svg svg, Specifications specifications,
            int innerSvgX, int topViewY) throws DatabaseException {
        int fullWidthArrowX = svgCalculationService.getVerticalArrowX();
        int arrowX = (fullWidthArrowX + innerSvgX) / 2;
        int tickEndX = innerSvgX - 10;
        int y1 = scaleTopViewYToOuterSvg(svgCalculationService.getTopPostY(), specifications, topViewY);
        int y2 = scaleTopViewYToOuterSvg(svgCalculationService.getBottomPostOuterEdgeY(specifications), specifications, topViewY);
        svg.addLine(arrowX, y1, arrowX, y2, true, true, "normal");
        svg.addLine(arrowX, y1, tickEndX, y1, false, false, "normal");
        svg.addLine(arrowX, y2, tickEndX, y2, false, false, "normal");
        int textX = arrowX - 10;
        int textY = y1 + ((y2 - y1) / 2);
        svg.addText(14, "middle", textX, textY, -90, getInnerPostDistanceText(specifications));
    }

    private void drawTopViewRafterMeasurements(Svg svg, Specifications specifications, int topViewY) throws DatabaseException {
        var rafterPositions = svgCalculationService.calculateRafterPositionsFromLeftSide(specifications);
        var rafterDistances = svgCalculationService.calculateRafterDistancesMm(specifications);
        int lineY = topViewY - 20;
        int tickBottomY = topViewY - 5;
        int textY = lineY - 5;
        int firstX = scaleXToOuterSvg(rafterPositions.get(0), specifications);
        int lastX = scaleXToOuterSvg(rafterPositions.get(rafterPositions.size() - 1), specifications);
        svg.addLine(firstX, lineY, lastX, lineY, false, false, "normal");
        for (int rafterPosition : rafterPositions) {
            int tickX = scaleXToOuterSvg(rafterPosition, specifications);
            svg.addLine(tickX, lineY, tickX, tickBottomY, false, false, "normal");
        }
        for (int i = 0; i < rafterDistances.size(); i++) {
            int x1 = scaleXToOuterSvg(rafterPositions.get(i), specifications);
            int x2 = scaleXToOuterSvg(rafterPositions.get(i + 1), specifications);
            int textX = x1 + ((x2 - x1) / 2);
            svg.addText(12, "middle", textX, textY, 0, formatSpacingText(rafterDistances.get(i)));
        }
    }

    private void drawSideViewPostMeasurements(Svg svg, Specifications specifications, int arrowY) throws DatabaseException {
        var positions = svgCalculationService.calculateSideViewPostMeasurementPositions(specifications);
        var distances = svgCalculationService.calculateSideViewPostMeasurementDistances(specifications);
        int tickTopY = arrowY - 10;
        int tickBottomY = arrowY + 10;
        int textY = arrowY + 25;
        int firstX = scaleXToOuterSvg(positions.get(0), specifications);
        int lastX = scaleXToOuterSvg(positions.get(positions.size() - 1), specifications);
        svg.addLine(firstX, arrowY, lastX, arrowY, false, false, "normal");
        for (int position : positions) {
            int tickX = scaleXToOuterSvg(position, specifications);
            svg.addLine(tickX, tickTopY, tickX, tickBottomY, false, false, "normal");
        }
        for (int i = 0; i < distances.size(); i++) {
            int x1 = scaleXToOuterSvg(positions.get(i), specifications);
            int x2 = scaleXToOuterSvg(positions.get(i + 1), specifications);
            int textX = x1 + ((x2 - x1) / 2);
            svg.addText(12, "middle", textX, textY, 0, formatSpacingText(distances.get(i)));
        }
    }

    // ===== COORDINATE SCALING =====

    private int scaleTopViewYToOuterSvg(int yMm, Specifications specifications, int topViewY) {
        double scaledY = (double) yMm * svgCalculationService.getTopViewHeight(specifications)
                / svgCalculationService.getCarportWidthMm(specifications);
        return topViewY + (int) Math.round(scaledY);
    }

    private int scaleSideViewYToOuterSvg(int yMm, Specifications specifications, int sideViewY, int sideViewBoxY) {
        double scaledY = (double) (yMm - sideViewBoxY) * svgCalculationService.getInnerWidth()
                / svgCalculationService.getCarportLengthMm(specifications);
        return sideViewY + (int) Math.round(scaledY);
    }

    private int scaleXToOuterSvg(int xMm, Specifications specifications) {
        double scaledX = (double) xMm * svgCalculationService.getInnerWidth()
                / svgCalculationService.getCarportLengthMm(specifications);
        return svgCalculationService.getArrowSpaceLeft() + (int) Math.round(scaledX);
    }

    // ===== TEXT FORMATTING =====

    private String formatSpacingText(int distanceMm) {
        return (int) Math.round(distanceMm / 10.0) + " cm";
    }

    private String getInnerPostDistanceText(Specifications specifications) throws DatabaseException {
        int distanceCm = (int) Math.round(svgCalculationService.getDistanceBetweenOuterPostEdgesMm(specifications) / 10.0);
        return distanceCm + " cm";
    }

    private String getLengthText(Specifications specifications) {
        return specifications.getLengthCm() + " cm";
    }

    private String getWidthText(Specifications specifications) {
        return specifications.getWidthCm() + " cm";
    }

    private String getFullSideViewHeightText(Specifications specifications) throws DatabaseException {
        int fullHeightMm = svgCalculationService.getSideViewRealHeightMm() + svgCalculationService.getSideViewExtraHeightMm();
        return (int) Math.round(fullHeightMm / 10.0) + " cm";
    }

    private String getFrontPostHeightText() {
        return svgCalculationService.getFrontPostHeightMm() / 10 + " cm";
    }

    private String getBackFullSideViewHeightText() throws DatabaseException {
        int fullHeightMm = svgCalculationService.getBackPostHeightMm() + svgCalculationService.getSideViewExtraHeightMm();
        return (int) Math.round(fullHeightMm / 10.0) + " cm";
    }

    private String formatNumber(double number) {
        if (number == (int) number) {
            return String.valueOf((int) number);
        }
        return String.valueOf(number);
    }
}