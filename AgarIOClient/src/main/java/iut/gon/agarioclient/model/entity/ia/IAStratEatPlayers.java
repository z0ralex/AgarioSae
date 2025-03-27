package iut.gon.agarioclient.model.entity.ia;

import iut.gon.agarioclient.model.entity.moveable.Ennemy;
import iut.gon.agarioclient.model.entity.moveable.Entity;
import iut.gon.agarioclient.model.entity.moveable.Player;
import iut.gon.agarioclient.model.map.MapNode;
import javafx.geometry.Point2D;

import java.util.Set;

public class IAStratEatPlayers implements IA{

    private MapNode root;

    public IAStratEatPlayers(MapNode root){ this.root = root;}

    @Override
    public void execute(Ennemy e) {
        Player nearestPlayer = findNearestPlayer(e);
        if (nearestPlayer != null) {
            moveToPlayer(e, nearestPlayer);
        }
    }

    private Player findNearestPlayer(Ennemy ennemy) {
        Set<Entity> entities = root.getEntitySet();
        Player nearestPlayer = null;
        double minDistance = Double.MAX_VALUE;

        for (Entity entity : entities) {
            if (entity instanceof Player && !entity.getId().equals(ennemy.getId())) {
                double distance = ennemy.getPosition().distance(entity.getPosition());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestPlayer = (Player) entity;
                }
            }
        }
        return nearestPlayer;
    }

    private void moveToPlayer(Ennemy ennemy, Player target) {
        Point2D direction = target.getPosition().subtract(ennemy.getPosition()).normalize();
        Point2D newPosition = ennemy.getPosition().add(direction.multiply(ennemy.getSpeed()));
        ennemy.setPosition(newPosition);
    }
}
