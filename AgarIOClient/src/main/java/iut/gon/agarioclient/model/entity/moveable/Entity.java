package iut.gon.agarioclient.model.entity.moveable;

import iut.gon.agarioclient.model.Game;
import iut.gon.agarioclient.model.map.MapNode;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;

/**
 * The Entity class represents a basic game entity with a unique identifier, position, and mass.
 * This class serves as a base class for other specific entities like players and pellets.
 * It provides fundamental properties and methods common to all game entities.
 */
public class Entity {
    private final String id;

    private SimpleObjectProperty<Point2D> position;
    private SimpleDoubleProperty mass;
    private SimpleObjectProperty<MapNode> currentMapNode;

    /**
     * Constructs a new Entity with the specified id, position, and mass.
     *
     * @param id       the unique identifier of the entity
     * @param position the initial position of the entity in the game space
     * @param mass     the initial mass of the entity
     */
    public Entity(String id, Point2D position, double mass) {
        this.id = id;
        this.position = new SimpleObjectProperty<>(position);
        this.mass = new SimpleDoubleProperty(mass);
        this.currentMapNode = new SimpleObjectProperty<>();
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
    public Point2D getPosition() {
        return position.getValue();
    }

    /**
     * Sets the position of the entity in the game space.
     * Only updates the position if it is valid according to game rules.
     *
     * @param position the new position to set
     */
    public void setPosition(Point2D position) {
        if(Game.isValidPosition(position)){
            this.position.set(position);
        }
    }

    /**
     * Returns the observable position property of the entity.
     * This allows for binding and change listeners to be added.
     *
     * @return the position property
     */
    public SimpleObjectProperty<Point2D> positionProperty(){
        return position;
    }

    /**
     * Returns the current mass of the entity.
     *
     * @return the current mass value
     */
    public double getMass() {
        return mass.getValue();
    }

    /**
     * Returns the observable mass property of the entity.
     * This allows for binding and change listeners to be added.
     *
     * @return the mass property
     */
    public DoubleProperty massProperty(){
        return mass;
    }

    /**
     * Sets the mass of the entity.
     *
     * @param mass the new mass value to set
     */
    public void setMass(double mass) {
        this.mass.setValue(mass);
    }

    /**
     * Gets the current map node where this entity is located.
     *
     * @return the current MapNode containing this entity
     */
    public MapNode getCurrentMapNode() {
        return currentMapNode.getValue();
    }

    /**
     * Sets the current map node for this entity.
     *
     * @param currentMapNode the new MapNode to associate with this entity
     */
    public void setCurrentMapNode(MapNode currentMapNode) {
        this.currentMapNode.setValue(currentMapNode);
    }

    /**
     * Removes this entity from its current map node.
     * Cleans up references between the entity and the node.
     */
    public void removeFromCurrentNode(){
        MapNode node = currentMapNode.getValue();
        if(node != null){
            node.getEntitySet().remove(this);
            currentMapNode.setValue(null);
        }
    }

    /**
     * Returns the observable map node property of the entity.
     * This allows for binding and change listeners to be added.
     *
     * @return the currentMapNode property
     */
    public SimpleObjectProperty<MapNode> currentMapNodeProperty(){
        return currentMapNode;
    }
}