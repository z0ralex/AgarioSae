package iut.gon.agarioclient.model.map;

import java.util.HashSet;
import java.util.Set;
import iut.gon.agarioclient.model.Entity;
import javafx.geometry.Point2D;

/**
 * Morceau concret de la map, le seul qui stock des entités
 */
public class MapChunk extends MapElt{
    private final Set<iut.gon.agarioclient.model.Entity> entitySet;

    private Point2D beginningPoint;
    private Point2D endPoint;


    public MapChunk(MapQuadTree parent, Point2D beginningPoint, Point2D endPoint){
        super(parent, true);
        this.entitySet = new HashSet<Entity>();
        this.beginningPoint = beginningPoint;
        this.endPoint = endPoint;
    }

    public MapChunk(MapQuadTree parent, Set<Entity> entitySet, Point2D beginningPoint, Point2D endPoint) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MapChunk mapChunk = (MapChunk) o;
        return entitySet.equals(mapChunk.entitySet) && beginningPoint.equals(mapChunk.beginningPoint) && endPoint.equals(mapChunk.endPoint);
    }


    /**
     *
     * @return tableau contenant les chunks autour de ce chunk
     * indices du tableau :
     * 0 - Nord
     * 1 - Ouest
     * 2 - Sud
     * 3 - Est
     */

}
