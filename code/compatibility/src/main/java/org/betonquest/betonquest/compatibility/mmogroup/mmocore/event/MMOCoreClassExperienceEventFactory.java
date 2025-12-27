package org.betonquest.betonquest.compatibility.mmogroup.mmocore.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;

/**
 * Factory to create {@link MMOCoreClassExperienceEvent}s from {@link Instruction}s.
 */
public class MMOCoreClassExperienceEventFactory implements PlayerEventFactory {

    /**
     * Create a new MMO Core Event Factory.
     */
    public MMOCoreClassExperienceEventFactory() {
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {

        final Argument<Number> amount = instruction.number().get();
        final FlagArgument<Boolean> level = instruction.bool().getFlag("level", false);
        return new MMOCoreClassExperienceEvent(amount, level);
    }
}
