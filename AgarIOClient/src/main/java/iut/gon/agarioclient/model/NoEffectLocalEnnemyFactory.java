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
        List<Ennemy> list = new ArrayList<>();
        for(int i = 0; i < quantity / 3; i++){//Strat Random
            Random r = new Random();
            Point2D p = new Point2D(r.nextInt(786, 986), r.nextInt(375, 575));
            list.add(new Ennemy(UUID.randomUUID().toString(), p, 15,new IAStratRandomMoving(root),5.0));
        }

        for (int i = 0; i < quantity / 3; i++) { // Strat Pellets
            Random r = new Random();

            Point2D p = new Point2D(r.nextInt(786, 986), r.nextInt(375, 575));
            list.add(new Ennemy(UUID.randomUUID().toString(), p, 15, new IAStratEatPelletsOnly(root), 5.0));
        }

        for (int i = 0; i < quantity / 3; i++) { // Strat Players
            Random r = new Random();

            Point2D p = new Point2D(r.nextInt(786, 986), r.nextInt(375, 575));
            list.add(new Ennemy(UUID.randomUUID().toString(), p, 15, new IAStratEatPlayers(root), 5.0));
        }

        return list;
    }
}
