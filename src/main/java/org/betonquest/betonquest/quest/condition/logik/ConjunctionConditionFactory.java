package org.betonquest.betonquest.quest.condition.logik;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.PackageArgument;

/**
 * Factory for the {@link ConjunctionCondition} class.
 */
public class ConjunctionConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Constructor for the {@link ConjunctionConditionFactory} class.
     *
     * @param questTypeAPI the Quest Type API
     */
    public ConjunctionConditionFactory(final QuestTypeAPI questTypeAPI) {
        this.questTypeAPI = questTypeAPI;
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
        return new ConjunctionCondition(instruction.get(PackageArgument.ofList(ConditionID::new)), questTypeAPI);
    }
}
