package org.betonquest.betonquest.compatibility.mmogroup.mmocore.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;

/**
 * Factory to create {@link MMOCoreClassExperienceEvent}s from {@link Instruction}s.
 */
public class MMOCoreClassExperienceEventFactory implements PlayerEventFactory {

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new MMO Core Event Factory.
     *
     * @param data the data for primary server thread access
     */
    public MMOCoreClassExperienceEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {

        final Variable<Number> amount = instruction.get(DefaultArgumentParsers.NUMBER);
        final boolean isLevel = instruction.hasArgument("level");
        return new PrimaryServerThreadEvent(new MMOCoreClassExperienceEvent(amount, isLevel), data);
    }
}
