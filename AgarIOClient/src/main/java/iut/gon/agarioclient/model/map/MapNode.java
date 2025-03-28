package iut.gon.agarioclient.model.map;

import iut.gon.agarioclient.model.entity.moveable.Entity;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.*;

/**
 * Represents a node in the game map, which can contain entities and be subdivided into smaller nodes.
 * Each node can have up to four child nodes (NEnode, NWnode, SEnode, SWnode) and a parent node.
 */
public class MapNode {
    private MapNode NEnode; // North-East child node
    private MapNode NWnode; // North-West child node
    private MapNode SEnode; // South-East child node
    private MapNode SWnode; // South-West child node
    private MapNode parent; // Parent node
    private Direction direction; // Direction of this node relative to its parent
    private Set<Entity> entitySet; // Set of entities contained in this node
    private final Point2D beginningPoint; // Top-left corner of the node
    private final Point2D endPoint; // Bottom-right corner of the node

    /**
     * Constructs a new MapNode with the specified parameters.
     *
     * @param parent         the parent node
     * @param direction      the direction of this node relative to its parent
     * @param entitySet      the set of entities contained in this node
     * @param beginningPoint the top-left corner of the node
     * @param endPoint       the bottom-right corner of the node
     */
    public MapNode(MapNode parent, Direction direction, Set<Entity> entitySet, Point2D beginningPoint, Point2D endPoint) {
        this.NEnode = null;
        this.NWnode = null;
        this.SEnode = null;
        this.SWnode = null;
        this.parent = parent;
        this.direction = direction;
        this.entitySet = entitySet != null ? entitySet : new HashSet<>();
        this.beginningPoint = beginningPoint;
        this.endPoint = endPoint;
    }

    /**
     * Constructs a new MapNode with the specified level, beginning point, end point, and parent node.
     * Subdivides the node into four child nodes if the level is greater than 0.
     *
     * @param level          the level of the node (0 = leaf)
     * @param beginningPoint the top-left corner of the node
     * @param endPoint       the bottom-right corner of the node
     * @param parent         the parent node
     */
    public MapNode(int level, Point2D beginningPoint, Point2D endPoint, MapNode parent) {
        this.beginningPoint = beginningPoint;
        this.endPoint = endPoint;
        this.parent = parent;
        this.entitySet = new HashSet<>();

        if (beginningPoint.getX() > endPoint.getX() || beginningPoint.getY() > endPoint.getY()) {
            throw new IllegalArgumentException("beginningPoint must have coordinates less than endPoint (x AND y)");
        }

        if (level > 0) {
            Point2D middle = beginningPoint.midpoint(endPoint);

            this.setNEnode(new MapNode(level - 1,
                    new Point2D(middle.getX(), beginningPoint.getY()),
                    new Point2D(endPoint.getX(), middle.getY()), this));

            this.setNWnode(new MapNode(level - 1, beginningPoint, middle, this));

            this.setSEnode(new MapNode(level - 1, middle, endPoint, this));

            this.setSWnode(new MapNode(level - 1,
                    new Point2D(beginningPoint.getX(), middle.getY()),
                    new Point2D(middle.getX(), endPoint.getY()), this));
        }
    }

    /**
     * Constructs a new MapNode with the specified level, beginning point, and end point.
     * Subdivides the node into four child nodes if the level is greater than 0.
     *
     * @param level          the level of the node (0 = leaf)
     * @param beginningPoint the top-left corner of the node
     * @param endPoint       the bottom-right corner of the node
     */
    public MapNode(int level, Point2D beginningPoint, Point2D endPoint) {
        this(level, beginningPoint, endPoint, null);
    }

    /**
     * Adds an entity to the map node.
     * If the node is a leaf, the entity is added to the entity set.
     * Otherwise, the entity is added to the appropriate child node based on its position.
     *
     * @param e the entity to add
     */
    public void addEntity(Entity e) {
        long x = Math.round(e.getPosition().getX());
        long y = Math.round(e.getPosition().getY());

        if (!positionInNode(x, y)) {
            throw new IllegalArgumentException("The entity is not in this node: \nEntity coordinates "
                    + x + " ; " + y + "\nNode beginning point: " + beginningPoint.getX() + " ; " + beginningPoint.getY() +
                    "\nNode end point: " + endPoint.getX() + " ; " + endPoint.getY() + "\nParent? " + (parent != null));
        }
        if (isLeaf()) {
            addEntityToSet(e);
        } else {
            boolean isSouth = y > (endPoint.getY() + beginningPoint.getY()) / 2;

            if (x > (endPoint.getX() + beginningPoint.getX()) / 2) {
                // East
                if (isSouth) {
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

    /**
     * Checks if the specified position is within the boundaries of the node.
     *
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @return true if the position is within the node, false otherwise
     */
    public boolean positionInNode(double x, double y) {
        return (x <= endPoint.getX() && x >= beginningPoint.getX()) &&
                (y <= endPoint.getY() && y >= beginningPoint.getY());
    }

    /**
     * Adds an entity to the entity set of the node.
     *
     * @param e the entity to add
     */
    private void addEntityToSet(Entity e) {
        if (entitySet == null) {
            entitySet = new HashSet<>();
        }
        entitySet.add(e);
        e.setCurrentMapNode(this);
    }

    /**
     * Sets the North-East child node and updates its direction.
     *
     * @param NEnode the North-East child node
     */
    public void setNEnode(MapNode NEnode) {
        this.NEnode = NEnode;
        NEnode.setDirection(Direction.NORTH_EAST);
    }

    /**
     * Sets the North-West child node and updates its direction.
     *
     * @param NWnode the North-West child node
     */
    public void setNWnode(MapNode NWnode) {
        this.NWnode = NWnode;
        NWnode.setDirection(Direction.NORTH_WEST);
    }

    /**
     * Sets the South-East child node and updates its direction.
     *
     * @param SEnode the South-East child node
     */
    public void setSEnode(MapNode SEnode) {
        this.SEnode = SEnode;
        SEnode.setDirection(Direction.SOUTH_EAST);
    }

    /**
     * Sets the South-West child node and updates its direction.
     *
     * @param SWnode the South-West child node
     */
    public void setSWnode(MapNode SWnode) {
        this.SWnode = SWnode;
        SWnode.setDirection(Direction.SOUTH_WEST);
    }

    /**
     * Sets the parent node.
     *
     * @param parent the parent node
     */
    public void setParent(MapNode parent) {
        this.parent = parent;
    }

    /**
     * Sets the direction of this node relative to its parent.
     *
     * @param direction the direction to set
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Checks if the node is a leaf (i.e., has no child nodes).
     *
     * @return true if the node is a leaf, false otherwise
     */
    public boolean isLeaf() {
        return NEnode == null &&
                NWnode == null &&
                SEnode == null &&
                SWnode == null;
    }

    /**
     * Gets the top-left corner of the node.
     *
     * @return the top-left corner of the node
     */
    public Point2D getBeginningPoint() {
        return beginningPoint;
    }

    /**
     * Gets the bottom-right corner of the node.
     *
     * @return the bottom-right corner of the node
     */
    public Point2D getEndPoint() {
        return endPoint;
    }

    /**
     * Gets the set of entities contained in the node.
     * If the node is not a leaf, recursively gets the entities from all child nodes.
     *
     * @return the set of entities contained in the node
     */
    public Set<Entity> getEntitySet() {
        if (isLeaf()) {
            return entitySet;
        }

        Set<Entity> entities = new HashSet<>();

        if (NEnode != null) entities.addAll(NEnode.getEntitySet());
        if (NWnode != null) entities.addAll(NWnode.getEntitySet());
        if (SEnode != null) entities.addAll(SEnode.getEntitySet());
        if (SWnode != null) entities.addAll(SWnode.getEntitySet());

        return entities;
    }

    /**
     * Gets the North-East child node.
     *
     * @return the North-East child node
     */
    public MapNode getNEnode() {
        return NEnode;
    }

    /**
     * Gets the North-West child node.
     *
     * @return the North-West child node
     */
    public MapNode getNWnode() {
        return NWnode;
    }

    /**
     * Gets the South-East child node.
     *
     * @return the South-East child node
     */
    public MapNode getSEnode() {
        return SEnode;
    }

    /**
     * Gets the South-West child node.
     *
     * @return the South-West child node
     */
    public MapNode getSWnode() {
        return SWnode;
    }

    /**
     * Gets the parent node.
     *
     * @return the parent node
     */
    public MapNode getParent() {
        return parent;
    }

    /**
     * Gets the direction of this node relative to its parent.
     *
     * @return the direction of this node
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Gets the node to the north of this node.
     *
     * @return the node to the north, or null if there is no northern node
     */
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
                throw new IllegalStateException("Unknown direction (new direction added to enum?). Direction = " + direction.toString());
            }
        }
    }

    /**
     * Gets the node to the south of this node.
     *
     * @return the node to the south, or null if there is no southern node
     */
    public MapNode getSouthElt() {
        if (parent == null) return null;

        MapNode parentSouth = parent.getSouthElt();

        switch (direction) {
            case NORTH_EAST -> {
                return parent.getSEnode();
            }
            case NORTH_WEST -> {
                return parent.getSWnode();
            }
            case SOUTH_EAST -> {
                if (parentSouth == null) {
                    return null;
                }
                return parentSouth.getNEnode();
            }
            case SOUTH_WEST -> {
                if (parentSouth == null) {
                    return null;
                }
                return parentSouth.getNWnode();
            }
            default ->
                    throw new IllegalStateException("Unknown direction (new direction added to enum?). Direction = " + direction.toString());
        }
    }

    /**
     * Gets the node to the east of this node.
     *
     * @return the node to the east, or null if there is no eastern node
     */
    public MapNode getEastElt() {
        if (parent == null) return null;

        MapNode parentEast = parent.getEastElt();

        switch (direction) {
            case NORTH_EAST -> {
                if (parentEast == null) {
                    return null;
                }
                return parentEast.getNWnode();
            }
            case NORTH_WEST -> {
                return parent.getNEnode();
            }
            case SOUTH_EAST -> {
                if (parentEast == null) {
                    return null;
                }
                return parentEast.getSWnode();
            }
            case SOUTH_WEST -> {
                return parent.getSEnode();
            }
            default ->
                    throw new IllegalStateException("Unknown direction (new direction added to enum?). Direction = " + direction.toString());
        }
    }

    /**
     * Gets the node to the west of this node.
     *
     * @return the node to the west, or null if there is no western node
     */
    public MapNode getWestElt() {
        if (parent == null) return null;

        MapNode parentWest = parent.getWestElt();

        switch (direction) {
            case NORTH_EAST -> {
                return parent.getNWnode();
            }
            case NORTH_WEST -> {
                if (parentWest == null) {
                    return null;
                }
                return parentWest.getNEnode();
            }
            case SOUTH_EAST -> {
                return parent.getSWnode();
            }
            case SOUTH_WEST -> {
                if (parentWest == null) {
                    return null;
                }
                return parentWest.getSEnode();
            }
            default ->
                    throw new IllegalStateException("Unknown direction (new direction added to enum?). Direction = " + direction.toString());
        }
    }

    /**
     * Draws the borders of the node on the specified pane.
     * If the node is a leaf, draws a rectangle representing the node's boundaries.
     * Otherwise, recursively draws the borders of all child nodes.
     *
     * @param pane the pane to draw the borders on
     */
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
     * Gets the nodes surrounding this node within the specified radius.
     * Recursively collects the surrounding nodes and adds them to the specified set.
     *
     * @param radius the radius in terms of chunks (smallest possible node)
     * @param set    the set of nodes being collected
     * @return the set of nodes within the specified radius
     */
    private Set<MapNode> getSurroundingNodes(int radius, Set<MapNode> set) {
        if (set.contains(this)) return set;

        set.add(this);

        if (radius == 0) {
            return set;
        }

        ArrayList<MapNode> listSurroundingNodes = new ArrayList<>(Arrays.asList(
                getNorthElt(),
                getSouthElt(),
                getEastElt(),
                getWestElt()
        ));

        for (MapNode node : listSurroundingNodes) {
            if (node != null) {
                Set<MapNode> newSet = node.getSurroundingNodes(radius - 1, set);
                set.addAll(newSet);
            }
        }

        return set;
    }

    /**
     * Gets the nodes surrounding this node within the specified radius.
     *
     * @param radius the radius in terms of chunks (smallest possible node)
     * @return the set of nodes within the specified radius
     */
    public Set<MapNode> getSurroundingNodes(int radius) {
        return getSurroundingNodes(radius, new HashSet<>());
    }
}