package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class NoEffectLocalEnnemyFactory implements LocalEnnemyFactory{


    @Override
    public List<Ennemy> generate(int quantity) {
        List<Ennemy> list = new ArrayList<>();
        for(int i = 0; i < quantity; i++){//Strat Random. ATTENION Remettre Quantity / 3
            Random r = new Random();
            Point2D p = new Point2D(r.nextInt(786, 986), r.nextInt(375, 575));
            list.add(new Ennemy(UUID.randomUUID().toString(), p, 15,new IAStratRandomMoving(),5.0));
        }
        /*
        for(int i = 0; i < quantity / 3; i++){//Strat Pellets
            Random r = new Random();
            Point2D p = new Point2D(r.nextInt(80), r.nextInt(80));
            list.add(new Ennemy(UUID.randomUUID().toString(), p, 15,new IAStratEatPelletsOnly(),5.0));
        }

        for(int i = 0; i < quantity / 3; i++){//Strat Players
            Random r = new Random();
            Point2D p = new Point2D(r.nextInt(80), r.nextInt(80));
            list.add(new Ennemy(UUID.randomUUID().toString(), p, 15, new IAStratEatPlayers(),5.0));
        }*/
        return list;
    }
}
