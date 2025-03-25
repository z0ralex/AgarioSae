package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

public abstract class Entity {
    private final String id;
    private Point2D position;
    private double mass;

    public Entity(String id, Point2D position, double mass) {
        this.id = id;
        this.position = position;
        this.mass = mass;
    }

    public String getId() {
        return id;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }
}