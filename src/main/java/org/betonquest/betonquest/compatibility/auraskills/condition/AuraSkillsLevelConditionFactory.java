package org.betonquest.betonquest.compatibility.auraskills.condition;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link AuraSkillsLevelCondition}s from {@link Instruction}s.
 */
public class AuraSkillsLevelConditionFactory implements PlayerConditionFactory {
    /**
     * The {@link AuraSkillsApi}.
     */
    private final AuraSkillsApi auraSkillsApi;

    /**
     * The data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Factory to create AuraSkills Stats Conditions.
     *
     * @param auraSkillsApi the {@link AuraSkillsApi}.
     * @param data          the data used for primary server access.
     */
    public AuraSkillsLevelConditionFactory(final AuraSkillsApi auraSkillsApi, final PrimaryServerThreadData data) {
        this.auraSkillsApi = auraSkillsApi;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> nameVar = instruction.get(Argument.STRING);
        final Variable<Number> targetLevelVar = instruction.get(Argument.NUMBER);
        final boolean mustBeEqual = instruction.hasArgument("equal");

        final AuraSkillsLevelCondition level = new AuraSkillsLevelCondition(auraSkillsApi, targetLevelVar, nameVar, mustBeEqual);
        return new PrimaryServerThreadPlayerCondition(level, data);
    }
}
