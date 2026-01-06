package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.Quests;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory to create {@link QuestsAction}s from {@link Instruction}s.
 */
public class QuestsActionFactory implements PlayerActionFactory {

    /**
     * Used Quests instance.
     */
    private final Quests quests;

    /**
     * Create the door action factory.
     *
     * @param quests active quests instance
     */
    public QuestsActionFactory(final Quests quests) {
        this.quests = quests;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> name = instruction.string().get();
        final FlagArgument<Boolean> override = instruction.bool().getFlag("check-requirements", true);
        return new QuestsAction(quests, name, override);
    }
}
