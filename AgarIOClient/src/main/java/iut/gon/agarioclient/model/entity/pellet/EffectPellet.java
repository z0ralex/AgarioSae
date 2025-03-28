// EffectPellet.java
package iut.gon.agarioclient.model.entity.pellet;

import iut.gon.agarioclient.model.entity.moveable.Player;
import iut.gon.agarioclient.model.entity.moveable.Point2DSerial;
import javafx.geometry.Point2D;

public abstract class EffectPellet extends Pellet {
    public EffectPellet(String id, Point2DSerial position, double mass) {
        super(id, position, mass);
    }



}