package iut.gon.agarioclient.model.entity.moveable;

import iut.gon.agarioclient.controller.GameController;
import iut.gon.agarioclient.model.entity.ia.IA;
import iut.gon.agarioclient.model.entity.ia.IAStratEatPelletsOnly;
import iut.gon.agarioclient.model.entity.ia.IAStratEatPlayers;
import iut.gon.agarioclient.model.entity.ia.IAStratRandomMoving;
import iut.gon.agarioclient.model.map.MapNode;
import javafx.geometry.Point2D;

import java.util.*;

public class NoEffectLocalEnnemyFactory implements LocalEnnemyFactory{

    private static int num = 0;
    private MapNode root;

    public NoEffectLocalEnnemyFactory(MapNode root) {
        this.root = root;
    }

    @Override
    public List<Ennemy> generate(int quantity) {
        List<Ennemy> ennemies = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < quantity; i++) {
            Point2D position = new Point2D(random.nextInt(1, GameController.X_MAX), random.nextInt(1, GameController.Y_MAX));
            double mass = 10;
            double speed = (mass / Math.pow(mass, 1.1)) * 10;
            IA strategy;

            switch (i % 3) {
                case 0:
                    strategy = new IAStratRandomMoving(root);
                    break;
                case 1:
                    strategy = new IAStratEatPlayers(root);
                    break;
                case 2:
                    strategy = new IAStratEatPelletsOnly(root);
                    break;
                default:
                    strategy = new IAStratEatPelletsOnly(root);
            }

            num++;
            Ennemy ennemy = new Ennemy("BOT - "+num, position, mass, strategy, speed);
            ennemies.add(ennemy);
        }

        return ennemies;
    }
}
