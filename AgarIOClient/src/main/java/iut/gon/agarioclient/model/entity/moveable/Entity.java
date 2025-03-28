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
 * It provides fundamental properties and methods common to all game entities.
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
     * @param position the initial position of the entity in the game space
     * @param mass     the initial mass of the entity
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
     * Returns the current position of the entity in the game space.
     *
     * @return the current position as a Point2D object
     */
    public Point2DSerial getPosition() {
        return position;
    }

    /**
     * Sets the position of the entity in the game space.
     * Only updates the position if it is valid according to game rules.
     *
     * @param position the new position to set
     */
    public void setPosition(Point2DSerial position) {
        if(Game.isValidPosition(position)){
            this.position = new Point2DSerial(position.getX(), position.getY());
        }
    }

    /**
     * Returns the observable position property of the entity.
     * This allows for binding and change listeners to be added.
     *
     * @return the position property
     */
    public Point2DSerial positionProperty(){
        return position;
    }

    /**
     * Returns the current mass of the entity.
     *
     * @return the current mass value
     */
    public double getMass() {
        return mass;
    }

    /**
     * Sets the mass of the entity.
     *
     * @param mass the new mass value to set
     */
    public void setMass(double mass) {
        this.mass = mass;
    }

    /**
     * Gets the current map node where this entity is located.
     *
     * @return the current MapNode containing this entity
     */
    public MapNode getCurrentMapNode() {
        return currentMapNode;
    }

    /**
     * Sets the current map node for this entity.
     *
     * @param currentMapNode the new MapNode to associate with this entity
     */
    public void setCurrentMapNode(MapNode currentMapNode) {
        this.currentMapNode = currentMapNode;
    }

    /**
     * Removes this entity from its current map node.
     * Cleans up references between the entity and the node.
     */
    public void removeFromCurrentNode(){
        MapNode node  = currentMapNode;
        if(node != null){
            node.getEntitySet().remove(this);
            currentMapNode = null;
        }
    }

    /**
     * Returns the observable map node property of the entity.
     * This allows for binding and change listeners to be added.
     *
     * @return the currentMapNode property
     */
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