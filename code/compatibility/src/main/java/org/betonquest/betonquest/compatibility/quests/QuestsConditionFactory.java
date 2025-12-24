package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.Quests;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory to create {@link QuestsCondition}s from {@link Instruction}s.
 */
public class QuestsConditionFactory implements PlayerConditionFactory {

    /**
     * Used Quests instance.
     */
    private final Quests quests;

    /**
     * Create the quests condition factory.
     *
     * @param quests active quests instance
     */
    public QuestsConditionFactory(final Quests quests) {
        this.quests = quests;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> name = instruction.string().get();
        return new QuestsCondition(quests, name);
    }
}
