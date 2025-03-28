package iut.gon.agarioclient.model.entity.moveable;

import iut.gon.agarioclient.model.Game;
import iut.gon.agarioclient.model.entity.ia.IA;
import iut.gon.agarioclient.model.entity.pellet.Pellet;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents an enemy entity in the game, which is a type of Player controlled by AI.
 * Inherits from Player class and implements specific enemy behaviors.
 */
public class Ennemy extends Player implements Serializable {
    private IA strat;
    private Point2DSerial posE;
    private double mass;
    private double speed;
    private boolean markedForRemoval = false;

    /**
     * Constructs a new Enemy with specified parameters.
     *
     * @param id       Unique identifier for the enemy
     * @param position Initial position of the enemy
     * @param mass     Initial mass of the enemy
     * @param strat    AI strategy controlling this enemy
     * @param speed    Initial speed of the enemy
     */
    public Ennemy(String id, Point2DSerial position, double mass, IA strat, double speed) {
        super(id, position, mass);
        this.posE = position;
        this.strat = strat;
        this.mass = mass;
        this.speed = speed;
    }

    /**
     * Gets the current AI strategy of this enemy.
     *
     * @return The current IA strategy
     */
    public IA getStrat() {
        return strat;
    }

    /**
     * Sets a new AI strategy for this enemy.
     *
     * @param strat The new IA strategy to set
     */
    public void setStrat(IA strat) {
        this.strat = strat;
    }

    /**
     * Executes the current AI strategy for this enemy.
     */
    public void executeStrat() {
        strat.execute(this);
    }

    /**
     * Checks if the enemy is still alive in the game.
     *
     * @return true if the enemy is alive (not marked for removal), false otherwise
     */
    @Override
    public boolean isAlive() {
        return !isMarkedForRemoval();
    }

    /**
     * Sets the position of the enemy if it's valid within the game boundaries.
     *
     * @param newPos The new position to set
     */
    @Override
    public void setPosition(Point2DSerial newPos) {
        if(Game.isValidPosition(newPos)) {
            this.posE = newPos;
        }
    }

    /**
     * Gets the current position of the enemy.
     *
     * @return The current position as Point2D
     */
    @Override
    public Point2DSerial getPosition() {
        return posE;
    }

    /**
     * Gets the current mass of the enemy.
     *
     * @return The current mass value
     */
    @Override
    public double getMass() {
        return mass;
    }

    /**
     * Sets the mass of the enemy.
     *
     * @param mass The new mass value
     */
    @Override
    public void setMass(double mass) {
        this.mass = mass;
    }

    /**
     * Gets the current speed of the enemy.
     *
     * @return The current speed value
     */
    @Override
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the speed of the enemy.
     *
     * @param speed The new speed value
     */
    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Calculates the visual radius of the enemy based on its mass.
     *
     * @return The calculated radius
     */
    @Override
    public double calculateRadius() {
        double radius = 10 * Math.sqrt(getMass());
        return radius;
    }

    /**
     * Calculates the speed of the enemy based on its mass and other parameters.
     *
     * @param cursorX   X-coordinate of cursor position (unused in this implementation)
     * @param cursorY   Y-coordinate of cursor position (unused in this implementation)
     * @param mapWidth  Width of the game map (unused in this implementation)
     * @param mapHeight Height of the game map (unused in this implementation)
     * @return The calculated speed value
     */
    @Override
    public double calculateSpeed(double cursorX, double cursorY, double mapWidth, double mapHeight) {
        double mass = getMass();
        return ((mass / Math.pow(mass, 1.1)) * 10);
    }

    /**
     * Checks for collisions between this enemy and a collection of pellets.
     *
     * @param pellets Collection of pellets to check for collisions
     * @return Set of pellets that have been eaten by this enemy
     */
    public Set<Pellet> checkCollisions(Collection<Pellet> pellets) {
        double ennemyRadius = calculateRadius();
        double eventHorizon = ennemyRadius + 10;
        Set<Pellet> eatenPellets = new HashSet<>();

        pellets.forEach(pellet -> {
            double distance = getPosition().distance(pellet.getPosition());

            if (distance <= eventHorizon) {
                setMass(getMass() + pellet.getMass());
                eatenPellets.add(pellet);
                pellet.removeFromCurrentNode();
            }
        });

        return eatenPellets;
    }

    /**
     * Marks this enemy for removal from the game.
     */
    public void markForRemoval() {
        if(!markedForRemoval){
            this.markedForRemoval = true;
            this.removeFromCurrentNode();
        }
    }

    /**
     * Checks if this enemy is marked for removal.
     *
     * @return true if marked for removal, false otherwise
     */
    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }
}