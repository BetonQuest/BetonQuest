package org.betonquest.betonquest.quest.placeholder.world;

import org.bukkit.World;

import java.util.function.Function;

/**
 * Represents a property of a world placeholder.
 */
public enum WorldPlaceholderProperty {

    /**
     * The name of the world.
     */
    NAME(World::getName),
    /**
     * The unique id of the world.
     */
    UUID(world -> world.getUID().toString()),
    /**
     * The environment of the world.
     */
    ENVIRONMENT(world -> world.getEnvironment().name()),
    /**
     * The difficulty of the world.
     */
    DIFFICULTY(world -> world.getDifficulty().name()),
    /**
     * The seed of the world.
     */
    SEED(world -> String.valueOf(world.getSeed())),
    /**
     * The time of the world.
     */
    TIME(world -> String.valueOf(world.getTime())),
    /**
     * The full time of the world.
     */
    FULLTIME(world -> String.valueOf(world.getFullTime())),
    /**
     * The game time of the world.
     */
    GAMETIME(world -> String.valueOf(world.getGameTime())),
    /**
     * The player count of the world.
     */
    PLAYERCOUNT(world -> String.valueOf(world.getPlayerCount())),
    /**
     * The PVP status of the world.
     */
    PVP(world -> String.valueOf(world.getPVP())),
    /**
     * Whether the world is storming.
     */
    STORMING(world -> String.valueOf(world.hasStorm())),
    /**
     * Whether the world is thundering.
     */
    THUNDERING(world -> String.valueOf(world.isThundering())),
    /**
     * The weather duration of the world.
     */
    WEATHERDURATION(world -> String.valueOf(world.getWeatherDuration())),
    /**
     * The entity count of the world.
     */
    ENTITYCOUNT(world -> String.valueOf(world.getEntityCount())),
    /**
     * The tile entity count of the world.
     */
    TILEENTITYCOUNT(world -> String.valueOf(world.getTileEntityCount())),
    /**
     * The chunk count of the world.
     */
    CHUNKCOUNT(world -> String.valueOf(world.getChunkCount())),
    /**
     * The loaded chunk count of the world.
     */
    LOADEDCHUNKCOUNT(world -> String.valueOf(world.getLoadedChunks().length));

    /**
     * The function to get the property value.
     */
    private final Function<World, String> function;

    WorldPlaceholderProperty(final Function<World, String> function) {
        this.function = function;
    }

    /**
     * Get the value of the property for the given world.
     *
     * @param world the world to get the property value for
     * @return the value of the property
     */
    public String getValue(final World world) {
        return function.apply(world);
    }
}
