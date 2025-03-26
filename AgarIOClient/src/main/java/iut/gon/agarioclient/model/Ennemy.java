// Ennemy.java
package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.Map;

public class Ennemy extends Player {

    /**
     * Constructs a new Ennemy with the specified id, position, and mass.
     *
     * @param id       the unique identifier of the entity
     * @param position the position of the entity in the game space
     * @param mass     the mass of the entity
     */
    private IA strat;

    public Ennemy(String id, Point2D position, double mass, IA strat, double speed) {
        super(id, position, mass);
        this.strat = strat;
    }

    public void setStrat(IA strat) {
        this.strat = strat;
    }

    public void executeStrat(){
        strat.execute(this);
    }

    public Point2D calculateNewPosition(Point2D targetPosition, double mapWidth, double mapHeight) {
        double speed = calculateSpeed(targetPosition.getX(), targetPosition.getY(), mapWidth, mapHeight);
        setSpeed(speed);

        Point2D direction = targetPosition.subtract(getPosition()).normalize();
        Point2D newPosition = getPosition().add(direction.multiply(getSpeed()));

        // Check for collisions with the map boundaries
        double newX = Math.max(0, Math.min(newPosition.getX(), mapWidth));
        double newY = Math.max(0, Math.min(newPosition.getY(), mapHeight));
        return new Point2D(newX, newY);
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
