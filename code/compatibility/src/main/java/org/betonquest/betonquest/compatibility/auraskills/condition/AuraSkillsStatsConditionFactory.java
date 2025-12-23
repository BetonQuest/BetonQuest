package org.betonquest.betonquest.compatibility.auraskills.condition;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory to create {@link AuraSkillsStatsCondition}s from {@link Instruction}s.
 */
public class AuraSkillsStatsConditionFactory implements PlayerConditionFactory {

    /**
     * The {@link AuraSkillsApi}.
     */
    private final AuraSkillsApi auraSkillsApi;

    /**
     * Create a new Factory to create AuraSkills Stats Conditions.
     *
     * @param auraSkillsApi the {@link AuraSkillsApi}.
     */
    public AuraSkillsStatsConditionFactory(final AuraSkillsApi auraSkillsApi) {
        this.auraSkillsApi = auraSkillsApi;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> nameVar = instruction.get(instruction.getParsers().string());
        final Variable<Number> targetLevelVar = instruction.get(instruction.getParsers().number());
        final boolean mustBeEqual = instruction.hasArgument("equal");

        return new AuraSkillsStatsCondition(auraSkillsApi, targetLevelVar, nameVar, mustBeEqual);
    }
}
