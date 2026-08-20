package org.betonquest.betonquest.command.quest;

import org.betonquest.betonquest.api.QuestException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A simple sub command.
 */
public interface SubCommand {

    /**
     * Primary command name with optional aliases.
     *
     * @return list of command names
     */
    @Contract(pure = true)
    List<String> names();

    /**
     * Primary command name with command syntax (help).
     *
     * @return command name to show with its syntax
     */
    Map.Entry<String, String> syntax();

    /**
     * Executes the command.
     *
     * @param sender the source of the command
     * @param args   the passed command arguments
     * @throws QuestException if the command execution fails
     */
    void handle(CommandSender sender, String... args) throws QuestException;

    /**
     * Returns a list including all possible options for tab complete of the sub command.
     * <p>
     * An empty optional indicates using the player names.
     * <p>
     * An empty list indicates no suggestions should be shown at all.
     *
     * @param args the arguments passed to the command, including final partial argument to be completed
     * @return the list of suggestions for the command
     */
    default Optional<List<String>> complete(final String... args) {
        return Optional.of(new ArrayList<>());
    }
}
