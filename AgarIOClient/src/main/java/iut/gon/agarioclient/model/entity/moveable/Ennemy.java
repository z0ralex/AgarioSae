// Ennemy.java
package iut.gon.agarioclient.model.entity.moveable;

import iut.gon.agarioclient.model.Game;
import iut.gon.agarioclient.model.entity.ia.IA;
import iut.gon.agarioclient.model.entity.pellet.Pellet;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Ennemy extends Player {
    private IA strat;
    private Point2D posE;
    private DoubleProperty mass;
    private double speed;
    private boolean markedForRemoval = false;

    public Ennemy(String id, Point2D position, double mass, IA strat, double speed) {
        super(id, position, mass);
        this.posE = position;
        this.strat = strat;
        this.mass = new SimpleDoubleProperty(mass);
        this.speed = speed;
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
    public boolean isAlive() {
        return !isMarkedForRemoval();
    }

    @Override
    public void setPosition(Point2D newPos) {
        if(Game.isValidPosition(newPos)) {
            this.posE = newPos;
        }
    }

    @Override
    public Point2D getPosition() {
        return posE;
    }

    @Override
    public double getMass() {
        return mass.get();
    }

    @Override
    public void setMass(double mass) {
        this.mass.set(mass);
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
        double radius = 10 * Math.sqrt(getMass());
        return radius;
    }

    @Override
    public double calculateSpeed(double cursorX, double cursorY, double mapWidth, double mapHeight) {
        double mass = getMass();
        return ((mass / Math.pow(mass, 1.1)) * 10);
    }

    public Set<Pellet> checkCollisions(Collection<Pellet> pellets) {
        double ennemyRadius = calculateRadius();
        double eventHorizon = ennemyRadius + 10;
        Set<Pellet> eatenPellets = new HashSet<>();

        pellets.forEach(pellet -> {
            double distance = getPosition().distance(pellet.getPosition());

            if (distance <= eventHorizon) {
                setMass(getMass() + pellet.getMass());
                eatenPellets.add(pellet);
                pellet.removeFromCurrentNode();

            }
        });

        return eatenPellets;
    }

    public void markForRemoval() {
        if(!markedForRemoval){
            this.markedForRemoval = true;
            this.removeFromCurrentNode();
        }
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }
}