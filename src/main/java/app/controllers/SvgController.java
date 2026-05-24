/*
package app.controllers;

import app.entities.Specifications;
import app.entities.SvgPost;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.SpecificationMapper;
import app.entities.Material;
import app.persistence.MaterialsMapper;
import app.service.SvgCalculationService;
import app.service.SvgDrawingService;
import app.util.ErrorRenderer;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.List;


public class SvgController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/test/specification/{id}", ctx -> testFetchSpecification(ctx, connectionPool));
        app.get("/svg", ctx -> showSvgForTestSpecification(ctx, connectionPool));
    }

    private static void testFetchSpecification(Context ctx, ConnectionPool connectionPool) {

        int specificationId = Integer.parseInt(ctx.pathParam("id"));

        try {
            SpecificationMapper specificationMapper = new SpecificationMapper();

            Specifications specs = specificationMapper.findSpecificationsById(
                    specificationId,
                    connectionPool
            );

            SvgCalculationService svgCalculationService = new SvgCalculationService();

            MaterialsMapper materialsMapper = new MaterialsMapper();
            List<Material> materials = materialsMapper.getAllMaterials(connectionPool);

            List<SvgPost> posts = svgCalculationService.calculatePostsForFlatRoofWithoutWorkshop(specs, materials);

            ctx.result(
                    "Specification fetched:\n" +
                            "ID: " + specs.getId() + "\n" +
                            "Width: " + specs.getWidthCm() + " cm\n" +
                            "Length: " + specs.getLengthCm() + " cm\n" +
                            "Roof type: " + specs.getRoofType() + "\n" +
                            "Roof pitch: " + specs.getRoofPitch() + "\n" +
                            "Posts: " + posts
            );

        } catch (DatabaseException e) {
            System.err.println("[SvgController.testFetchSpecification] " + e.getMessage());
            ctx.status(500).result("Could not fetch specifications");
        }
    }

    private static void showSvgForTestSpecification(Context ctx, ConnectionPool connectionPool) {
        int specificationId = 1; // skift her for test

        try {
            SpecificationMapper specificationMapper = new SpecificationMapper();
            Specifications specs = specificationMapper.findSpecificationsById(specificationId, connectionPool);

            System.out.println("SVG bruger specification id: " + specificationId);
            System.out.println("Width: " + specs.getWidthCm());
            System.out.println("Length: " + specs.getLengthCm());

            MaterialsMapper materialsMapper = new MaterialsMapper();
            List<Material> materials = materialsMapper.getAllMaterials(connectionPool);

            SvgDrawingService svgDrawingService = new SvgDrawingService();

            String svg = svgDrawingService.createFlatRoofWithoutWorkshopSvg(specs, materials).toString();

            ctx.attribute("svg", svg);
            ctx.attribute("specs", specs);
            ctx.render("Svg.html");

        } catch (DatabaseException e) {
            System.err.println("[SvgController.showSvgForTestSpecification] " + e.getMessage());
            ErrorRenderer.renderError(ctx, 500, "SVG error", "Could not create SVG from specification id" + specificationId);
            ctx.status(500).result("Could not create SVG from specification id " + specificationId);

        }
    }
}*/