// EffectPellet.java
package iut.gon.agarioclient.model.entity.pellet;

import iut.gon.agarioclient.model.entity.moveable.Player;
import javafx.geometry.Point2D;

public abstract class EffectPellet extends Pellet {
    public EffectPellet(String id, Point2D position, double mass) {
        super(id, position, mass);
    }



}