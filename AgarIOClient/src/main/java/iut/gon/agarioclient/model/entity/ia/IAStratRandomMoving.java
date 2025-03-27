package iut.gon.agarioclient.model.entity.ia;

import iut.gon.agarioclient.controller.GameController;
import iut.gon.agarioclient.model.entity.moveable.Ennemy;
import iut.gon.agarioclient.model.map.MapNode;
import javafx.geometry.Point2D;

import java.util.Random;

public class IAStratRandomMoving implements IA{

    private long tookDecisionAt = System.currentTimeMillis();
    private int dirSelector = 4;
    private MapNode root;

    public IAStratRandomMoving(MapNode root){ this.root = root;}

    public void execute(Ennemy e) {
        Random r = new Random();

        if (this.tookDecisionAt + 1000 < System.currentTimeMillis()) {
            this.tookDecisionAt = System.currentTimeMillis();
            dirSelector = r.nextInt(1, 9);
        }

        Point2D newPosition = e.getPosition();
        switch (dirSelector) {
            case 1: // N
                newPosition = new Point2D(e.getPosition().getX(), e.getPosition().getY() + 2);
                break;
            case 2: // S
                newPosition = new Point2D(e.getPosition().getX(), e.getPosition().getY() - 2);
                break;
            case 3: // W
                newPosition = new Point2D(e.getPosition().getX() - 2, e.getPosition().getY());
                break;
            case 4: // E
                newPosition = new Point2D(e.getPosition().getX() + 2, e.getPosition().getY());
                break;
            case 5: // NW
                newPosition = new Point2D(e.getPosition().getX() - 2, e.getPosition().getY() + 2);
                break;
            case 6: // NE
                newPosition = new Point2D(e.getPosition().getX() + 2, e.getPosition().getY() + 2);
                break;
            case 7: // SE
                newPosition = new Point2D(e.getPosition().getX() + 2, e.getPosition().getY() - 2);
                break;
            case 8: // SW
                newPosition = new Point2D(e.getPosition().getX() - 2, e.getPosition().getY() - 2);
                break;
        }

        // Check for collisions with the map boundaries
        double newX = Math.max(0, Math.min(newPosition.getX(), GameController.X_MAX));
        double newY = Math.max(0, Math.min(newPosition.getY(), GameController.Y_MAX));
        newPosition = new Point2D(newX, newY);

        e.setPosition(newPosition);
        e.calculateRadius();
    }
}
