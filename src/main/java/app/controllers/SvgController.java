package app.controllers;

import app.service.Svg;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Locale;

public class SvgController {

public static void addRoutes(Javalin app){
    app.get("/svg", SvgController::showSVG);
}

    public static void showSVG(Context ctx) {

        Locale.setDefault(new Locale("en", "US"));

        Svg carportSvg = new Svg(0, 0, "100%", "auto", "0 0 850 690");

        carportSvg.addRectangle(0, 0, 600, 780, "default");

        carportSvg.addLine(10, 10, 50, 50, false, true, "default");

        carportSvg.addText(20, "middle", 100, 100, 0, "Test text");


        ctx.attribute("svg", carportSvg.toString());

        ctx.render("SVGTest.html");
    }
}