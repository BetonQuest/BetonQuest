package org.betonquest.betonquest.quest.condition.party;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.quest.condition.ThrowExceptionPlayerlessCondition;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

/**
 * Factory to create party conditions from {@link Instruction}s.
 */
public class PartyConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Create the party condition factory.
     *
     * @param questTypeApi    the Quest Type API
     * @param profileProvider the profile provider instance
     */
    public PartyConditionFactory(final QuestTypeApi questTypeApi, final ProfileProvider profileProvider) {
        this.questTypeApi = questTypeApi;
        this.profileProvider = profileProvider;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final String locationRaw = instruction.string().get("location", "%location%").getValue(null);
        final Argument<Location> location = instruction.get(locationRaw, instruction.getParsers().location());
        return new NullableConditionAdapter(parse(instruction, location));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        final Argument<Location> location = instruction.location().get("location").orElse(null);
        if (location == null) {
            return new ThrowExceptionPlayerlessCondition();
        }
        return new NullableConditionAdapter(parse(instruction, location));
    }

    private PartyCondition parse(final Instruction instruction, final Argument<Location> location) throws QuestException {
        final Argument<Number> range = instruction.number().get();
        final Argument<List<ConditionID>> conditions = instruction.parse(ConditionID::new).getList();
        final Argument<List<ConditionID>> everyone = instruction.parse(ConditionID::new)
                .getList("every", Collections.emptyList());
        final Argument<List<ConditionID>> anyone = instruction.parse(ConditionID::new)
                .getList("any", Collections.emptyList());
        final Argument<Number> count = instruction.number().get("count").orElse(null);
        return new PartyCondition(location, range, conditions, everyone, anyone, count, questTypeApi, profileProvider);
    }
}
