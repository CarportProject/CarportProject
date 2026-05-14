package app.service;

import app.entities.RoofMaterial;
import app.entities.RoofType;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.RoofMaterialMapper;

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

    public List<RoofMaterial> getRoofByRoofType(RoofType roofType, ConnectionPool connectionPool) throws DatabaseException {
        RoofMaterialMapper roofMaterialMapper = new RoofMaterialMapper();
        List<RoofMaterial> roofMaterials = roofMaterialMapper.getAllRoofMaterial(connectionPool);
        return roofMaterials.stream()
                .filter(c -> c.getRoofType() == roofType).toList();
    }
}

