package org.betonquest.betonquest.quest.event.command;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.bukkit.Server;

/**
 * Creates new {@link SudoEvent}s from {@link Instruction}s.
 */
public class SudoEventFactory extends BaseCommandEventFactory {

    /**
     * Create the sudo event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param server        the data for primary server thread access
     */
    public SudoEventFactory(final BetonQuestLoggerFactory loggerFactory, final Server server) {
        super(loggerFactory, server);
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return new OnlineEventAdapter(new SudoEvent(parseCommands(instruction)),
                loggerFactory.create(SudoEvent.class), instruction.getPackage());
    }
}
