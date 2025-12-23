package org.betonquest.betonquest.compatibility.auraskills.event;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;

/**
 * Factory to create {@link AuraSkillsExperienceEvent}s from {@link Instruction}s.
 */
public class AuraSkillsExperienceEventFactory implements PlayerEventFactory {

    /**
     * The {@link AuraSkillsApi}.
     */
    private final AuraSkillsApi auraSkillsApi;

    /**
     * Create a new Factory to create AuraSkills Experience Events.
     *
     * @param auraSkillsApi the {@link AuraSkillsApi}.
     */
    public AuraSkillsExperienceEventFactory(final AuraSkillsApi auraSkillsApi) {
        this.auraSkillsApi = auraSkillsApi;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> nameVar = instruction.string().get();
        final Variable<Number> amountVar = instruction.number().get();
        final boolean isLevel = instruction.hasArgument("level");

        return new AuraSkillsExperienceEvent(auraSkillsApi, amountVar, nameVar, isLevel);
    }
}
