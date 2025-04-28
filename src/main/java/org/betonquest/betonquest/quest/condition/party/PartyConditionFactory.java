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
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.betonquest.betonquest.quest.condition.ThrowExceptionPlayerlessCondition;
import org.bukkit.Location;

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
        final Variable<Location> location = instruction.getVariable(instruction.getOptional("location", "%location%"),
                Argument.LOCATION);
        return new NullableConditionAdapter(parse(instruction, location));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        final Variable<Location> location = instruction.getVariable(instruction.getOptional("location"), Argument.LOCATION);
        if (location == null) {
            return new ThrowExceptionPlayerlessCondition();
        }
        return new NullableConditionAdapter(parse(instruction, location));
    }

    private PartyCondition parse(final Instruction instruction, final Variable<Location> location) throws QuestException {
        final Variable<Number> range = instruction.getVariable(Argument.NUMBER);
        final VariableList<ConditionID> conditions = instruction.get(PackageArgument.ofList(ConditionID::new));
        final VariableList<ConditionID> everyone = instruction.get(instruction.getOptional("every", ""), PackageArgument.ofList(ConditionID::new));
        final VariableList<ConditionID> anyone = instruction.get(instruction.getOptional("any", ""), PackageArgument.ofList(ConditionID::new));
        final Variable<Number> count = instruction.getVariable(instruction.getOptional("count"), Argument.NUMBER);

        return new PartyCondition(location, range, conditions, everyone, anyone, count, questTypeAPI, profileProvider);
    }
}
