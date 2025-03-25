package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

/**
 * The Entity class represents a basic game entity with a unique identifier, position, and mass.
 * This class serves as a base class for other specific entities like players and pellets.
 */
public class Entity {
    private final String id;
    private Point2D position;
    private double mass;

    /**
     * Constructs a new Entity with the specified id, position, and mass.
     *
     * @param id       the unique identifier of the entity
     * @param position the position of the entity in the game space
     * @param mass     the mass of the entity
     */
    public Entity(String id, Point2D position, double mass) {
        this.id = id;
        this.position = position;
        this.mass = mass;
    }

    /**
     * Returns the unique identifier of the entity.
     *
     * @return the unique identifier of the entity
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the position of the entity in the game space.
     *
     * @return the position of the entity
     */
    public Point2D getPosition() {
        return position;
    }

    /**
     * Sets the position of the entity in the game space.
     *
     * @param position the new position of the entity
     */
    public void setPosition(Point2D position) {
        this.position = position;
    }

    /**
     * Returns the mass of the entity.
     *
     * @return the mass of the entity
     */
    public double getMass() {
        return mass;
    }

    /**
     * Sets the mass of the entity.
     *
     * @param mass the new mass of the entity
     */
    public void setMass(double mass) {
        this.mass = mass;
    }
}