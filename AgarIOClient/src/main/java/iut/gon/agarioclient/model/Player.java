// Player.java
package iut.gon.agarioclient.model;

import iut.gon.agarioclient.controller.AnimationManager;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Player extends Entity implements PlayerComponent {
    protected List<PlayerComponent> components = new ArrayList<>();
    private ObjectProperty<Point2D> position;
    private DoubleProperty mass;

    private boolean alive = true;
    private boolean markedForRemoval = false;

    public Player(String id, Point2D position, double mass) {
        super(id, position, mass);
        this.position = new SimpleObjectProperty<>(position);
        this.mass = new SimpleDoubleProperty(mass);
    }

    public void add(PlayerComponent component) {
        components.add(component);
    }

    public void remove(PlayerComponent component) {
        components.remove(component);
    }

    public List<PlayerComponent> getComponents() {
        return components;
    }

    private Point2D lastPosition = getPosition();


    @Override
    public double getMass() {
        return components.stream().mapToDouble(PlayerComponent::getMass).sum();
    }

    @Override
    public void setMass(double mass) {
        double totalMass = getMass();
        for (PlayerComponent component : components) {
            double proportion = component.getMass() / totalMass;
            component.setMass(mass * proportion);
        }
        this.mass.set(mass);
    }

    public Point2D getDirection() {
        Point2D currentPosition = getPosition();
        Point2D direction = currentPosition.subtract(lastPosition);

        if (direction.magnitude() == 0) {
            return Point2D.ZERO;
        }
        return direction.normalize();
    }


    @Override
    public Point2D getPosition() {
        double x = components.stream().mapToDouble(c -> c.getPosition().getX()).average().orElse(0);
        double y = components.stream().mapToDouble(c -> c.getPosition().getY()).average().orElse(0);
        return new Point2D(x, y);
    }

    @Override
    public void setPosition(Point2D position) {
        this.lastPosition = getPosition();
        this.position.set(position);
        for (PlayerComponent component : components) {
            component.setPosition(position);
        }
    }

    @Override
    public double getSpeed() {
        return components.stream().mapToDouble(PlayerComponent::getSpeed).average().orElse(0);
    }

    @Override
    public void setSpeed(double speed) {
        for (PlayerComponent component : components) {
            component.setSpeed(speed);
        }
    }

    @Override
    public double calculateRadius() {
        return components.stream().mapToDouble(PlayerComponent::calculateRadius).average().orElse(0);
    }

    @Override
    public double calculateSpeed(double cursorX, double cursorY, double mapWidth, double mapHeight) {
        double mass = getMass();
        return (mass / Math.pow(mass, 1.44)) * 10;
    }

    @Override
    public boolean isAlive() {
        return alive && !components.isEmpty();
    }

    @Override
    public void setAlive(boolean alive) {
        this.alive = alive;
        if (!alive) {
            markForRemoval();
        }
    }

    public SimpleObjectProperty<Point2D> positionProperty() {
        return (SimpleObjectProperty<Point2D>) position;
    }

    public DoubleProperty massProperty() {
        return mass;
    }

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

    public void checkCollisions(Map<Pellet, Circle> pelletCircles, Pane pane, AnimationManager animationManager) {
        double playerRadius = calculateRadius();
        double eventHorizon = playerRadius + 100;

        pelletCircles.entrySet().removeIf(entry -> {
            Pellet pellet = entry.getKey();
            Circle pelletCircle = entry.getValue();
            double distance = getPosition().distance(pellet.getPosition());

            if (distance <= eventHorizon) {
                animationManager.playPelletAbsorption(pelletCircle, getPosition());

                setMass(getMass() + pellet.getMass());
                pane.getChildren().remove(pelletCircle);
                pellet.removeFromCurrentNode();
                return true;
            }
            return false;
        });
    }

    public void checkCollisionsWithEnemies(Map<Ennemy, Circle> ennemyCircles, Pane pane, AnimationManager animationManager) {
        double playerRadius = calculateRadius();
        double eventHorizon = playerRadius * 0.33;

        ennemyCircles.entrySet().removeIf(entry -> {
            Ennemy ennemy = entry.getKey();
            Circle ennemyCircle = entry.getValue();
            double distance = getPosition().distance(ennemy.getPosition());
            double overlap = Math.max(0, playerRadius + ennemy.calculateRadius() - distance);

            if (getMass() >= ennemy.getMass() * 1.33 && overlap >= playerRadius * 0.33) {
                animationManager.playPelletAbsorption(ennemyCircle, getPosition());
                setMass(getMass() + ennemy.getMass());
                pane.getChildren().remove(ennemyCircle);
                ennemy.markForRemoval();
                return true;
            }
            return false;
        });
    }

    public void checkCollisionsWithPlayers(Map<Player, Circle> playerCircles, Pane pane, AnimationManager animationManager) {
        double playerRadius = calculateRadius();
        double eventHorizon = playerRadius * 0.33;

        playerCircles.entrySet().removeIf(entry -> {
            Player otherPlayer = entry.getKey();
            Circle otherPlayerCircle = entry.getValue();
            double distance = getPosition().distance(otherPlayer.getPosition());
            double overlap = Math.max(0, playerRadius + otherPlayer.calculateRadius() - distance);

            if (this != otherPlayer && getMass() >= otherPlayer.getMass() * 1.33 && overlap >= playerRadius * 0.33) {
                animationManager.playPelletAbsorption(otherPlayerCircle, getPosition());

                setMass(getMass() + otherPlayer.getMass());
                pane.getChildren().remove(otherPlayerCircle);
                otherPlayer.markForRemoval();
                return true;
            } else if (this == otherPlayer && overlap >= playerRadius * 0.33) {
                animationManager.playPelletAbsorption(otherPlayerCircle, getPosition());

                setMass(getMass() + otherPlayer.getMass());
                pane.getChildren().remove(otherPlayerCircle);
                otherPlayer.markForRemoval();
                return true;
            }
            return false;
        });
    }

    public void markForRemoval() {
        this.markedForRemoval = true;
        removeFromCurrentNode();
        components.clear();
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public void setInvisible(boolean b) {
        // TODO: Implement setInvisible logic
    }

    public List<PlayerComponent> divide() {
        // TODO: Implement divide logic
        return null;
    }

    public void merge() {
        // TODO: Implement merge logic
        // Implement merging logic based on the formula t = C + m/100
    }
}