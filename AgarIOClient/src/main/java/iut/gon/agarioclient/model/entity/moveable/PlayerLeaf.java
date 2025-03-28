// PlayerLeaf.java
package iut.gon.agarioclient.model.entity.moveable;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;

public class PlayerLeaf implements PlayerComponent {
    private String id;
    private Point2DSerial position;
    private DoubleProperty mass;
    private double speed;
    private boolean alive;

    public PlayerLeaf(String id, Point2DSerial position, double mass, double speed) {
        this.id = id;
        this.position = position;
        this.mass = new SimpleDoubleProperty(mass);
        this.speed = speed;
        this.alive = true;
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
    public Point2DSerial getPosition() {
        return position;
    }

    @Override
    public void setPosition(Point2DSerial position) {
        this.position = position;
    }

    public Point2DSerial positionProperty() {
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
        double mass = getMass();
        return (mass / Math.pow(mass, 1.1)) * 10;
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