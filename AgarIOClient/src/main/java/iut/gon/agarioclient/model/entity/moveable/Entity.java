// Entity.java
package iut.gon.agarioclient.model.entity.moveable;

import iut.gon.agarioclient.model.Game;
import iut.gon.agarioclient.model.map.MapNode;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;

import java.io.Serializable;
import java.util.Objects;

/**
 * The Entity class represents a basic game entity with a unique identifier, position, and mass.
 * This class serves as a base class for other specific entities like players and pellets.
 */
public class Entity implements Serializable {
    private final String id;

    private Point2DSerial position;
    private double mass;

    private MapNode currentMapNode;

    /**
     * Constructs a new Entity with the specified id, position, and mass.
     *
     * @param id       the unique identifier of the entity
     * @param position the position of the entity in the game space
     * @param mass     the mass of the entity
     */
    public Entity(String id, Point2DSerial position, double mass) {
        this.id = id;
        this.position = position;
        this.mass = mass;
        this.currentMapNode = null;
    }

    /**
     * Returns the unique identifier of the entity.
     *
     * @return the unique identifier of the entity
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the position of the entity in the game space.
     *
     * @return the position of the entity
     */
    public Point2DSerial getPosition() {
        return position;
    }

    /**
     * Sets the position of the entity in the game space.
     *
     * @param position the new position of the entity
     */
    public void setPosition(Point2DSerial position) {
        if(Game.isValidPosition(position)){
            this.position = new Point2DSerial(position.getX(), position.getY());
        }
    }

    public Point2DSerial positionProperty(){
        return position;
    }

    /**
     * Returns the mass of the entity.
     *
     * @return the mass of the entity
     */
    public double getMass() {
        return mass;
    }

    public double massProperty(){
        return mass;
    }


    /**
     * Sets the mass of the entity.
     *
     * @param mass the new mass of the entity
     */
    public void setMass(double mass) {
        this.mass = mass;
    }

    public MapNode getCurrentMapNode() {
        return currentMapNode;
    }

    public void setCurrentMapNode(MapNode currentMapNode) {
        this.currentMapNode = currentMapNode;
    }

    public void removeFromCurrentNode(){
        MapNode node  = currentMapNode;
        if(node != null){
            node.getEntitySet().remove(this);
            currentMapNode = null;
        }
    }

    public  MapNode currentMapNodeProperty(){
        return currentMapNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Double.compare(entity.mass, mass) == 0 && id.equals(entity.id) && position.equals(entity.position) && Objects.equals(currentMapNode, entity.currentMapNode);
    }

}