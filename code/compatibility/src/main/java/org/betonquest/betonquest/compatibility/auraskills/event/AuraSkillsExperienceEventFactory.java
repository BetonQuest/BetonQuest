package org.betonquest.betonquest.compatibility.auraskills.event;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;

/**
 * Factory to create {@link AuraSkillsExperienceEvent}s from {@link Instruction}s.
 */
public class AuraSkillsExperienceEventFactory implements PlayerEventFactory {

    /**
     * The {@link AuraSkillsApi}.
     */
    private final AuraSkillsApi auraSkillsApi;

    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Factory to create AuraSkills Experience Events.
     *
     * @param auraSkillsApi the {@link AuraSkillsApi}.
     * @param data          the data used for primary server access.
     */
    public AuraSkillsExperienceEventFactory(final AuraSkillsApi auraSkillsApi, final PrimaryServerThreadData data) {
        this.auraSkillsApi = auraSkillsApi;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> nameVar = instruction.get(DefaultArgumentParsers.STRING);
        final Variable<Number> amountVar = instruction.get(DefaultArgumentParsers.NUMBER);
        final boolean isLevel = instruction.hasArgument("level");

        final PlayerEvent experience = new AuraSkillsExperienceEvent(auraSkillsApi, amountVar, nameVar, isLevel);
        return new PrimaryServerThreadEvent(experience, data);
    }
}
