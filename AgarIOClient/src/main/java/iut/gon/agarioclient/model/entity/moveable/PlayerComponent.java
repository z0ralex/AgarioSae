package iut.gon.agarioclient.model.entity.moveable;

import javafx.geometry.Point2D;

/**
 * Interface representing a component of a player in the game.
 * Defines the essential methods that any player component must implement.
 */
public interface PlayerComponent {
    /**
     * Gets the mass of the player component.
     *
     * @return the mass of the player component
     */
    double getMass();

    /**
     * Sets the mass of the player component.
     *
     * @param mass the new mass to set
     */
    void setMass(double mass);

    /**
     * Gets the position of the player component.
     *
     * @return the position as a Point2D object
     */
    Point2D getPosition();

    /**
     * Sets the position of the player component.
     *
     * @param position the new position to set
     */
    void setPosition(Point2D position);

    /**
     * Gets the speed of the player component.
     *
     * @return the speed of the player component
     */
    double getSpeed();

    /**
     * Sets the speed of the player component.
     *
     * @param speed the new speed to set
     */
    void setSpeed(double speed);

    /**
     * Calculates the radius of the player component based on its mass.
     *
     * @return the calculated radius
     */
    double calculateRadius();

    /**
     * Calculates the speed of the player component based on various parameters.
     *
     * @param cursorX   X-coordinate of cursor position
     * @param cursorY   Y-coordinate of cursor position
     * @param panelWidth  Width of the game panel
     * @param panelHeight Height of the game panel
     * @return the calculated speed value
     */
    double calculateSpeed(double cursorX, double cursorY, double panelWidth, double panelHeight);

    /**
     * Checks if the player component is alive.
     *
     * @return true if the player component is alive, false otherwise
     */
    boolean isAlive();

    /**
     * Sets the alive status of the player component.
     *
     * @param alive the new alive status
     */
    void setAlive(boolean alive);
}