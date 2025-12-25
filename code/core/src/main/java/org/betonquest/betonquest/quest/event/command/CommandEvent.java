package org.betonquest.betonquest.quest.event.command;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.command.SilentConsoleCommandSender;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Event that runs given commands in the server console.
 */
public class CommandEvent implements NullableEvent {

    /**
     * Command sender to run the commands as.
     * <p>
     * {@link SilentConsoleCommandSender} is used to keep console and log clean.
     */
    private final CommandSender silentSender;

    /**
     * Server to run the commands on.
     */
    private final Server server;

    /**
     * The commands to run.
     */
    private final List<Argument<String>> commands;

    /**
     * Creates a new CommandEvent.
     *
     * @param commands     the commands to run
     * @param silentSender the command sender to run the commands as
     * @param server       the server to run the commands on
     */
    public CommandEvent(final List<Argument<String>> commands, final CommandSender silentSender, final Server server) {
        this.silentSender = silentSender;
        this.server = server;
        this.commands = commands;
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        try {
            for (final Argument<String> command : commands) {
                server.dispatchCommand(silentSender, command.getValue(profile));
            }
        } catch (final RuntimeException exception) {
            throw new QuestException("Unhandled exception executing command: " + exception.getMessage(), exception);
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
