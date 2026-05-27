package app.service;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialsMapper;

import java.util.List;

public abstract class MaterialService {

    MaterialsMapper materialsMapper = new MaterialsMapper();

    /**
     * Returns the concrete {@link MaterialService} implementation for the given roof type.
     *
     * @param roofType the roof type selected for the carport
     * @return a {@link MaterialService} capable of calculating materials for that roof type
     * @throws UnsupportedOperationException if no implementation exists for the given roof type
     */
    public static MaterialService forRoofType(RoofType roofType) throws UnsupportedOperationException {
        return switch (roofType) {
            case FLAT -> new StandardFlatRoofCalculator();
            case RAISED -> throw new UnsupportedOperationException("Beregning for rejst tag er ikke implementeret endnu");
        };
    }


    /**
     * Calculates and inserts all required materials for an order into the {@code material_list} table.
     * <p>
     * Delegates to {@link #insertAllMaterials} for each material type defined by the concrete subclass.
     * </p>
     *
     * @param orderId        the ID of the order to finalize
     * @param specifications the carport dimensions and roof configuration
     * @param connectionPool the database connection pool
     * @throws DatabaseException if any material quantity calculation or database insert fails
     */
    public void finalizeOrder(int orderId, Specifications specifications, ConnectionPool connectionPool) throws DatabaseException {
        List<MaterialType> materialTypes = getMaterialTypes();
        insertAllMaterials(materialTypes, orderId, specifications, connectionPool);
    }

    /**
     * Iterates over the given material types and inserts a material line for each into the order.
     *
     * @param materialTypes  the ordered list of material types to insert
     * @param orderId        the ID of the order
     * @param specifications the carport dimensions and roof configuration
     * @param connectionPool the database connection pool
     * @throws DatabaseException if any insert fails
     */
    protected void insertAllMaterials(List<MaterialType> materialTypes, int orderId,
                                      Specifications specifications, ConnectionPool connectionPool) throws DatabaseException {

        for (MaterialType materialType : materialTypes) {
            insertMaterialLine(materialType, orderId, specifications, connectionPool);
        }
    }
    protected void insertRoofMaterial(RoofMaterial roofMaterial){

    }

    /**
     * Returns the ordered list of material types that the concrete subclass calculates.
     * <p>
     * Only the types returned here will have material lines inserted when
     * {@link #finalizeOrder} is called.
     * </p>
     *
     * @return an ordered list of {@link MaterialType} values relevant to this roof variant
     */
    protected abstract List<MaterialType> getMaterialTypes();

    /**
     * Calculates the quantity for a single material type and inserts it into the
     * {@code material_list} table via {@link MaterialsMapper}.
     *
     * @param materialType   the type of material to calculate and insert
     * @param orderId        the ID of the order
     * @param specifications the carport dimensions and roof configuration
     * @param connectionPool the database connection pool
     * @throws DatabaseException if the calculation yields an invalid result or a SQL error occurs
     */
    public void insertMaterialLine(MaterialType materialType, int orderId, Specifications specifications,
                                   ConnectionPool connectionPool) throws DatabaseException {
        int amount = switch (materialType) {
            case POST -> calculatePosts(specifications);
            case RAFTER -> calculateRafter(specifications);
            case REM -> calculateRem(specifications);
            case WIDE_BOARD_FRONT -> calculateWideBoardFront(specifications);
            case WIDE_BOARD_SIDE -> calculateWideBoardSide(specifications);
            case NARROW_BOARD_FRONT -> calculateNarrowBoardFront(specifications);
            case NARROW_BOARD_SIDE -> calculateNarrowBoardSide(specifications);
            case REGULAR -> calculateRegular(specifications);
            default -> throw new UnsupportedOperationException("Unknown type" + materialType);
        };

        materialsMapper.insertMaterialList(orderId, materialType.getId(), amount, materialType.getDescription(), connectionPool);
    }

    /**
     * Calculates the total cost of a material list by summing each material's unit price.
     *
     * @param materialList the list of materials for an order
     * @return the total cost in kroner
     */
    public double getMaterialListCost(List<MaterialListEntry> materialList) {
        double cost = 0;

        for (MaterialListEntry materialListEntry : materialList) {
            cost += materialListEntry.material().getPrice() * materialListEntry.amount();
        }

        return cost;
    }

    /**
     * Calculates the number of vertical support posts required.
     * Posts are placed along both sides of the carport and sunk 90 cm into the ground.
     *
     * @param specifications the carport dimensions and roof configuration
     * @return the number of posts
     */
    protected abstract int calculatePosts(Specifications specifications);

    /**
     * Calculates the number of rafters (spær) required.
     * Rafters run across the width of the carport and rest on the side beams.
     *
     * @param specifications the carport dimensions and roof configuration
     * @return the number of rafters
     */
    protected abstract int calculateRafter(Specifications specifications);

    /**
     * Calculates the number of side beams (remme) required.
     * Beams run along both sides of the carport, resting in notches cut into the posts.
     *
     * @param specifications the carport dimensions and roof configuration
     * @return the number of side beams
     */
    protected abstract int calculateRem(Specifications specifications);

    /**
     * Calculates the number of wide fascia boards (understernbrædder) for the front and back ends.
     *
     * @param specifications the carport dimensions and roof configuration
     * @return the number of wide fascia boards for the front and back ends
     */
    protected abstract int calculateWideBoardFront(Specifications specifications);

    /**
     * Calculates the number of wide fascia boards (understernbrædder) for the sides.
     *
     * @param specifications the carport dimensions and roof configuration
     * @return the number of wide fascia boards for the sides
     */
    protected abstract int calculateWideBoardSide(Specifications specifications);

    /**
     * Calculates the number of narrow fascia boards (oversternbrædder) for the front end.
     *
     * @param specifications the carport dimensions and roof configuration
     * @return the number of narrow fascia boards for the front end
     */
    protected abstract int calculateNarrowBoardFront(Specifications specifications);

    /**
     * Calculates the number of narrow fascia boards (oversternbrædder) for the sides.
     *
     * @param specifications the carport dimensions and roof configuration
     * @return the number of narrow fascia boards for the sides
     */
    protected abstract int calculateNarrowBoardSide(Specifications specifications);

    /**
     * Calculates the number of regular boards (løsholter) required for shed gable ends.
     * Only relevant when a workshop is attached to the carport.
     *
     * @param specifications the carport dimensions and roof configuration
     * @return the number of regular boards
     */
    protected abstract int calculateRegular(Specifications specifications);

    protected abstract int calculateRoof(Specifications specifications);
}
