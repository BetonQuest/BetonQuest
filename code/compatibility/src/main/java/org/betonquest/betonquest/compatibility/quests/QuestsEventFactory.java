package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.Quests;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;

/**
 * Factory to create {@link QuestsEvent}s from {@link Instruction}s.
 */
public class QuestsEventFactory implements PlayerEventFactory {

    /**
     * Used Quests instance.
     */
    private final Quests quests;

    /**
     * Create the door event factory.
     *
     * @param quests active quests instance
     */
    public QuestsEventFactory(final Quests quests) {
        this.quests = quests;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> name = instruction.string().get();
        final FlagArgument<Boolean> override = instruction.bool().getFlag("check-requirements", true);
        return new QuestsEvent(quests, name, override);
    }
}
