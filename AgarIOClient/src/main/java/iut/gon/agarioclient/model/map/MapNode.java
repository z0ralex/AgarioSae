package iut.gon.agarioclient.model.map;

import iut.gon.agarioclient.model.Entity;
import javafx.geometry.Point2D;

import java.util.HashSet;
import java.util.Set;

public class MapNode {
    private MapNode NEnode;
    private MapNode NWnode;
    private MapNode SEnode;
    private MapNode SWnode;
    private MapNode parent;
    private Direction direction;
    private Set<Entity> entitySet;
    private Point2D beginningPoint;
    private Point2D endPoint;


    //CONSTRUCTEURS

    public MapNode(MapNode NEnode, MapNode NWnode, MapNode SEnode, MapNode SWnode, MapNode parent, Direction direction) {
        this.NEnode = NEnode;
        this.NWnode = NWnode;
        this.SEnode = SEnode;
        this.SWnode = SWnode;
        this.parent = parent;
        this.direction = direction;
        this.entitySet = null;
        this.beginningPoint = null;
        this.endPoint = null;
    }


    public MapNode(MapNode parent, Direction direction, Set<Entity> entitySet, Point2D beginningPoint, Point2D endPoint){
        this.NEnode = null;
        this.NWnode = null;
        this.SEnode = null;
        this.SWnode = null;
        this.parent = parent;
        this.direction = direction;
        this.entitySet = entitySet;
        this.beginningPoint = beginningPoint;
        this.endPoint = endPoint;
    }

    public MapNode(){
        this.NEnode = null;
        this.NWnode = null;
        this.SEnode = null;
        this.SWnode = null;
        this.parent = null;
        this.direction = null;
        this.entitySet = null;
        this.beginningPoint = null;
        this.endPoint = null;
    }

    //Gestion d'entité
    public void addEntity(Entity e){
        if(entitySet == null){
            if(!isLeaf()){
                throw new IllegalStateException("on ne peut ajouter d'entité qu'à une feuille");
            }
        }

        entitySet.add(e);
    }


    //SETTERS
    public void setNEnode(MapNode NEnode) {
        this.NEnode = NEnode;
        NEnode.setDirection(Direction.NORTH_EAST);
    }

    public void setNWnode(MapNode NWnode) {
        this.NWnode = NWnode;
        NWnode.setDirection(Direction.NORTH_WEST);
    }

    public void setSEnode(MapNode SEnode) {
        this.SEnode = SEnode;
        SEnode.setDirection(Direction.SOUTH_EAST);
    }

    public void setSWnode(MapNode SWnode) {
        this.SWnode = SWnode;
        SWnode.setDirection(Direction.SOUTH_WEST);
    }

    public void setParent(MapNode parent) {
        this.parent = parent;
    }
    public void setDirection(Direction direction) {
        this.direction = direction;
    }


    //GETTERS

    public boolean isLeaf(){
        return  NEnode == null &&
                NWnode == null &&
                SEnode == null &&
                SWnode == null;
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

    public MapNode getNEnode() {
        return NEnode;
    }

    public MapNode getNWnode() {
        return NWnode;
    }

    public MapNode getSEnode() {
        return SEnode;
    }

    public MapNode getSWnode() {
        return SWnode;
    }

    public MapNode getParent() {
        return parent;
    }

    public Direction getDirection() {
        return direction;
    }


    //GETTERS des node dans une direction

    public MapNode getNorthElt(){
        if(parent == null) return null;

        MapNode parentNorth = parent.getNorthElt();

        switch(direction){
            case NORTH_EAST:
                if(parentNorth == null){
                    return null;
                }

                return parentNorth.getSEnode();


            case NORTH_WEST:
                if(parentNorth == null){
                    return null;
                }

                return parentNorth.getSWnode();


            case SOUTH_EAST:
                return parent.getNEnode();

            case SOUTH_WEST:
                return parent.getNWnode();

            default:
                if (direction == null) return null;

                throw new IllegalStateException("direction inconnue (nouvelle direction ajoutée à l'enum ?). Direction = " + direction.toString());
        }
    }

    public MapNode getSouthElt(){
        if(parent == null) return null; //racine

        MapNode parentSouth = parent.getSouthElt();

        switch(direction){
            case NORTH_EAST:
                return parent.getSEnode();


            case NORTH_WEST:
                return parent.getSWnode();


            case SOUTH_EAST:
                if(parentSouth == null){ //fin de la map
                    return null;
                }

                return parentSouth.getNEnode();

            case SOUTH_WEST:
                if(parentSouth == null){ //fin de la map
                    return null;
                }

                return parentSouth.getNWnode();

            default:

                throw new IllegalStateException("direction inconnue (nouvelle direction ajoutée à l'enum ?). Direction = " + direction.toString());
        }
    }

    public MapNode getEastElt(){
        if(parent == null) return null; //racine

        MapNode parentEast = parent.getEastElt();

        switch(direction){
            case NORTH_EAST:

                if(parentEast == null){ //fin de la map
                    return null;
                }

                return parentEast.getNWnode();


            case NORTH_WEST:
                return parent.getNEnode();


            case SOUTH_EAST:

                if(parentEast == null){ //fin de la map
                    return null;
                }

                return parentEast.getSWnode();


            case SOUTH_WEST:
                return parent.getSEnode();

            default:
                throw new IllegalStateException("direction inconnue (nouvelle direction ajoutée à l'enum ?). Direction = " + direction.toString());
        }
    }

    public MapNode getWestElt(){
        if(parent == null) return null; //racine

        MapNode parentWest = parent.getWestElt();

        switch(direction){
            case NORTH_EAST:
                return parent.getNWnode();


            case NORTH_WEST:
                if(parentWest == null){ //fin de la map
                    return null;
                }

                return  parentWest.getNEnode();


            case SOUTH_EAST:
                return parent.getSWnode();


            case SOUTH_WEST:
                if(parentWest == null){ //fin de la map
                    return null;
                }

                return parentWest.getSEnode();

            default:
                throw new IllegalStateException("direction inconnue (nouvelle direction ajoutée à l'enum ?). Direction = " + direction.toString());
        }
    }
}
