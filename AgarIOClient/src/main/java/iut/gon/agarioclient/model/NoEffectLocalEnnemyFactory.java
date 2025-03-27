package iut.gon.agarioclient.model;

import iut.gon.agarioclient.model.map.MapNode;
import javafx.geometry.Point2D;

import java.util.*;

public class NoEffectLocalEnnemyFactory implements LocalEnnemyFactory{

    private MapNode root;

    public NoEffectLocalEnnemyFactory(MapNode root) {
        this.root = root;
    }

    @Override
    public List<Ennemy> generate(int quantity) {
        List<Ennemy> ennemies = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < quantity; i++) {
            Point2D position = new Point2D(random.nextInt(786, 986), random.nextInt(375, 575));
            double mass = 10;
            double speed = (mass / Math.pow(mass, 1.44)) * 10;
            IA strategy;

            switch (i % 3) {
                case 0:
                    strategy = new IAStratRandomMoving(root);
                    break;
                case 1:
                    strategy = new IAStratEatPelletsOnly(root);
                    break;
                case 2:
                    strategy = new IAStratRandomMoving(root);
                    break;
                default:
                    strategy = new IAStratEatPelletsOnly(root);
            }

            Ennemy ennemy = new Ennemy(UUID.randomUUID().toString(), position, mass, strategy, speed);
            ennemies.add(ennemy);
        }

        return ennemies;
    }
}
