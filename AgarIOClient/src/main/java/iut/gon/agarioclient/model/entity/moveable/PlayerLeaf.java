package iut.gon.agarioclient.model.entity.moveable;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;

/**
 * Represents a leaf component of a player in the game.
 * Implements the PlayerComponent interface.
 */
public class PlayerLeaf implements PlayerComponent {
    private String id;
    private ObjectProperty<Point2D> position;
    private DoubleProperty mass;
    private double speed;
    private boolean alive;

    /**
     * Constructs a new PlayerLeaf with the specified id, position, mass, and speed.
     *
     * @param id       the unique identifier of the player leaf
     * @param position the initial position of the player leaf
     * @param mass     the initial mass of the player leaf
     * @param speed    the initial speed of the player leaf
     */
    public PlayerLeaf(String id, Point2D position, double mass, double speed) {
        this.id = id;
        this.position = new SimpleObjectProperty<>(position);
        this.mass = new SimpleDoubleProperty(mass);
        this.speed = speed;
        this.alive = true;
    }

    /**
     * Gets the mass of the player leaf.
     *
     * @return the mass of the player leaf
     */
    @Override
    public double getMass() {
        return mass.get();
    }

    /**
     * Sets the mass of the player leaf.
     *
     * @param mass the new mass to set
     */
    @Override
    public void setMass(double mass) {
        this.mass.set(mass);
    }

    /**
     * Gets the mass property of the player leaf for binding and change listeners.
     *
     * @return the mass property
     */
    public DoubleProperty massProperty() {
        return mass;
    }

    /**
     * Gets the position of the player leaf.
     *
     * @return the position as a Point2D object
     */
    @Override
    public Point2D getPosition() {
        return position.get();
    }

    /**
     * Sets the position of the player leaf.
     *
     * @param position the new position to set
     */
    @Override
    public void setPosition(Point2D position) {
        this.position.set(position);
    }

    /**
     * Gets the position property of the player leaf for binding and change listeners.
     *
     * @return the position property
     */
    public ObjectProperty<Point2D> positionProperty() {
        return position;
    }

    /**
     * Gets the speed of the player leaf.
     *
     * @return the speed of the player leaf
     */
    @Override
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the speed of the player leaf.
     *
     * @param speed the new speed to set
     */
    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Calculates the radius of the player leaf based on its mass.
     *
     * @return the calculated radius
     */
    @Override
    public double calculateRadius() {
        return 10 * Math.sqrt(mass.get());
    }

    /**
     * Calculates the speed of the player leaf based on various parameters.
     *
     * @param cursorX   X-coordinate of cursor position
     * @param cursorY   Y-coordinate of cursor position
     * @param mapWidth  Width of the game map
     * @param mapHeight Height of the game map
     * @return the calculated speed value
     */
    @Override
    public double calculateSpeed(double cursorX, double cursorY, double mapWidth, double mapHeight) {
        double mass = getMass();
        return (mass / Math.pow(mass, 1.1)) * 10;
    }

    /**
     * Checks if the player leaf is alive.
     *
     * @return true if the player leaf is alive, false otherwise
     */
    @Override
    public boolean isAlive() {
        return alive;
    }

    /**
     * Sets the alive status of the player leaf.
     *
     * @param alive the new alive status
     */
    @Override
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * Gets the unique identifier of the player leaf.
     *
     * @return the unique identifier
     */
    public String getId() {
        return id;
    }
}