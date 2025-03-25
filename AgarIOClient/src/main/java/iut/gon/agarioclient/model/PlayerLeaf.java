package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

public class PlayerLeaf implements PlayerComponent {
    private String id;
    private Point2D position;
    private double mass;
    private double speed;
    private boolean alive;

    public PlayerLeaf(String id, Point2D position, double mass, double speed) {
        this.id = id;
        this.position = position;
        this.mass = mass;
        this.speed = speed;
        this.alive = true;
    }

    @Override
    public double getMass() {
        return mass;
    }

    @Override
    public void setMass(double mass) {
        this.mass = mass;
    }

    @Override
    public Point2D getPosition() {
        return position;
    }

    @Override
    public void setPosition(Point2D position) {
        this.position = position;
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
        return 10 * Math.sqrt(mass);
    }

    @Override
    public double calculateSpeed(double cursorX, double cursorY, double panelWidth, double panelHeight) {
        double maxSpeed = 100 / mass;
        double centerX = panelWidth / 2;
        double centerY = panelHeight / 2;
        double distanceX = cursorX - centerX;
        double distanceY = cursorY - centerY;
        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        double maxDistance = Math.sqrt(centerX * centerX + centerY * centerY);
        return maxSpeed * (distance / maxDistance);
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public String getId() {
        return id;
    }
}