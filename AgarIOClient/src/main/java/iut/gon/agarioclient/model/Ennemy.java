package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

public class Ennemy extends Entity{
    /**
     * Constructs a new Ennemy with the specified id, position, and mass.
     *
     * @param id       the unique identifier of the entity
     * @param position the position of the entity in the game space
     * @param mass     the mass of the entity
     */
    
    public Ennemy(String id, Point2D position, double mass) {
        super(id, position, mass);
    }
}
