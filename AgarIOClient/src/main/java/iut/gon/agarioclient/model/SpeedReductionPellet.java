// SpeedReductionPellet.java
package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

public class SpeedReductionPellet extends EffectPellet {


    public SpeedReductionPellet(String id, Point2D position, double mass) {
        super(id, position, mass);
    }

    @Override
    public void applyEffect(Player player) {
        if (player.getGotEffectedAt() + 2000 > System.currentTimeMillis()) {
            player.setSpecialEffect(0.5);
        } else {
            player.setSpecialEffect(1);
        }

    }
}