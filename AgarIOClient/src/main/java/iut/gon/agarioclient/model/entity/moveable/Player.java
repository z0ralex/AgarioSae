package iut.gon.agarioclient.model.entity.moveable;

import iut.gon.agarioclient.model.entity.pellet.PartialInvisibilityPellet;
import iut.gon.agarioclient.model.entity.pellet.Pellet;
import iut.gon.agarioclient.model.entity.pellet.SpeedBoostPellet;
import iut.gon.agarioclient.model.entity.pellet.SpeedReductionPellet;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;

import java.util.*;

/**
 * Represents a player in the game, which can be controlled by the user or AI.
 * Inherits from Entity and implements PlayerComponent.
 */
public class Player extends Entity implements PlayerComponent {
    // List of components that make up the player
    protected List<PlayerComponent> components = new ArrayList<>();
    private ObjectProperty<Point2D> position;
    private DoubleProperty mass;

    private boolean alive = true;
    private boolean markedForRemoval = false;

    private boolean isVisible = true;

    private long affectedUntil = -1;
    private double specialEffect = 1;
    private long gotEffectedAt;

    private long gotInvisbileAt = -1;

    private long InvisbileUntil = -1;

    /**
     * Constructs a new Player with the specified id, position, and mass.
     *
     * @param id       the unique identifier of the player
     * @param position the initial position of the player
     * @param mass     the initial mass of the player
     */
    public Player(String id, Point2D position, double mass) {
        super(id, position, mass);
        this.position = new SimpleObjectProperty<>(position);
        this.mass = new SimpleDoubleProperty(mass);
    }

    /**
     * Adds a component to the player.
     *
     * @param component the component to add
     */
    public void add(PlayerComponent component) {
        components.add(component);
    }

    /**
     * Removes a component from the player.
     *
     * @param component the component to remove
     */
    public void remove(PlayerComponent component) {
        components.remove(component);
    }

    /**
     * Gets the list of components that make up the player.
     *
     * @return the list of components
     */
    public List<PlayerComponent> getComponents() {
        return components;
    }

    private Point2D lastPosition = getPosition();

    /**
     * Gets the total mass of the player by summing the mass of all components.
     *
     * @return the total mass of the player
     */
    @Override
    public double getMass() {
        return components.stream().mapToDouble(PlayerComponent::getMass).sum();
    }

    /**
     * Sets the mass of the player and distributes it proportionally among components.
     *
     * @param mass the new mass to set
     */
    @Override
    public void setMass(double mass) {
        double totalMass = getMass();
        for (PlayerComponent component : components) {
            double proportion = component.getMass() / totalMass;
            component.setMass(mass * proportion);
        }
        this.mass.set(mass);
    }

    /**
     * Gets the direction of the player's movement.
     *
     * @return the direction as a normalized Point2D vector
     */
    public Point2D getDirection() {
        Point2D currentPosition = getPosition();
        Point2D direction = currentPosition.subtract(lastPosition);

        if (direction.magnitude() == 0) {
            return Point2D.ZERO;
        }
        return direction.normalize();
    }

    /**
     * Gets the average position of all components.
     *
     * @return the average position as a Point2D object
     */
    @Override
    public Point2D getPosition() {
        double x = components.stream().mapToDouble(c -> c.getPosition().getX()).average().orElse(0);
        double y = components.stream().mapToDouble(c -> c.getPosition().getY()).average().orElse(0);
        return new Point2D(x, y);
    }

    /**
     * Sets the position of the player and updates the position of all components.
     *
     * @param position the new position to set
     */
    @Override
    public void setPosition(Point2D position) {
        this.lastPosition = getPosition();
        this.position.set(position);
        for (PlayerComponent component : components) {
            component.setPosition(position);
        }
    }

    /**
     * Gets the average speed of all components.
     *
     * @return the average speed
     */
    @Override
    public double getSpeed() {
        return components.stream().mapToDouble(PlayerComponent::getSpeed).average().orElse(0);
    }

    /**
     * Sets the speed of the player and updates the speed of all components.
     *
     * @param speed the new speed to set
     */
    @Override
    public void setSpeed(double speed) {
        for (PlayerComponent component : components) {
            component.setSpeed(speed);
        }
    }

    /**
     * Calculates the average radius of all components.
     *
     * @return the average radius
     */
    @Override
    public double calculateRadius() {
        return components.stream().mapToDouble(PlayerComponent::calculateRadius).average().orElse(0);
    }

    /**
     * Sets a special effect multiplier for the player.
     *
     * @param specialEffect the special effect multiplier
     */
    public void setSpecialEffect(double specialEffect){
        this.specialEffect = specialEffect;
    }

    /**
     * Calculates the speed of the player based on its mass and special effects.
     *
     * @param cursorX   X-coordinate of cursor position (unused in this implementation)
     * @param cursorY   Y-coordinate of cursor position (unused in this implementation)
     * @param mapWidth  Width of the game map (unused in this implementation)
     * @param mapHeight Height of the game map (unused in this implementation)
     * @return the calculated speed value
     */
    @Override
    public double calculateSpeed(double cursorX, double cursorY, double mapWidth, double mapHeight) {
        double mass = getMass();
        return ((mass / Math.pow(mass, 1.1)) * 10) * specialEffect;
    }

    /**
     * Checks if the player is alive.
     *
     * @return true if the player is alive and has components, false otherwise
     */
    @Override
    public boolean isAlive() {
        return alive && !components.isEmpty();
    }

    /**
     * Sets the alive status of the player.
     *
     * @param alive the new alive status
     */
    @Override
    public void setAlive(boolean alive) {
        this.alive = alive;
        if (!alive) {
            markForRemoval();
        }
    }

    /**
     * Sets the timestamp when the player was last affected by a special effect.
     *
     * @param gotEffectedAt the timestamp
     */
    public void setGotEffectedAt(long gotEffectedAt) {
        this.gotEffectedAt = gotEffectedAt;
    }

    /**
     * Gets the timestamp when the player was last affected by a special effect.
     *
     * @return the timestamp
     */
    public long getGotEffectedAt() {
        return gotEffectedAt;
    }

    /**
     * Gets the position property of the player for binding and change listeners.
     *
     * @return the position property
     */
    public SimpleObjectProperty<Point2D> positionProperty() {
        return (SimpleObjectProperty<Point2D>) position;
    }

    /**
     * Gets the mass property of the player for binding and change listeners.
     *
     * @return the mass property
     */
    public DoubleProperty massProperty() {
        return mass;
    }

    /**
     * Calculates the new position of the player based on the target position and the map dimensions.
     * This method takes into account the player's speed and direction to determine the new position.
     * It also ensures that the new position is within the boundaries of the game map.
     *
     * @param targetPosition The target position the player is moving towards.
     * @param mapWidth       The width of the game map.
     * @param mapHeight      The height of the game map.
     * @return The new position of the player as a Point2D object.
     */
    public Point2D calculateNewPosition(Point2D targetPosition, double mapWidth, double mapHeight) {
        double speed = calculateSpeed(targetPosition.getX(), targetPosition.getY(), mapWidth, mapHeight);
        setSpeed(speed);

        Point2D direction = targetPosition.subtract(getPosition()).normalize();
        Point2D newPosition = getPosition().add(direction.multiply(getSpeed()));

        // Check for collisions with the map boundaries
        double newX = Math.max(0, Math.min(newPosition.getX(), mapWidth));
        double newY = Math.max(0, Math.min(newPosition.getY(), mapHeight));
        return new Point2D(newX, newY);
    }

    /**
     * Checks for collisions between the player and a collection of pellets.
     *
     * @param pellets Collection of pellets to check for collisions
     * @return Set of pellets that have been eaten by the player
     */
    public Set<Pellet> checkCollisionsWithPellet(Collection<Pellet> pellets) {
        double playerRadius = calculateRadius();
        double eventHorizon = playerRadius + 10;
        Set<Pellet> eatenPellets = new HashSet<>();

        pellets.forEach(pellet -> {
            double distance = getPosition().distance(pellet.getPosition());

            if (distance <= eventHorizon) {
                eatenPellets.add(pellet);

                if(pellet instanceof SpeedReductionPellet){
                    this.gotEffectedAt = System.currentTimeMillis();
                    this.affectedUntil = System.currentTimeMillis() + 4000;
                    this.setSpecialEffect(0.5);
                }

                if(pellet instanceof SpeedBoostPellet){
                    this.gotEffectedAt = System.currentTimeMillis();
                    this.affectedUntil = System.currentTimeMillis() + 4000;
                    this.setSpecialEffect(2);
                }

                if(pellet instanceof PartialInvisibilityPellet){
                    this.gotInvisbileAt = System.currentTimeMillis();
                    this.InvisbileUntil = System.currentTimeMillis() + 4000;
                    this.isVisible = false;
                }
                setMass(getMass() + pellet.getMass());
                pellet.removeFromCurrentNode();

            } else {
                // Reset Speed
                if (System.currentTimeMillis() > affectedUntil) {
                    this.setSpecialEffect(1.0);
                }
                // Reset Invisibility
                if (System.currentTimeMillis() > InvisbileUntil) {
                    this.isVisible = true;
                }
            }
        });

        return eatenPellets;
    }

    /**
     * Checks if the player is visible.
     *
     * @return true if the player is visible, false otherwise
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Checks for collisions between the player and a collection of enemies.
     *
     * @param enemies Collection of enemies to check for collisions
     * @return Set of enemies that have been eaten by the player
     */
    public Set<Ennemy> checkCollisionsWithEnemies(Collection<Ennemy> enemies) {
        double playerRadius = calculateRadius();
        double eventHorizon = playerRadius * 0.33;
        Set<Ennemy> eaten = new HashSet<>();

        enemies.forEach(enemy -> {
            double distance = getPosition().distance(enemy.getPosition());
            double overlap = Math.max(0, playerRadius + enemy.calculateRadius() - distance);

            if (getMass() >= enemy.getMass() * 1.33 && overlap >= playerRadius * 0.33) {
                eaten.add(enemy);
                setMass(getMass() + enemy.getMass());
                enemy.markForRemoval();
            }
        });
        return eaten;
    }

    /**
     * Checks for collisions between the player and a collection of other players.
     *
     * @param playerCircles Collection of other players to check for collisions
     * @return Set of players that have been eaten by this player
     */
    public Set<Player> checkCollisionsWithPlayers(Collection<Player> playerCircles) {
        double playerRadius = calculateRadius();
        double eventHorizon = playerRadius * 0.33;
        Set<Player> eaten = new HashSet<>();

        playerCircles.forEach(otherPlayer -> {
            double distance = getPosition().distance(otherPlayer.getPosition());
            double overlap = Math.max(0, playerRadius + otherPlayer.calculateRadius() - distance);

            if (this != otherPlayer && getMass() >= otherPlayer.getMass() * 1.33 && overlap >= playerRadius * 0.33) {
                eaten.add(otherPlayer);
                setMass(getMass() + otherPlayer.getMass());
                otherPlayer.markForRemoval();
            }
        });
        return eaten;
    }

    /**
     * Marks the player for removal from the game.
     */
    public void markForRemoval() {
        if(!markedForRemoval){
            this.markedForRemoval = true;
            removeFromCurrentNode();
            components.clear();
        }
    }

    /**
     * Checks if the player is marked for removal.
     *
     * @return true if marked for removal, false otherwise
     */
    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    /**
     * Divides the player into multiple components.
     *
     * @return List of new PlayerComponents created from the division
     */
    public List<PlayerComponent> divide() {
        // TODO: Implement divide logic
        return null;
    }

    /**
     * Merges the player components into a single entity.
     */
    public void merge() {
        // TODO: Implement merge logic
        // Implement merging logic based on the formula t = C + m/100
    }
}