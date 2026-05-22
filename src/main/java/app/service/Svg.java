package app.service;

public class Svg {

    private static final String svgTemplate =
        "<svg xmlns=\"http://www.w3.org/2000/svg\"\n" +
        " xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n" +
        "x=\"%d\" y=\"%d\"\n" +
        "width=\"%s\" height=\"%s\"\n" +
        "viewBox=\"%s\" \n" +
        "preserveAspectRatio=\"xMinYMin\">";

    private static final String svgRectTemplate =
        "<rect x=\"%d\" y=\"%d\"\n" +
        "height=\"%f\" width=\"%f\"\n" +
        "style=\"%s\"/>";

    private static final String svgLineTemplate =
            "<line x1=\"%d\" y1=\"%d\"\n" +
            "x2=\"%d\" y2=\"%d\"\n" +
            "style=\"%s\" />";

    private static final String svgTextTemplate =
        "<text font-size=\"%d\"\n" +
        "style=\"\n" +
        "text-anchor: %s\"\n" +
        "transform=\"\n" +
        "translate(%d,%d)\n" +
        "rotate(%d)\">\n" +
        "%s\n" +
        "</text>";

    private static final String svgArrowheads =
            "<defs>\n" +
            "<marker \n" +
            "id=\"beginArrow\" \n" +
            "markerWidth=\"12\" \n" +
            "markerHeight=\"12\" \n" +
            "refX=\"0\" \n" +
            "refY=\"6\" \n" +
            "orient=\"auto\">\n" +
            "<path d=\"M0,6 L12,0 L12,12 L0,6\" style=\"fill: #000000;\" />\n" +
            "</marker>\n" +
            "<marker \n" +
            "id=\"endArrow\" \n" +
            "markerWidth=\"12\" \n" +
            "markerHeight=\"12\" \n" +
            "refX=\"12\" \n" +
            "refY=\"6\" \n" +
            "orient=\"auto\">\n" +
            "<path d=\"M0,0 L12,6 L0,12 L0,0 \" style=\"fill: #000000;\" />\n" +
            "</marker>\n" +
            "</defs>";

    private StringBuilder svg = new StringBuilder();

    public Svg(int x, int y, String width, String height, String viewBox) {

        svg.append(String.format(svgTemplate, x, y, width, height, viewBox));
        svg.append(svgArrowheads);

    }

    public void addRectangle(int x, int y, double width, double height, String style){

        if (style.equals("default")) {style = "stroke-width:2px; stroke:#000000; fill:#ffffff";}

        svg.append(String.format(svgRectTemplate, x, y, width, height, style));

    }

    public void addLine(int x1, int y1, int x2, int y2,
                        boolean beginArrow, boolean endArrow, String style) {

        StringBuilder styleBuilder = new StringBuilder();

        if ("default".equals(style)) {
            styleBuilder.append("stroke:#000000;\n");
        } else {
            styleBuilder.append(style);

            if (!style.endsWith(";")) {
                styleBuilder.append(";");
            }
        }

        if (beginArrow) {
            styleBuilder.append("marker-start:url(#beginArrow);\n");
        }

        if (endArrow) {
            styleBuilder.append("marker-end:url(#endArrow);\n");
        }

        svg.append(String.format(
                svgLineTemplate,
                x1, y1, x2, y2, styleBuilder.toString()
        ));
    }

    public void addText(int fontSize, String anchor, int x, int y, int rotation, String text){

        svg.append(String.format(svgTextTemplate, fontSize, anchor, x, y, rotation, text));
    }

    public void addSvg(Svg innerSvg){

        svg.append(innerSvg.toString());
    }

    @Override
    public String toString() {
        return svg.append("</svg>").toString();
    }
}