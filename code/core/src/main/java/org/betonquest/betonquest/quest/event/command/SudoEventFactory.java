package org.betonquest.betonquest.quest.event.command;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;
import org.bukkit.Server;

/**
 * Creates new {@link SudoEvent}s from {@link Instruction}s.
 */
public class SudoEventFactory extends BaseCommandEventFactory {

    /**
     * Create the sudo event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param server        the server to execute commands on
     */
    public SudoEventFactory(final BetonQuestLoggerFactory loggerFactory, final Server server) {
        super(loggerFactory, server);
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return new OnlineActionAdapter(new SudoEvent(parseCommands(instruction)),
                loggerFactory.create(SudoEvent.class), instruction.getPackage());
    }
}
