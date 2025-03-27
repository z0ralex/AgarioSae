package iut.gon.agarioclient.model.entity.moveable;

import javafx.geometry.Point2D;

public interface PlayerComponent {
    double getMass();
    void setMass(double mass);
    Point2D getPosition();
    void setPosition(Point2D position);
    double getSpeed();
    void setSpeed(double speed);
    double calculateRadius();
    double calculateSpeed(double cursorX, double cursorY, double panelWidth, double panelHeight);
    boolean isAlive();
    void setAlive(boolean alive);
}