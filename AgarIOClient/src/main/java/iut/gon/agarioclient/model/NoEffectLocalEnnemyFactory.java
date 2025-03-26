package iut.gon.agarioclient.model;

import iut.gon.agarioclient.model.map.MapNode;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class NoEffectLocalEnnemyFactory implements LocalEnnemyFactory {

    private MapNode root;

    public NoEffectLocalEnnemyFactory(MapNode root) {
        this.root = root;
    }

    @Override
    public List<Ennemy> generate(int quantity) {
        List<Ennemy> list = new ArrayList<>();
        Random r = new Random();

        for (int i = 0; i < quantity / 3; i++) { // Strat Random
            Point2D p = new Point2D(r.nextInt(80), r.nextInt(80));
            list.add(new Ennemy(UUID.randomUUID().toString(), p, 15, new IAStratRandomMoving(), 5.0));
        }

        for (int i = 0; i < quantity / 3; i++) { // Strat Pellets
            Point2D p = new Point2D(r.nextInt(80), r.nextInt(80));
            list.add(new Ennemy(UUID.randomUUID().toString(), p, 15, new IAStratEatPelletsOnly(root), 5.0));
        }

        for (int i = 0; i < quantity / 3; i++) { // Strat Players
            Point2D p = new Point2D(r.nextInt(80), r.nextInt(80));
            list.add(new Ennemy(UUID.randomUUID().toString(), p, 15, new IAStratEatPlayers(), 5.0));
        }

        return list;
    }
}