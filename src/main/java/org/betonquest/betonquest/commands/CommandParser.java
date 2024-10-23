package org.betonquest.betonquest.commands;

/**
 * Parser for commands.
 *
 * @param <C> command to be parsed
 */
public interface CommandParser<C> {
    /**
     * Parse a list of command arguments to the correct command object.
     *
     * @param arguments arguments to parse
     * @return parsed command object
     */
    C parse(String... arguments);
}
