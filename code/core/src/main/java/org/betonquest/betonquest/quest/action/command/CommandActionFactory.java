package org.betonquest.betonquest.quest.action.command;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.command.SilentCommandSender;
import org.betonquest.betonquest.api.bukkit.command.SilentConsoleCommandSender;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

/**
 * Creates a new CommandEvent from an {@link Instruction}.
 */
public class CommandActionFactory extends BaseCommandActionFactory implements PlayerlessActionFactory {

    /**
     * Command sender to run the commands as.
     * <p>
     * {@link SilentConsoleCommandSender} is used to keep console and log clean.
     */
    private final CommandSender silentSender;

    /**
     * Create the command event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param server        the server to execute commands on
     */
    public CommandActionFactory(final BetonQuestLoggerFactory loggerFactory, final Server server) {
        super(loggerFactory, server);
        this.silentSender = new SilentConsoleCommandSender(loggerFactory.create(SilentCommandSender.class,
                "CommandEvent"), server.getConsoleSender());
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createCommandEvent(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createCommandEvent(instruction);
    }

    private NullableActionAdapter createCommandEvent(final Instruction instruction) throws QuestException {
        return new NullableActionAdapter(new CommandAction(parseCommands(instruction), silentSender, server));
    }
}
