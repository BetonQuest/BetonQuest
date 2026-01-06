package org.betonquest.betonquest.compatibility.auraskills.event;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory to create {@link AuraSkillsExperienceAction}s from {@link Instruction}s.
 */
public class AuraSkillsExperienceActionFactory implements PlayerActionFactory {

    /**
     * The {@link AuraSkillsApi}.
     */
    private final AuraSkillsApi auraSkillsApi;

    /**
     * Create a new Factory to create AuraSkills Experience Events.
     *
     * @param auraSkillsApi the {@link AuraSkillsApi}.
     */
    public AuraSkillsExperienceActionFactory(final AuraSkillsApi auraSkillsApi) {
        this.auraSkillsApi = auraSkillsApi;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> name = instruction.string().get();
        final Argument<Number> amount = instruction.number().get();
        final FlagArgument<Boolean> level = instruction.bool().getFlag("level", true);
        return new AuraSkillsExperienceAction(auraSkillsApi, amount, name, level);
    }
}
