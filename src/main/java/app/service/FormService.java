package app.service;

import app.entities.RoofMaterial;
import app.entities.RoofType;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.RoofMaterialMapper;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

public class FormService {
    public List<Integer> getRange(int min, int max, int interval) {

        List<Integer> numbers = new ArrayList<>();
        for (int i = min; i <= max; i += interval) {
            numbers.add(i);
        }
        return numbers;
    }

    public MaterialService getCorrectMaterialService(RoofType roofType){
        MaterialService materialService = null;
        switch(roofType){
            case FLAT -> {
                materialService = new StandardFlatRoofCalculator();
            }
            case RAISED -> {
                //TODO create path
                System.err.println("[FormService.getCorrectMaterialService] This path has not been created yet");
                throw new UnsupportedOperationException("This path has not been created");
            }
        }
        return materialService;
    }

    public void validateOrderForm(Context ctx) {
        requireValidRoofType(ctx.formParam("roofType"));
        requireInt(ctx.formParam("roofMaterial"), "Tagmateriale");
        requireInt(ctx.formParam("widthCm"), "Bredde");
        requireInt(ctx.formParam("lengthCm"), "Længde");

        if ("WITH".equals(ctx.formParam("workshop"))) {
            requireInt(ctx.formParam("workshop-width"), "Skurbredde");
            requireInt(ctx.formParam("workshop-length"), "Skurlængde");
        }

        requireNotBlank(ctx.formParam("firstName"), "Fornavn");
        requireNotBlank(ctx.formParam("lastName"), "Efternavn");
        requireNotBlank(ctx.formParam("address"), "Adresse");
        requireInt(ctx.formParam("postalCode"), "Postnummer");
        requireNotBlank(ctx.formParam("city"), "By");
        requireNotBlank(ctx.formParam("email"), "Email");
        requireNotBlank(ctx.formParam("phoneNumber"), "Telefonnummer");
    }

    private void requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " mangler.");
        }
    }

    private void requireInt(String value, String fieldName) {
        requireNotBlank(value, fieldName);
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " er ugyldigt.");
        }
    }

    private void requireValidRoofType(String value) {
        requireNotBlank(value, "Tagtype");
        try {
            RoofType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ugyldig tagtype: " + value);
        }
    }

    public List<RoofMaterial> getRoofByRoofType(RoofType roofType, ConnectionPool connectionPool) throws DatabaseException {
        RoofMaterialMapper roofMaterialMapper = new RoofMaterialMapper();
        List<RoofMaterial> roofMaterials = roofMaterialMapper.getAllRoofMaterial(connectionPool);
        return roofMaterials.stream()
                .filter(c -> c.getRoofType() == roofType).toList();
    }
}

