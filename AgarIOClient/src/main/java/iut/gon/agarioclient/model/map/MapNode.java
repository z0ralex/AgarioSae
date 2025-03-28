// MapNode.java
package iut.gon.agarioclient.model.map;

import iut.gon.agarioclient.model.entity.moveable.Entity;
import iut.gon.agarioclient.model.entity.moveable.Point2DSerial;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;
import java.util.*;

public class MapNode implements Serializable {
    private MapNode NEnode;
    private MapNode NWnode;
    private MapNode SEnode;
    private MapNode SWnode;
    private MapNode parent;
    private Direction direction;
    private Set<Entity> entitySet;
    private final Point2DSerial beginningPoint;
    private final Point2DSerial endPoint;

    // CONSTRUCTEURS

    public MapNode(MapNode parent, Direction direction, Set<Entity> entitySet, Point2DSerial beginningPoint, Point2DSerial endPoint) {
        this.NEnode = null;
        this.NWnode = null;
        this.SEnode = null;
        this.SWnode = null;
        this.parent = parent;
        this.direction = direction;
        this.entitySet = entitySet != null ? entitySet : new HashSet<>(); //ajout
        this.beginningPoint = beginningPoint;
        this.endPoint = endPoint;
    }

    /**
     * @param level niveau de l'arbre (0 = feuille)
     */
    public MapNode(int level, Point2DSerial beginningPoint, Point2DSerial endPoint, MapNode parent) {
        this.beginningPoint = beginningPoint;
        this.endPoint = endPoint;
        this.parent = parent;
        this.entitySet = new HashSet<>(); //ajout

        if(beginningPoint.getX() > endPoint.getX() || beginningPoint.getY() > endPoint.getY()){
            throw new IllegalArgumentException("beginningPoint doit avoir des coordonnées inférieures à endpoint (x ET y)");
        }

        if(level > 0) {
            Point2DSerial middle = beginningPoint.midpoint(endPoint);

            this.setNEnode(new MapNode(level - 1,
                    new Point2DSerial(middle.getX(), beginningPoint.getY()),
                    new Point2DSerial(endPoint.getX(), middle.getY()), this));

            this.setNWnode(new MapNode(level - 1, beginningPoint, middle, this));

            this.setSEnode(new MapNode(level - 1, middle, endPoint, this));

            this.setSWnode(new MapNode(level - 1,
                    new Point2DSerial(beginningPoint.getX(), middle.getY()),
                    new Point2DSerial(middle.getX(), endPoint.getY()), this));
        }
    }

    public MapNode(int level, Point2DSerial beginningPoint, Point2DSerial endPoint) {
        this(level, beginningPoint, endPoint, null);
    }


    //Gestion d'entité

    /**
     * Ajoute l'entité à la map
     * @param e
     */
    public void addEntity(Entity e) {
        long x = Math.round(e.getPosition().getX());
        long y = Math.round(e.getPosition().getY());

        if (!positionInNode(x, y)) {
            throw new IllegalArgumentException("L'entité n'est pas dans cette node : \nCoordonnées de l'entité "
                    + x + " ; " + y + "\nCoordonnées du beginPoint : " + beginningPoint.getX() + " ; " + beginningPoint.getY() +
                    "\nCoordonnées du endPoint : " + endPoint.getX() + " ; " + endPoint.getY() + "\nparent ? " + (parent != null));
        }
        if (isLeaf()) {
            addEntityToSet(e);

        } else {
            boolean isSouth = y > (endPoint.getY() + beginningPoint.getY())/2;

            if(x > (endPoint.getX() + beginningPoint.getX())/2){
                //East

                if(isSouth){
                    getSEnode().addEntity(e);
                } else {
                    getNEnode().addEntity(e);
                }

            } else {
                // West
                if (isSouth) {
                    getSWnode().addEntity(e);
                } else {
                    getNWnode().addEntity(e);
                }
            }
        }
    }

    public boolean positionInNode(double x, double y){
        return (x <= endPoint.getX() && x >= beginningPoint.getX()) &&
                (y <= endPoint.getY() && y >= beginningPoint.getY());
    }

    private void addEntityToSet(Entity e) {
        if (entitySet == null) {
            entitySet = new HashSet<>();
        }

        entitySet.add(e);
        e.setCurrentMapNode(this);
    }


    // SETTERS
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


    // GETTERS
    public boolean isLeaf() {
        return NEnode == null &&
                NWnode == null &&
                SEnode == null &&
                SWnode == null;
    }

    public Point2DSerial getBeginningPoint() {
        return beginningPoint;
    }

    public Point2DSerial getEndPoint() {
        return endPoint;
    }

    public Set<Entity> getEntitySet() {
        if(isLeaf()){
            return entitySet;
        }

        Set<Entity> entities = new HashSet<>();

        if(NEnode != null) entities.addAll(NEnode.getEntitySet());
        if(NWnode != null) entities.addAll(NWnode.getEntitySet());
        if(SEnode != null) entities.addAll(SEnode.getEntitySet());
        if(SWnode != null) entities.addAll(SWnode.getEntitySet());

        return entities;
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


    // GETTERS des node dans une direction

    public MapNode getNorthElt() {
        if (parent == null) return null;

        MapNode parentNorth = parent.getNorthElt();

        switch (direction) {
            case NORTH_EAST -> {
                if (parentNorth == null) {
                    return null;
                }
                return parentNorth.getSEnode();
            }
            case NORTH_WEST -> {
                if (parentNorth == null) {
                    return null;
                }
                return parentNorth.getSWnode();
            }
            case SOUTH_EAST -> {
                return parent.getNEnode();
            }
            case SOUTH_WEST -> {
                return parent.getNWnode();
            }
            default -> {
                throw new IllegalStateException("direction inconnue (nouvelle direction ajoutée à l'enum ?). Direction = " + direction.toString());
            }
        }
    }

    public MapNode getSouthElt() {
        if (parent == null) return null; // racine

        MapNode parentSouth = parent.getSouthElt();

        switch (direction) {
            case NORTH_EAST -> {
                return parent.getSEnode();
            }
            case NORTH_WEST -> {
                return parent.getSWnode();
            }
            case SOUTH_EAST -> {
                if (parentSouth == null) { // fin de la map
                    return null;
                }
                return parentSouth.getNEnode();
            }
            case SOUTH_WEST -> {
                if (parentSouth == null) { // fin de la map
                    return null;
                }
                return parentSouth.getNWnode();
            }
            default ->
                    throw new IllegalStateException("direction inconnue (nouvelle direction ajoutée à l'enum ?). Direction = " + direction.toString());
        }
    }

    public MapNode getEastElt() {
        if (parent == null) return null; // racine

        MapNode parentEast = parent.getEastElt();

        switch (direction) {
            case NORTH_EAST -> {
                if (parentEast == null) { // fin de la map
                    return null;
                }
                return parentEast.getNWnode();
            }
            case NORTH_WEST -> {
                return parent.getNEnode();
            }
            case SOUTH_EAST -> {
                if (parentEast == null) { // fin de la map
                    return null;
                }
                return parentEast.getSWnode();
            }
            case SOUTH_WEST -> {
                return parent.getSEnode();
            }
            default ->
                    throw new IllegalStateException("direction inconnue (nouvelle direction ajoutée à l'enum ?). Direction = " + direction.toString());
        }
    }

    public MapNode getWestElt() {
        if (parent == null) return null; // racine

        MapNode parentWest = parent.getWestElt();

        switch (direction) {
            case NORTH_EAST -> {
                return parent.getNWnode();
            }
            case NORTH_WEST -> {
                if (parentWest == null) { // fin de la map
                    return null;
                }
                return parentWest.getNEnode();
            }
            case SOUTH_EAST -> {
                return parent.getSWnode();
            }
            case SOUTH_WEST -> {
                if (parentWest == null) { // fin de la map
                    return null;
                }
                return parentWest.getSEnode();
            }
            default ->
                    throw new IllegalStateException("direction inconnue (nouvelle direction ajoutée à l'enum ?). Direction = " + direction.toString());
        }
    }

    public void drawBorders(Pane pane) {
        if (isLeaf()) {
            Rectangle border = new Rectangle(
                    beginningPoint.getX(),
                    beginningPoint.getY(),
                    endPoint.getX() - beginningPoint.getX(),
                    endPoint.getY() - beginningPoint.getY()
            );
            border.setStroke(Color.GRAY);
            border.setFill(Color.TRANSPARENT);
            pane.getChildren().add(border);
        } else {
            if (NEnode != null) NEnode.drawBorders(pane);
            if (NWnode != null) NWnode.drawBorders(pane);
            if (SEnode != null) SEnode.drawBorders(pane);
            if (SWnode != null) SWnode.drawBorders(pane);
        }
    }

    /**
     * Renvoie les nodes autour de cette node
     * @param radius int le rayon en terme de chunk (plus petite node possible)
     * @param set Set<MapNode> le set en cours de récupération, sert pour éviter de passer 2x par la même node
     * @return Set<MapNode> un set contenant les nodes comprises dans le rayon
     */
    private Set<MapNode> getSurroundingNodes(int radius, Set<MapNode> set){
        //System.out.println("appel recursif : set.size() = " + set.size());

        //if(isLeaf()) System.out.println("feuille");

        if(set.contains(this)) return set; // on est déjà passé par là

        set.add(this);

        if(radius == 0){
            //System.out.println("ARRET");
            //condition d'arrêt
            return set;
        }

        ArrayList<MapNode> listSurroundingNodes = new ArrayList<>(Arrays.asList(
                getNorthElt(),
                getSouthElt(),
                getEastElt(),
                getWestElt()
        ));

        for (MapNode node: listSurroundingNodes) {
            if(node != null){ //évite des problèmes avec les limites de la carte
                //System.out.println("boucle");
                Set<MapNode> newSet = node.getSurroundingNodes(radius - 1, set);
                set.addAll(newSet);
            } /*else {
                System.out.println("node null");
            }*/
        }

        return set;
    }

    /**
     * Renvoie les nodes autour de cette node
     * @param radius int le rayon en terme de chunk (plus petite node possible)
     * @return Set<MapNode> un set contenant les nodes comprises dans le rayon
     */
    public Set<MapNode> getSurroundingNodes(int radius){
        return getSurroundingNodes(radius, new HashSet<MapNode>());
    }


}