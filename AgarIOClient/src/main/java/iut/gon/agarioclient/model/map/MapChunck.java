package iut.gon.agarioclient.model.map;

import java.util.HashSet;
import java.util.Set;
import iut.gon.agarioclient.model.Entity;
import javafx.geometry.Point2D;

/**
 * Morceau concret de la map, le seul qui stock des entités
 */
public class MapChunck extends MapElt{
    private final Set<iut.gon.agarioclient.model.Entity> entitySet;

    private Point2D beginningPoint;
    private Point2D endPoint;


    public MapChunck(MapQuadTree parent, Point2D beginningPoint, Point2D endPoint){
        super(parent, true);
        this.entitySet = new HashSet<Entity>();
        this.beginningPoint = beginningPoint;
        this.endPoint = endPoint;
    }

    public MapChunck(MapQuadTree parent, Set<Entity> entitySet, Point2D beginningPoint, Point2D endPoint) {
        super(parent, true);
        this.entitySet = entitySet;

        this.beginningPoint = beginningPoint;
        this.endPoint = endPoint;
    }

    public Point2D getBeginningPoint() {
        return beginningPoint;
    }

    public Point2D getEndPoint() {
        return endPoint;
    }

    public Set<Entity> getEntitySet(){
        return entitySet;
    }
}
