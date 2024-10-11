package org.betonquest.betonquest.quest.condition.journal;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.Utils;

/**
 * Factory for {@link JournalCondition}s.
 */
public class JournalConditionFactory implements PlayerConditionFactory {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Logger factory to create a logger for conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the journal condition factory.
     *
     * @param betonQuest    the BetonQuest instance
     * @param loggerFactory the logger factory
     */
    public JournalConditionFactory(final BetonQuest betonQuest, final BetonQuestLoggerFactory loggerFactory) {
        this.betonQuest = betonQuest;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final String targetPointer = Utils.addPackage(instruction.getPackage(), instruction.next());
        final BetonQuestLogger log = loggerFactory.create(JournalCondition.class);
        return new OnlineConditionAdapter(new JournalCondition(betonQuest, targetPointer), log, instruction.getPackage());
    }
}
