package iut.gon.agarioclient.model;

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
            checkCollisionAndConsume(e, nearestPlayer);
        }
    }

    private Player findNearestPlayer(Player player){
        Set<Entity> entities = root.getEntitySet();

        Player nearestPlayer = null;
        double minDistance = Double.MAX_VALUE;

        for (Entity entity : entities) {
            if (entity instanceof Player ) {
                double distance = player.getPosition().distance(entity.getPosition());
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
        System.out.println(ennemy.getSpeed());
        Point2D newPosition = ennemy.getPosition().add(direction.multiply(10));
        ennemy.setPosition(newPosition);
    }

    private void checkCollisionAndConsume(Ennemy ennemy, Player target) {
        double distance = ennemy.getPosition().distance(target.getPosition());
        if (distance <= ennemy.calculateRadius()) {
            ennemy.setMass(ennemy.getMass() + target.getMass());
            target.removeFromCurrentNode();
        }
    }

}
