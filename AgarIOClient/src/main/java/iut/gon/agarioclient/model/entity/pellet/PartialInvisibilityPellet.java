// PartialInvisibilityPellet.java
package iut.gon.agarioclient.model.entity.pellet;

import iut.gon.agarioclient.model.entity.moveable.Player;
import iut.gon.agarioclient.model.entity.moveable.Point2DSerial;
import iut.gon.agarioclient.model.entity.pellet.EffectPellet;
import javafx.geometry.Point2D;

public class PartialInvisibilityPellet extends EffectPellet {
    public PartialInvisibilityPellet(String id, Point2DSerial position, double mass) {
        super(id, position, mass);
    }


}