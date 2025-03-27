// SpeedBoostPellet.java
package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

public class SpeedBoostPellet extends EffectPellet {


    public SpeedBoostPellet(String id, Point2D position, double mass) {
        super(id, position, mass);
    }


    @Override
    public void applyEffect(Player player) {
        if (player.getGotEffectedAt() + 2000 > System.currentTimeMillis()) {
            player.setSpecialEffect(2);
        } else {
            player.setSpecialEffect(1);
        }

    }
}