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

    private boolean markedForRemoval = false;

    public Ennemy(String id, Point2D position, double mass, IA strat, double speed) {
        super(id, position, mass);
        this.posE = position;
        this.strat = strat;
        this.massE = mass;
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
    public double calculateRadius() {
        return 10 * Math.sqrt(getMass());
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

    public void markForRemoval() {
        this.markedForRemoval = true;
        this.removeFromCurrentNode();
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }
}