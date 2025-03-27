// EffectPellet.java
package iut.gon.agarioclient.model;

import javafx.geometry.Point2D;

public abstract class EffectPellet extends Pellet {
    public EffectPellet(String id, Point2D position, double mass) {
        super(id, position, mass);
    }

    public abstract void applyEffect(Player player);

}