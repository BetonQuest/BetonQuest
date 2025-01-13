package org.betonquest.betonquest.quest.condition.logik;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.BetonQuestAPI;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * Factory for the {@link ConjunctionCondition} class.
 */
public class ConjunctionConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * BetonQuest API.
     */
    private final BetonQuestAPI questAPI;

    /**
     * Constructor for the {@link ConjunctionConditionFactory} class.
     *
     * @param questAPI the BetonQuest API
     */
    public ConjunctionConditionFactory(final BetonQuestAPI questAPI) {
        this.questAPI = questAPI;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parse(instruction));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parse(instruction));
    }

    private ConjunctionCondition parse(final Instruction instruction) throws QuestException {
        return new ConjunctionCondition(instruction.getList(instruction::getCondition), questAPI);
    }
}
