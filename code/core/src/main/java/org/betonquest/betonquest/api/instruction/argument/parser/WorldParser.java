package org.betonquest.betonquest.api.instruction.argument.parser;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.bukkit.Server;
import org.bukkit.World;

/**
 * Parses a string to a world.
 */
public class WorldParser implements SimpleArgumentParser<World> {

    /**
     * The server to use.
     */
    private final Server server;

    /**
     * Creates a new parser for worlds.
     *
     * @param server the server to use
     */
    public WorldParser(final Server server) {
        this.server = server;
    }

    /**
     * Parses the given value to a world.
     *
     * @param value  the value to parse
     * @param server the server to use
     * @return the parsed world
     * @throws QuestException if the value could not be parsed to a world
     */
    public static World parse(final String value, final Server server) throws QuestException {
        final World world = server.getWorld(value);
        if (world == null) {
            throw new QuestException("World " + value + " does not exists.");
        }
        return world;
    }

    @Override
    public World apply(final String string) throws QuestException {
        return parse(string, server);
    }
}
