// SpeedReductionPellet.java
package iut.gon.agarioclient.model.entity.pellet;

import iut.gon.agarioclient.model.entity.moveable.Player;
import iut.gon.agarioclient.model.entity.pellet.EffectPellet;
import javafx.geometry.Point2D;

public class SpeedReductionPellet extends EffectPellet {


    public SpeedReductionPellet(String id, Point2D position, double mass) {
        super(id, position, mass);
    }
}