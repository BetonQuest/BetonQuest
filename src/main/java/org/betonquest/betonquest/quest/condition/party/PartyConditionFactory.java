package org.betonquest.betonquest.quest.condition.party;

import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.IDArgument;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.condition.ThrowExceptionPlayerlessCondition;

/**
 * Factory to create party conditions from {@link Instruction}s.
 */
public class PartyConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Create the party condition factory.
     *
     * @param questTypeAPI    the Quest Type API
     * @param profileProvider the profile provider instance
     */
    public PartyConditionFactory(final QuestTypeAPI questTypeAPI, final ProfileProvider profileProvider) {
        this.questTypeAPI = questTypeAPI;
        this.profileProvider = profileProvider;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final VariableLocation location = instruction.get(instruction.getOptional("location", "%location%"),
                VariableLocation::new);
        return new NullableConditionAdapter(parse(instruction, location));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        final VariableLocation location = instruction.get(instruction.getOptional("location"), VariableLocation::new);
        if (location == null) {
            return new ThrowExceptionPlayerlessCondition();
        }
        return new NullableConditionAdapter(parse(instruction, location));
    }

    private PartyCondition parse(final Instruction instruction, final VariableLocation location) throws QuestException {
        final VariableNumber range = instruction.get(VariableNumber::new);
        final VariableList<ConditionID> conditions = instruction.get(IDArgument.ofList(ConditionID::new));
        final VariableList<ConditionID> everyone = instruction.get(instruction.getOptional("every", ""), IDArgument.ofList(ConditionID::new));
        final VariableList<ConditionID> anyone = instruction.get(instruction.getOptional("any", ""), IDArgument.ofList(ConditionID::new));
        final VariableNumber count = instruction.get(instruction.getOptional("count"), VariableNumber::new);

        return new PartyCondition(location, range, conditions, everyone, anyone, count, questTypeAPI, profileProvider);
    }
}
