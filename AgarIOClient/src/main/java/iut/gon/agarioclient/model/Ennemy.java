// Ennemy.java
package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

public class Ennemy extends Player {
    /**
     * Constructs a new Ennemy with the specified id, position, and mass.
     *
     * @param id       the unique identifier of the entity
     * @param position the position of the entity in the game space
     * @param mass     the mass of the entity
     */

    private IA strat;
    private Point2D posE;
    private double massE;

    public Ennemy(String id, Point2D position, double mass, IA strat, double speed) {
        super(id, position, mass);
        this.posE = position;
        this.strat = strat;
        this.massE = mass;
    }

    public void setStrat(IA strat) {
        this.strat = strat;
    }

    public void executeStrat() {
        strat.execute(this);
    }

    public void setPosition(Point2D newPos){
        this.posE = newPos;
    }
    public Point2D getPosition(){
        return posE;
    }

    public void setMass(double m){
        this.massE = m;
    }

    public double getMass(){
        return this.massE;
    }
}
