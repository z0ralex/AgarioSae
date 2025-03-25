package iut.gon.agarioclient.model.map;

import java.util.Objects;

/**
 * element de l'abre de la map.
 * Possède un parent, et une proprriété isChunck
 */
public class MapElt {
    private MapQuadTree parent;

    private final boolean isChunck;

    private Direction direction;

    /**
     *
     * @param parent map.MapElt representant le parent de l'element
     */
    public MapElt(MapQuadTree parent, boolean isChunck){
        this.parent = parent;
        this.isChunck = isChunck;
        this.direction = null;
    }

    /**
     *
     * @return map.MapElt le parent de l'élement
     */
    public MapQuadTree getParent() {
        return parent;
    }
    public boolean isChunck(){
        return this.isChunck;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setParent(MapQuadTree parent) {
        this.parent = parent;
    }


    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapElt mapElt = (MapElt) o;
        return isChunck == mapElt.isChunck && Objects.equals(parent, mapElt.parent) && direction == mapElt.direction;
    }

    public MapElt getNorthElt(){
        if(parent == null) return null;

        MapElt parentNorth = parent.getNorthElt();

        switch(direction){
            case NORTH_EAST:
                if(parentNorth == null){
                    return null;
                }

                return ((MapQuadTree) parentNorth).getSEnode();


            case NORTH_WEST:
                if(parentNorth == null){
                    return null;
                }

                return ((MapQuadTree) parentNorth).getSWnode();


            case SOUTH_EAST:
                return parent.getNEnode();

            case SOUTH_WEST:
                return parent.getNWnode();

            default:
                if (direction == null) return null;

                throw new IllegalStateException("direction inconnue (nouvelle direction ajoutée à l'enum ?). Direction = " + direction.toString());
        }
    }

    public MapElt getSouthElt(){
        if(parent == null) return null; //racine

        MapElt parentSouth = parent.getSouthElt();

        switch(direction){
            case NORTH_EAST:
                return parent.getSEnode();


            case NORTH_WEST:
                return parent.getSWnode();


            case SOUTH_EAST:
                if(parentSouth == null){ //fin de la map
                    return null;
                }

                return ((MapQuadTree) parentSouth).getNEnode();

            case SOUTH_WEST:
                if(parentSouth == null){ //fin de la map
                    return null;
                }

                return ((MapQuadTree) parentSouth).getNWnode();

            default:

                throw new IllegalStateException("direction inconnue (nouvelle direction ajoutée à l'enum ?). Direction = " + direction.toString());
        }
    }

    public MapElt getEastElt(){
        if(parent == null) return null; //racine

        MapElt parentEast = parent.getEastElt();

        switch(direction){
            case NORTH_EAST:

                if(parentEast == null){ //fin de la map
                    return null;
                }

                return ((MapQuadTree) parentEast).getNWnode();


            case NORTH_WEST:
                return parent.getNEnode();


            case SOUTH_EAST:

                if(parentEast == null){ //fin de la map
                    return null;
                }

                return ((MapQuadTree) parentEast).getSWnode();


            case SOUTH_WEST:
                return parent.getSEnode();

            default:
                throw new IllegalStateException("direction inconnue (nouvelle direction ajoutée à l'enum ?). Direction = " + direction.toString());
        }
    }

    public MapElt getWestElt(){
        if(parent == null) return null; //racine

        MapElt parentWest = parent.getWestElt();

        switch(direction){
            case NORTH_EAST:
                return parent.getNWnode();


            case NORTH_WEST:
                if(parentWest == null){ //fin de la map
                    return null;
                }

                return ((MapQuadTree) parentWest).getNEnode();


            case SOUTH_EAST:
                return parent.getSWnode();


            case SOUTH_WEST:
                if(parentWest == null){ //fin de la map
                    return null;
                }

                return ((MapQuadTree) parentWest).getSEnode();

            default:
                throw new IllegalStateException("direction inconnue (nouvelle direction ajoutée à l'enum ?). Direction = " + direction.toString());
        }
    }
}
