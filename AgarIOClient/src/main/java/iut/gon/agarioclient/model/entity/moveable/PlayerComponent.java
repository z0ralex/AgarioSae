package iut.gon.agarioclient.model.entity.moveable;

import javafx.geometry.Point2D;

/**
 * Interface representing a component of a player in the game.
 * Defines the essential methods that any player component must implement.
 */
public interface PlayerComponent {
    double getMass();
    void setMass(double mass);
    Point2DSerial getPosition();
    void setPosition(Point2DSerial position);
    double getSpeed();
    void setSpeed(double speed);
    double calculateRadius();
    double calculateSpeed(double cursorX, double cursorY, double panelWidth, double panelHeight);
    boolean isAlive();
    void setAlive(boolean alive);
}