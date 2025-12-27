package org.betonquest.betonquest.quest.placeholder.objective;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;

/**
 * Factory to create {@link ObjectivePropertyPlaceholder}s from {@link Instruction}s.
 * <p>
 * Format:
 * {@code %objective.<id>.<property>%}
 */
public class ObjectivePropertyPlaceholderFactory implements PlayerPlaceholderFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Create a new factory to create Objective Property Placeholders.
     *
     * @param questTypeApi the Quest Type API
     */
    public ObjectivePropertyPlaceholderFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        final ObjectiveID objectiveID = instruction.parse(ObjectiveID::new).get().getValue(null);
        return new ObjectivePropertyPlaceholder(questTypeApi, objectiveID, instruction.nextElement());
    }
}
