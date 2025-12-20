package org.betonquest.betonquest.quest.event.command;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.command.SilentCommandSender;
import org.betonquest.betonquest.api.bukkit.command.SilentConsoleCommandSender;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadPlayerlessEvent;
import org.bukkit.command.CommandSender;

/**
 * Creates a new CommandEvent from an {@link DefaultInstruction}.
 */
public class CommandEventFactory extends BaseCommandEventFactory implements PlayerlessEventFactory {

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
     * @param data          the data for primary server thread access
     */
    public CommandEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        super(loggerFactory, data);
        this.silentSender = new SilentConsoleCommandSender(loggerFactory.create(SilentCommandSender.class,
                "CommandEvent"), data.server().getConsoleSender());
    }

    @Override
    public PlayerEvent parsePlayer(final DefaultInstruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(createCommandEvent(instruction), data);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final DefaultInstruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessEvent(createCommandEvent(instruction), data);
    }

    private NullableEventAdapter createCommandEvent(final DefaultInstruction instruction) throws QuestException {
        return new NullableEventAdapter(new CommandEvent(parseCommands(instruction), silentSender, data.server()));
    }
}
