// PlayerLeaf.java
package iut.gon.agarioclient.model.entity.moveable;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;

public class PlayerLeaf implements PlayerComponent {
    private String id;
    private ObjectProperty<Point2D> position;
    private DoubleProperty mass;
    private double speed;
    private boolean alive;
    private double baseSpeed;

    public PlayerLeaf(String id, Point2D position, double mass, double speed) {
        this.id = id;
        this.position = new SimpleObjectProperty<>(position);
        this.mass = new SimpleDoubleProperty(mass);
        this.speed = speed;
        this.alive = true;
        this.baseSpeed = speed;
    }

    @Override
    public double getMass() {
        return mass.get();
    }

    @Override
    public void setMass(double mass) {
        this.mass.set(mass);
    }

    public DoubleProperty massProperty() {
        return mass;
    }

    @Override
    public Point2D getPosition() {
        return position.get();
    }

    @Override
    public void setPosition(Point2D position) {
        this.position.set(position);
    }

    public ObjectProperty<Point2D> positionProperty() {
        return position;
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public double calculateRadius() {
        return 10 * Math.sqrt(mass.get());
    }

    @Override
    public double calculateSpeed(double cursorX, double cursorY, double mapWidth, double mapHeight) {
        // Cap minimum mass to avoid division by zero
        double actualMass = Math.max(mass.get(), 0.1);
        return baseSpeed * (10 / Math.sqrt(actualMass));
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public String getId() {
        return id;
    }
}