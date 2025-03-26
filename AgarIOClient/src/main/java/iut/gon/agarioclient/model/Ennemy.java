// Ennemy.java
package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.Map;

public class Ennemy extends Player {
    private IA strat;
    private Point2D posE;
    private double massE;
    private double speedE;

    public Ennemy(String id, Point2D position, double mass, IA strat, double speed) {
        super(id, position, mass);
        this.posE = position;
        this.strat = strat;
        this.massE = mass;
        this.speedE = speed;
    }

    public IA getStrat() {
        return strat;
    }

    public void setStrat(IA strat) {
        this.strat = strat;
    }

    public void executeStrat() {
        strat.execute(this);
    }

    @Override
    public void setPosition(Point2D newPos) {
        this.posE = newPos;
    }

    @Override
    public Point2D getPosition() {
        return posE;
    }

    @Override
    public void setMass(double m) {
        this.massE = m;
    }

    @Override
    public double getMass() {
        return this.massE;
    }

    @Override
    public double getSpeed() {
        return this.speedE;
    }

    @Override
    public void setSpeed(double speed) {
        this.speedE = speed;
    }

    @Override
    public double calculateRadius() {
        return getMass();
    }

    @Override
    public double calculateSpeed(double targetX, double targetY, double mapWidth, double mapHeight) {
        double mass = getMass();
        double speed = (mass / Math.pow(mass, 1.44)) * 10;
        return speed;
    }

    public void checkCollisions(Map<Pellet, Circle> pelletCircles, Pane pane) {
        double ennemyRadius = calculateRadius();
        double eventHorizon = ennemyRadius + 100;

        pelletCircles.entrySet().removeIf(entry -> {
            Pellet pellet = entry.getKey();
            Circle pelletCircle = entry.getValue();
            double distance = getPosition().distance(pellet.getPosition());

            if (distance <= eventHorizon) {
                setMass(getMass() + pellet.getMass());
                pane.getChildren().remove(pelletCircle);
                pellet.removeFromCurrentNode();
                return true;
            }
            return false;
        });
    }
}