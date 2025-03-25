package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

public class Ennemy extends Player{
    /**
     * Constructs a new Ennemy with the specified id, position, and mass.
     *
     * @param id       the unique identifier of the entity
     * @param position the position of the entity in the game space
     * @param mass     the mass of the entity
     */

    private IA strat;

    public Ennemy(String id, Point2D position, double mass, IA strat, double speed) {
        super(id, position, mass, speed);
        this.strat = strat;
    }

    public void setStrat(IA strat) {
        this.strat = strat;
    }

    public void executeStrat(){
        strat.execute();
    }
}
