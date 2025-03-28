package iut.gon.agarioclient.model.entity.pellet;

import iut.gon.agarioclient.model.entity.moveable.Entity;
import javafx.geometry.Point2D;

/**
 * The Pellet class represents a pellet in the game, extending the Entity class.
 * Pellets are inanimate objects that players can absorb to gain mass.
 */
public class Pellet extends Entity {
    /**
     * Constructs a new Pellet with the specified id, position, and mass.
     *
     * @param id       the unique identifier of the pellet
     * @param position the position of the pellet in the game space
     * @param mass     the mass of the pellet
     */
    public Pellet(String id, Point2D position, double mass) {
        super(id, position, mass);
    }

    /**
     * Calculates the radius of the pellet based on its mass.
     * This method provides a fixed radius for simplicity.
     *
     * @return the radius of the pellet
     */
    public double calculateRadius() {
        return 10;
    }
}