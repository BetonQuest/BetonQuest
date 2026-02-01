package org.betonquest.betonquest.quest.action.command;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.quest.action.OpPlayerActionAdapter;
import org.bukkit.Server;

/**
 * Creates a new OpSudoAction from an {@link Instruction}.
 */
public class OpSudoActionFactory extends BaseCommandActionFactory {

    /**
     * Create the OpSudoAction factory.
     *
     * @param loggerFactory the logger factory to create a logger for the actions
     * @param server        the server to execute commands on
     */
    public OpSudoActionFactory(final BetonQuestLoggerFactory loggerFactory, final Server server) {
        super(loggerFactory, server);
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return new OnlineActionAdapter(new OpPlayerActionAdapter(new SudoAction(parseCommands(instruction))),
                loggerFactory.create(SudoAction.class), instruction.getPackage());
    }
}
