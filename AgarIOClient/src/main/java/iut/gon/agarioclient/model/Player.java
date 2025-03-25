// Player.java
package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

/**
 * The Player class represents a player in the game, extending the Entity class.
 * It includes additional attributes like speed and methods to calculate radius and speed.
 */
public class Player extends Entity {
    private double speed;
    private boolean alive;

    /**
     * Constructs a new Player with the specified id, position, mass, and speed.
     *
     * @param id       the unique identifier of the player
     * @param position the position of the player in the game space
     * @param mass     the mass of the player
     * @param speed    the speed of the player
     */
    public Player(String id, Point2D position, double mass, double speed) {
        super(id, position, mass);
        this.speed = speed;
        this.alive = true; // Player is alive by default
    }

    /**
     * Returns the speed of the player.
     *
     * @return the speed of the player
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the speed of the player.
     *
     * @param speed the new speed of the player
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Calculates the radius of the player based on its mass.
     *
     * @return the radius of the player
     */
    public double calculateRadius() {
        return 10 * Math.sqrt(getMass());
    }

    /**
     * Calculates the speed of the player based on the cursor position and panel dimensions.
     *
     * @param cursorX     the x-coordinate of the cursor
     * @param cursorY     the y-coordinate of the cursor
     * @param panelWidth  the width of the panel
     * @param panelHeight the height of the panel
     * @return the calculated speed of the player
     */
    public double calculateSpeed(double cursorX, double cursorY, double panelWidth, double panelHeight) {
        double maxSpeed = 100 / getMass(); // Example max speed calculation
        double centerX = panelWidth / 2;
        double centerY = panelHeight / 2;
        double distanceX = cursorX - centerX;
        double distanceY = cursorY - centerY;
        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        double maxDistance = Math.sqrt(centerX * centerX + centerY * centerY);
        return maxSpeed * (distance / maxDistance);
    }

    /**
     * Returns whether the player is alive.
     *
     * @return true if the player is alive, false otherwise
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Sets the alive status of the player.
     *
     * @param alive the new alive status of the player
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}