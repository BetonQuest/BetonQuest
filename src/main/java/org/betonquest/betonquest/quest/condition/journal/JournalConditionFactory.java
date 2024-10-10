package org.betonquest.betonquest.quest.condition.journal;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
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
     * Create the journal condition factory.
     *
     * @param betonQuest the BetonQuest instance
     */
    public JournalConditionFactory(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final String targetPointer = Utils.addPackage(instruction.getPackage(), instruction.next());
        return new JournalCondition(betonQuest, targetPointer);
    }
}
