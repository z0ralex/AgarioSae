// EffectPelletFactory.java
package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EffectPelletFactory implements PelletFactory {
    @Override
    public List<Pellet> generatePellets(int quantity) {
        List<Pellet> list = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < quantity; i++) {
            Point2D p = new Point2D(r.nextDouble() * 1600, r.nextDouble() * 1200); // Use pane dimensions
            int effectType = r.nextInt(4); // Assuming 4 types of effect pellets
            switch (effectType) {
                case 0:
                    list.add(new SpeedBoostPellet(UUID.randomUUID().toString(), p, 1));
                    break;
                case 1:
                    list.add(new SpeedReductionPellet(UUID.randomUUID().toString(), p, 1));
                    break;
                case 2:
                    list.add(new PartialInvisibilityPellet(UUID.randomUUID().toString(), p, 1));
                    break;
            }
        }
        return list;
    }
}