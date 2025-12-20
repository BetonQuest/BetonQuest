package org.betonquest.betonquest.compatibility.mmogroup.mmocore.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;

/**
 * Factory to create {@link MMOCoreSkillPointsEvent}s from {@link DefaultInstruction}s.
 */
public class MMOCoreSkillPointsEventFactory implements PlayerEventFactory {

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new MMO Core Event Factory.
     *
     * @param data the data for primary server thread access
     */
    public MMOCoreSkillPointsEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final DefaultInstruction instruction) throws QuestException {
        final Variable<Number> amount = instruction.get(Argument.NUMBER);
        return new PrimaryServerThreadEvent(new MMOCoreSkillPointsEvent(amount), data);
    }
}
