package org.betonquest.betonquest.quest.condition.tag;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.Utils;

/**
 * Factory to create global tag conditions from {@link Instruction}s.
 */
public class GlobalTagConditionFactory implements PlayerlessConditionFactory {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Creates the tag condition factory.
     *
     * @param betonQuest the BetonQuest instance
     */
    public GlobalTagConditionFactory(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws InstructionParseException {
        final String tag = Utils.addPackage(instruction.getPackage(), instruction.next());
        return new GlobalTagCondition(betonQuest, tag);
    }
}
