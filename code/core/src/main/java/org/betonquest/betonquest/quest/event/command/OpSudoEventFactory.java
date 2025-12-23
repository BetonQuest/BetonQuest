package org.betonquest.betonquest.quest.event.command;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.quest.event.OpPlayerEventAdapter;
import org.bukkit.Server;

/**
 * Creates a new OpSudoEvent from an {@link Instruction}.
 */
public class OpSudoEventFactory extends BaseCommandEventFactory {

    /**
     * Create the OpSudoEvent factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param server        the server to execute commands on
     */
    public OpSudoEventFactory(final BetonQuestLoggerFactory loggerFactory, final Server server) {
        super(loggerFactory, server);
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return new OnlineEventAdapter(new OpPlayerEventAdapter(new SudoEvent(parseCommands(instruction))),
                loggerFactory.create(SudoEvent.class), instruction.getPackage());
    }
}
