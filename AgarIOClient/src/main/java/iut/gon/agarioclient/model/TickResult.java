package iut.gon.agarioclient.model;

import iut.gon.agarioclient.model.entity.moveable.Entity;

import java.util.HashMap;
import java.util.Set;

public class TickResult {
    private Set<Entity> allEntities;
    private HashMap<Entity, Set<Entity>> eatenEntities;

    public TickResult(Set<Entity> allEntities, HashMap<Entity, Set<Entity>> eatenEntities) {
        this.allEntities = allEntities;
        this.eatenEntities = eatenEntities;
    }


}
