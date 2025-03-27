package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

/**
 * The Pellet class represents a pellet in the game, extending the Entity class.
 * Pellets are inanimate objects that players can absorb to gain mass.
 */
public class Pellet extends Entity {
    /**
     * Constructs a new Entity with the specified id, position, and mass.
     *
     * @param id       the unique identifier of the entity
     * @param position the position of the entity in the game space
     * @param mass     the mass of the entity
     */
    public Pellet(String id, Point2D position, double mass) {
        super(id, position, mass);
    }

    /**
     * Calculates the radius of the pellet based on its mass.
     *
     * @return the radius of the pellet
     */
    public double calculateRadius() {
        return 10;
    }
}