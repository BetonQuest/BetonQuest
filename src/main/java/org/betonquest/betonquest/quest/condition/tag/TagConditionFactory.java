package org.betonquest.betonquest.quest.condition.tag;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.Utils;

/**
 * Factory to create tag conditions from {@link Instruction}s.
 */
public class TagConditionFactory implements PlayerConditionFactory {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Creates the tag condition factory.
     *
     * @param betonQuest the BetonQuest instance
     */
    public TagConditionFactory(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final String tag = Utils.addPackage(instruction.getPackage(), instruction.next());
        return new TagCondition(tag, betonQuest);
    }
}
