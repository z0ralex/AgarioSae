// Entity.java
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
     * @param position the position of the entity in the game space
     * @param mass     the mass of the entity
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
     * Returns the position of the entity in the game space.
     *
     * @return the position of the entity
     */
    public Point2D getPosition() {
        return position.getValue();
    }

    /**
     * Sets the position of the entity in the game space.
     *
     * @param position the new position of the entity
     */
    public void setPosition(Point2D position) {
        if(Game.isValidPosition(position)){
            this.position.set(position);
        }
    }

    public SimpleObjectProperty<Point2D> positionProperty(){
        return position;
    }

    /**
     * Returns the mass of the entity.
     *
     * @return the mass of the entity
     */
    public double getMass() {
        return mass.getValue();
    }

    public DoubleProperty massProperty(){
        return mass;
    }


    /**
     * Sets the mass of the entity.
     *
     * @param mass the new mass of the entity
     */
    public void setMass(double mass) {
        this.mass.setValue(mass);
    }

    public MapNode getCurrentMapNode() {
        return currentMapNode.getValue();
    }

    public void setCurrentMapNode(MapNode currentMapNode) {
        this.currentMapNode.setValue(currentMapNode);
    }

    public void removeFromCurrentNode(){
        MapNode node  = currentMapNode.getValue();
        if(node != null){
            node.getEntitySet().remove(this);
            currentMapNode.setValue(null);
        }
    }

    public SimpleObjectProperty<MapNode> currentMapNodeProperty(){
        return currentMapNode;
    }

}