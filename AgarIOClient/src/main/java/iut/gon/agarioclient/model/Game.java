// Game.java
package iut.gon.agarioclient.model;

import iut.gon.agarioclient.model.entity.moveable.*;
import iut.gon.agarioclient.model.entity.pellet.NoEffectPelletFactory;
import iut.gon.agarioclient.model.entity.pellet.Pellet;
import iut.gon.agarioclient.model.map.MapNode;
import javafx.geometry.Point2D;

import java.util.*;

/**
 * Represents the game model, managing players, enemies, pellets, and the game map.
 * Handles game initialization, entity movement, collision detection, and game updates.
 */
public class Game {
    // Constants for game configuration
    public static final int INITIAL_PLAYER_MASS = 10;
    public static final int INITIAL_PELLET_NB = 20;
    public static final int MAX_PELLET = 1500;
    public static final int INITIAL_PLAYER_SPEED = 5;
    public static final double PLAYER_SPAWNPOINT_X = 400;
    public static final double PLAYER_SPAWNPOINT_Y = 300;
    public static final int X_MAX = 8000;
    public static final int Y_MAX = 6000;

    private final MapNode root; // Root node of the game map
    private final NoEffectPelletFactory pelletFactory = new NoEffectPelletFactory(); // Factory for generating pellets

    private final List<Ennemy> enemyList; // List of enemies in the game
    private final Set<Pellet> pellets; // Set of pellets in the game
    private final Set<Player> players; // Set of players in the game

    /**
     * Checks if the specified position is within the valid game boundaries.
     *
     * @param pos the position to check
     * @return true if the position is valid, false otherwise
     */
    public static boolean isValidPosition(Point2D pos) {
        return (pos.getX() >= 0 && pos.getX() <= X_MAX) &&
                (pos.getY() >= 0 && pos.getY() <= Y_MAX);
    }

    /**
     * Constructs a new Game instance and initializes the game map, enemies, and collections.
     */
    public Game() {
        root = new MapNode(4, new Point2D(0, 0), new Point2D(X_MAX, Y_MAX));
        if (root == null) {
            throw new IllegalStateException("Root MapNode is not initialized.");
        }

        NoEffectLocalEnnemyFactory f = new NoEffectLocalEnnemyFactory(root);
        enemyList = f.generate(10);
        pellets = new HashSet<>();
        players = new HashSet<>();
    }

    /**
     * Gets the root node of the game map.
     *
     * @return the root node
     */
    public MapNode getRoot() {
        return root;
    }

    /**
     * Updates the game state for the next tick.
     * Moves entities, spawns pellets, and checks for collisions.
     *
     * @return a map of entities and the entities they have eaten
     */
    public synchronized HashMap<Entity, Set<Entity>> nextTick() {
        HashMap<Entity, Set<Entity>> map = new HashMap<>();
        for (Player p : players) {
            checkEntityChunk(p);
        }

        for (Ennemy ennemy : enemyList) {
            ennemy.executeStrat();
            double speedE = ennemy.calculateSpeed(ennemy.getPosition().getX(), ennemy.getPosition().getY(), X_MAX, Y_MAX);
            ennemy.setSpeed(speedE);
        }

        spawnPellets().forEach((pellet -> {
            map.put(pellet, null);
        }));

        map.putAll(getEatenEntities(map.keySet()));
        return map;
    }

    /**
     * Moves the specified player entity by the given vector.
     * Ensures the new position is within the game boundaries.
     *
     * @param entity the player entity to move
     * @param vector the movement vector
     */
    public void moveEntity(Player entity, Point2D vector) {
        Point2D newPosition = entity.getPosition().add(vector.multiply(entity.getSpeed()));

        // Check for collisions with the map boundaries
        double newX = Math.max(0, Math.min(newPosition.getX(), X_MAX));
        double newY = Math.max(0, Math.min(newPosition.getY(), Y_MAX));
        newPosition = new Point2D(newX, newY);

        entity.setPosition(newPosition);
    }

    /**
     * Adds an enemy to the game and places it in the game map.
     *
     * @param e the enemy to add
     */
    public void addEnemy(Ennemy e) {
        root.addEntity(e);
    }

    /**
     * Spawns new pellets if the current number of pellets is below the maximum limit.
     *
     * @return a list of newly spawned pellets
     */
    public List<Pellet> spawnPellets() {
        if (pellets.size() < MAX_PELLET) {
            return createPellets(Math.min(100, MAX_PELLET - pellets.size()));
        }

        return new ArrayList<>();
    }

    /**
     * Creates a specified number of pellets and adds them to the game map.
     *
     * @param count the number of pellets to create
     * @return a list of created pellets
     */
    public List<Pellet> createPellets(int count) {
        List<Pellet> pelletsList = pelletFactory.generatePellets(count);
        for (Pellet pellet : pelletsList) {
            getRoot().addEntity(pellet);
            root.addEntity(pellet);
            pellets.add(pellet);
        }

        return pelletsList;
    }

    /**
     * Adds a new player to the game with the specified nickname.
     *
     * @param nickname the nickname of the player
     * @return the created player
     */
    public Player addPlayer(String nickname) {
        Player player = new Player(nickname, new Point2D(PLAYER_SPAWNPOINT_X, PLAYER_SPAWNPOINT_Y), INITIAL_PLAYER_MASS);
        player.add(new PlayerLeaf(nickname, new Point2D(PLAYER_SPAWNPOINT_Y, PLAYER_SPAWNPOINT_Y), INITIAL_PLAYER_MASS, INITIAL_PLAYER_SPEED));
        players.add(player);

        root.addEntity(player);

        return player;
    }

    /**
     * Checks if the entity has moved out of its current chunk and updates its position in the game map.
     *
     * @param entity the entity to check
     */
    public void checkEntityChunk(Entity entity) {
        if (entity.getCurrentMapNode() != null &&
                !entity.getCurrentMapNode().positionInNode(entity.getPosition().getX(), entity.getPosition().getY())) {

            entity.removeFromCurrentNode();
            root.addEntity(entity);
        }
    }

    /**
     * Gets the entities that have been eaten by other entities.
     * Removes the eaten entities from the game.
     *
     * @param newEntity the set of new entities to check
     * @return a map of entities and the entities they have eaten
     */
    public HashMap<Entity, Set<Entity>> getEatenEntities(Set<Entity> newEntity) {
        HashMap<Entity, Set<Entity>> eatenMap = new HashMap<>();

        for (Ennemy ennemy : enemyList) {
            Set<Entity> eaten = new HashSet<>();

            eaten.addAll(ennemy.checkCollisions(pellets));
            eaten.addAll(ennemy.checkCollisionsWithEnemies(enemyList));
            eaten.addAll(ennemy.checkCollisionsWithPlayers(players));

            eatenMap.put(ennemy, eaten);
        }

        for (Player player : players) {
            Set<Entity> eaten = new HashSet<>();

            eaten.addAll(player.checkCollisionsWithEnemies(enemyList));
            eaten.addAll(player.checkCollisionsWithPellet(pellets));
            eaten.addAll(player.checkCollisionsWithPlayers(players));

            eatenMap.put(player, eaten);
        }

        HashSet<Entity> allEaten = new HashSet<>();
        eatenMap.values().forEach(allEaten::addAll);

        // Remove eaten entities from the game
        for (Entity e : allEaten) {
            removeEntity(e);
        }

        return eatenMap;
    }

    /**
     * Removes an entity from the game and its current map node.
     *
     * @param entity the entity to remove
     */
    public void removeEntity(Entity entity) {
        if (entity instanceof Ennemy) {
            enemyList.remove(entity);
        } else if (entity instanceof Player) {
            players.remove(entity);
        } else {
            pellets.remove((Pellet) entity);
        }

        entity.removeFromCurrentNode();
    }
}