package app.service;

import app.entities.Material;
import app.entities.Specifications;

import java.util.List;

public class SvgDrawingService {

    private final SvgCalculationService svgCalculationService = new SvgCalculationService();

    public Svg createFlatRoofWithoutWorkshopSvg(Specifications specifications, List<Material> materials) {
        int carportLengthMm = svgCalculationService.getCarportLengthMm(specifications);
        int carportWidthMm = svgCalculationService.getCarportWidthMm(specifications);
        int sideViewRealHeightMm = svgCalculationService.getSideViewRealHeightMm();

        int outerWidth = svgCalculationService.getOuterWidth();
        int innerWidth = svgCalculationService.getInnerWidth();
        int innerSvgX = svgCalculationService.getArrowSpaceLeft();

        int sideViewExtraHeightMm = svgCalculationService.getSideViewExtraHeightMm(materials);
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

        drawSideViewPosts(sideViewSvg, specifications, materials);
        drawSideViewBeam(sideViewSvg, specifications, materials);
        drawSideViewUnderStern(sideViewSvg, specifications, materials);
        drawSideViewOverStern(sideViewSvg, specifications, materials);

        drawTopViewPosts(topViewSvg, specifications, materials);
        drawTopViewBeams(topViewSvg, specifications, materials);
        drawTopViewRafters(topViewSvg, specifications, materials);

        outerSvg.addSvg(sideViewSvg);
        outerSvg.addSvg(topViewSvg);

        String lengthText = getLengthText(specifications);
        String fullSideHeightText = getFullSideViewHeightText(specifications, materials);
        String frontPostHeightText = getFrontPostHeightText();
        String widthText = getWidthText(specifications);

        drawVerticalDimensionArrow(outerSvg, svgCalculationService.getVerticalArrowX(),
            innerSvgX - 10, sideViewY, sideViewGroundY, fullSideHeightText);

        int frontPostArrowX = (svgCalculationService.getVerticalArrowX() + innerSvgX) / 2;

        int frontPostTopY = scaleSideViewYToOuterSvg(0, specifications, sideViewY, sideViewBoxY);

        drawVerticalDimensionArrow(outerSvg, frontPostArrowX, innerSvgX - 10,
            frontPostTopY, sideViewGroundY, frontPostHeightText);

        int backFullHeightTopY = scaleSideViewYToOuterSvg(svgCalculationService.getSideViewRoofTopYAtX(specifications, carportLengthMm)
            - svgCalculationService.getSideViewExtraHeightMm(materials), specifications, sideViewY, sideViewBoxY);

        int rightFullHeightArrowX = innerSvgX + innerWidth + 45;
        int rightFullHeightTickEndX = innerSvgX + innerWidth + 10;

        drawRightSideVerticalDimensionArrow(outerSvg, rightFullHeightArrowX, rightFullHeightTickEndX,
            backFullHeightTopY, sideViewGroundY, getBackFullSideViewHeightText(materials));

        drawSideViewPostMeasurements(outerSvg, specifications, materials, sideViewWidthArrowY);

        drawTopViewRafterMeasurements(outerSvg, specifications, materials, topViewY);

        drawVerticalDimensionArrow(outerSvg, svgCalculationService.getVerticalArrowX(),
    innerSvgX - 10, topViewY, topViewY + (int) Math.round(topViewHeight), widthText);

        drawTopViewInnerPostDistanceMeasurement(outerSvg, specifications, materials, innerSvgX, topViewY);

        drawHorizontalDimensionArrow(outerSvg, innerSvgX, innerSvgX + innerWidth,
            topViewWidthArrowY, lengthText);

        return outerSvg;
    }

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

    private void drawTopViewPosts(Svg svg, Specifications specifications, List<Material> materials) {

        int postWidthMm = svgCalculationService.getPostWidthMm(materials);

        for (var post : svgCalculationService.calculatePostsForFlatRoofWithoutWorkshop(specifications, materials)) {
            svg.addRectangle(post.xMm(), post.yMm(), postWidthMm, postWidthMm, "normal");
        }
    }

    private void drawTopViewBeams(Svg svg, Specifications specifications, List<Material> materials) {

        int carportLengthMm = svgCalculationService.getCarportLengthMm(specifications);
        int beamWidthMm = svgCalculationService.getBeamTopViewWidthMm(materials);

        int topBeamY = svgCalculationService.getTopBeamY();
        int bottomBeamY = svgCalculationService.getBottomBeamY(specifications, materials);

        svg.addRectangle(0, topBeamY, carportLengthMm, beamWidthMm, "normal");

        svg.addRectangle(0, bottomBeamY, carportLengthMm, beamWidthMm, "normal");
    }

    private void drawTopViewRafters(Svg svg, Specifications specifications, List<Material> materials) {

        int rafterWidthMm = svgCalculationService.getRafterWidthMm(materials);
        int carportWidthMm = svgCalculationService.getCarportWidthMm(specifications);

        for (int xPosition : svgCalculationService.calculateRafterPositionsFromLeftSide(specifications, materials)) {
            svg.addRectangle(xPosition, 0, rafterWidthMm, carportWidthMm, "normal");
        }
    }

    private void drawSideViewPosts(Svg svg, Specifications specifications, List<Material> materials) {

        int postWidthMm = svgCalculationService.getPostWidthMm(materials);
        int groundY = svgCalculationService.getSideViewGroundY();

        for (var post : svgCalculationService.calculateSideViewPostsForFlatRoofWithoutWorkshop(specifications, materials)) {
            int postTopY = svgCalculationService.getSideViewRoofTopYAtX(specifications, post.xMm());

            int postHeight = groundY - postTopY;

            svg.addRectangle(post.xMm(), postTopY, postWidthMm, postHeight, "normal");
        }
    }

    private void drawSideViewBeam(Svg svg, Specifications specifications, List<Material> materials) {
        int carportLengthMm = svgCalculationService.getCarportLengthMm(specifications);
        int beamHeightMm = svgCalculationService.getBeamSideViewHeightMm(materials);

        int frontTopY = svgCalculationService.getSideViewRoofTopYAtX(specifications, 0);
        int backTopY = svgCalculationService.getSideViewRoofTopYAtX(specifications, carportLengthMm);

        int frontBottomY = frontTopY + beamHeightMm;
        int backBottomY = backTopY + beamHeightMm;

        String points = "0," + frontTopY + " " + carportLengthMm + "," + backTopY + " " +
            carportLengthMm + "," + backBottomY + " " + "0," + frontBottomY;

        svg.addPolygon(points, "normal");
    }

    private void drawSideViewUnderStern(Svg svg, Specifications specifications, List<Material> materials) {
        int carportLengthMm = svgCalculationService.getCarportLengthMm(specifications);
        int underSternHeightMm = svgCalculationService.getUnderSternSideViewHeightMm(materials);

        int frontBottomY = svgCalculationService.getSideViewRoofTopYAtX(specifications, 0);
        int backBottomY = svgCalculationService.getSideViewRoofTopYAtX(specifications, carportLengthMm);

        int frontTopY = frontBottomY - underSternHeightMm;
        int backTopY = backBottomY - underSternHeightMm;

        String points = "0," + frontTopY + " " + carportLengthMm + "," + backTopY + " " +
            carportLengthMm + "," + backBottomY + " " + "0," + frontBottomY;

        svg.addPolygon(points, "normal");
    }

    private void drawSideViewOverStern(Svg svg, Specifications specifications, List<Material> materials) {
        int carportLengthMm = svgCalculationService.getCarportLengthMm(specifications);

        int underSternHeightMm = svgCalculationService.getUnderSternSideViewHeightMm(materials);
        int overSternHeightMm = svgCalculationService.getOverSternSideViewHeightMm(materials);
        int overSternOverlapMm = svgCalculationService.getOverSternOverlapMm(materials);

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

    private void drawTopViewInnerPostDistanceMeasurement(Svg svg, Specifications specifications,
         List<Material> materials, int innerSvgX, int topViewY) {

        int fullWidthArrowX = svgCalculationService.getVerticalArrowX();
        int arrowX = (fullWidthArrowX + innerSvgX) / 2;
        int tickEndX = innerSvgX - 10;

        int y1 = scaleTopViewYToOuterSvg(svgCalculationService.getTopPostY(), specifications, topViewY);

        int y2 = scaleTopViewYToOuterSvg(svgCalculationService.
            getBottomPostOuterEdgeY(specifications, materials), specifications, topViewY);

        svg.addLine(arrowX, y1, arrowX, y2, true, true, "normal");

        svg.addLine(arrowX, y1, tickEndX, y1, false, false, "normal");

        svg.addLine(arrowX, y2, tickEndX, y2, false, false, "normal");

        int textX = arrowX - 10;
        int textY = y1 + ((y2 - y1) / 2);

        svg.addText(14, "middle", textX, textY,
    -90, getInnerPostDistanceText(specifications, materials));
    }

    private void drawTopViewRafterMeasurements(Svg svg, Specifications specifications, List<Material> materials, int topViewY) {

        var rafterPositions = svgCalculationService.calculateRafterPositionsFromLeftSide(specifications, materials);
        var rafterDistances = svgCalculationService.calculateRafterDistancesMm(specifications, materials);

        int lineY = topViewY - 20;
        int tickBottomY = topViewY - 5;
        int textY = lineY - 5;

        int firstX = scaleTopViewXToOuterSvg(rafterPositions.get(0), specifications);
        int lastX = scaleTopViewXToOuterSvg(rafterPositions.get(rafterPositions.size() - 1), specifications);

        svg.addLine(firstX, lineY, lastX, lineY, false, false, "normal");

        for (int rafterPosition : rafterPositions) {

            int tickX = scaleTopViewXToOuterSvg(rafterPosition, specifications);
            svg.addLine(tickX, lineY, tickX, tickBottomY, false, false, "normal");
        }

        for (int i = 0; i < rafterDistances.size(); i++) {

            int x1 = scaleTopViewXToOuterSvg(rafterPositions.get(i), specifications);
            int x2 = scaleTopViewXToOuterSvg(rafterPositions.get(i + 1), specifications);

            int textX = x1 + ((x2 - x1) / 2);

            svg.addText(12, "middle", textX, textY, 0, formatSpacingText(rafterDistances.get(i)));
        }
    }

    private void drawSideViewPostMeasurements(Svg svg, Specifications specifications, List<Material> materials, int arrowY) {

        var positions = svgCalculationService.calculateSideViewPostMeasurementPositions(specifications, materials);
        var distances = svgCalculationService.calculateSideViewPostMeasurementDistances(specifications, materials);

        int tickTopY = arrowY - 10;
        int tickBottomY = arrowY + 10;
        int textY = arrowY + 25;

        int firstX = scaleSideViewXToOuterSvg(positions.get(0), specifications);
        int lastX = scaleSideViewXToOuterSvg(positions.get(positions.size() - 1), specifications);

        svg.addLine(firstX, arrowY, lastX, arrowY, false, false, "normal");

        for (int position : positions) {

            int tickX = scaleSideViewXToOuterSvg(position, specifications);

            svg.addLine(tickX, tickTopY, tickX, tickBottomY, false, false, "normal");
        }

        for (int i = 0; i < distances.size(); i++) {

            int x1 = scaleSideViewXToOuterSvg(positions.get(i), specifications);
            int x2 = scaleSideViewXToOuterSvg(positions.get(i + 1), specifications);

            int textX = x1 + ((x2 - x1) / 2);

            svg.addText(12, "middle", textX, textY, 0, formatSpacingText(distances.get(i)));
        }
    }

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

    private int scaleTopViewXToOuterSvg(int xMm, Specifications specifications) {

        double scaledX = (double) xMm * svgCalculationService.getInnerWidth()
            / svgCalculationService.getCarportLengthMm(specifications);

        return svgCalculationService.getArrowSpaceLeft() + (int) Math.round(scaledX);
    }

    private int scaleSideViewXToOuterSvg(int xMm, Specifications specifications) {

        double scaledX = (double) xMm * svgCalculationService.getInnerWidth()
            / svgCalculationService.getCarportLengthMm(specifications);

        return svgCalculationService.getArrowSpaceLeft() + (int) Math.round(scaledX);
    }

    private String formatSpacingText(int distanceMm) {

        int distanceCm = (int) Math.round(distanceMm / 10.0);

        return distanceCm + " cm";
    }

    private String getInnerPostDistanceText(Specifications specifications, List<Material> materials) {

        int distanceCm = (int) Math.round(svgCalculationService.
            getDistanceBetweenOuterPostEdgesMm(specifications, materials) / 10.0);

        return distanceCm + " cm";
    }

    private String getLengthText(Specifications specifications) {

        return specifications.getLengthCm() + " cm";
    }

    private String getWidthText(Specifications specifications) {

        return specifications.getWidthCm() + " cm";
    }

    private String getFullSideViewHeightText(Specifications specifications, List<Material> materials) {

        int fullHeightMm = svgCalculationService.getSideViewRealHeightMm()
            + svgCalculationService.getSideViewExtraHeightMm(materials);

        int fullHeightCm = (int) Math.round(fullHeightMm / 10.0);

        return fullHeightCm + " cm";
    }

    private String getFrontPostHeightText() {

        return svgCalculationService.getFrontPostHeightMm() / 10 + " cm";
    }

    private String getBackPostHeightText() {

        return svgCalculationService.getBackPostHeightMm() / 10 + " cm";
    }

    private String formatNumber(double number) {

        if (number == (int) number) {
            return String.valueOf((int) number);
        }

        return String.valueOf(number);
    }

    private String getBackFullSideViewHeightText(List<Material> materials) {

        int fullHeightMm = svgCalculationService.getBackPostHeightMm()
            + svgCalculationService.getSideViewExtraHeightMm(materials);

        int fullHeightCm = (int) Math.round(fullHeightMm / 10.0);

        return fullHeightCm + " cm";
    }
}