package iut.gon.agarioclient.model.entity.ia;

import iut.gon.agarioclient.model.entity.moveable.Ennemy;

import java.io.Serializable;

public interface IA extends Serializable {
    void execute(Ennemy e);
}
