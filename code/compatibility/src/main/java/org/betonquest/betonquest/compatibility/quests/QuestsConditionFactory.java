package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.Quests;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link QuestsCondition}s from {@link Instruction}s.
 */
public class QuestsConditionFactory implements PlayerConditionFactory {

    /**
     * Used Quests instance.
     */
    private final Quests quests;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the quests condition factory.
     *
     * @param quests active quests instance
     * @param data   the data for primary server thread access
     */
    public QuestsConditionFactory(final Quests quests, final PrimaryServerThreadData data) {
        this.quests = quests;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> name = instruction.get(Argument.STRING);
        return new PrimaryServerThreadPlayerCondition(new QuestsCondition(quests, name), data);
    }
}
