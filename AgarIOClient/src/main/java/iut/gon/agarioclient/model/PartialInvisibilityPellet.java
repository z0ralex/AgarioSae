// PartialInvisibilityPellet.java
package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

public class PartialInvisibilityPellet extends EffectPellet {
    public PartialInvisibilityPellet(String id, Point2D position, double mass) {
        super(id, position, mass);
    }

    @Override
    public void applyEffect(Player player) {
        player.setInvisible(true);
    }
}