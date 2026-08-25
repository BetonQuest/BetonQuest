package org.betonquest.betonquest.compatibility.discordsrv.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory to create {@link DiscordLinkedCondition}s from {@link Instruction}s.
 */
public class DiscordLinkedConditionFactory implements PlayerConditionFactory {

    /**
     * Creates a new factory for discord linked conditions.
     */
    public DiscordLinkedConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new DiscordLinkedCondition();
    }
}
