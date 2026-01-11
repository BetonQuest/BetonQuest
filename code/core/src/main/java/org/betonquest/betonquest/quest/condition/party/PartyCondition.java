package org.betonquest.betonquest.quest.condition.party;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A condition that checks if a party meets certain conditions.
 */
public class PartyCondition implements NullableCondition {

    /**
     * The location to check for party members.
     */
    private final Argument<Location> location;

    /**
     * The range to check for party members.
     */
    private final Argument<Number> range;

    /**
     * The conditions to check for to be a party member.
     */
    private final Argument<List<ConditionIdentifier>> conditions;

    /**
     * The conditions that everyone in the party must meet.
     */
    private final Argument<List<ConditionIdentifier>> everyone;

    /**
     * The conditions that at least one party member must meet.
     */
    private final Argument<List<ConditionIdentifier>> anyone;

    /**
     * The minimum number of party members.
     */
    @Nullable
    private final Argument<Number> count;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Create a new party condition.
     *
     * @param location        the location to check for party members
     * @param range           the range to check for party members
     * @param conditions      the conditions to check for to be a party member
     * @param everyone        the conditions that everyone in the party must meet
     * @param anyone          the conditions that at least one party member must meet
     * @param count           the minimum number of party members
     * @param questTypeApi    the Quest Type API
     * @param profileProvider the profile provider instance
     */
    public PartyCondition(final Argument<Location> location, final Argument<Number> range,
                          final Argument<List<ConditionIdentifier>> conditions, final Argument<List<ConditionIdentifier>> everyone,
                          final Argument<List<ConditionIdentifier>> anyone, @Nullable final Argument<Number> count,
                          final QuestTypeApi questTypeApi, final ProfileProvider profileProvider) {
        this.location = location;
        this.range = range;
        this.conditions = conditions;
        this.everyone = everyone;
        this.anyone = anyone;
        this.count = count;
        this.questTypeApi = questTypeApi;
        this.profileProvider = profileProvider;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final Set<OnlineProfile> partyMembers = Utils.getParty(questTypeApi, profileProvider.getOnlineProfiles(),
                location.getValue(profile), range.getValue(profile).doubleValue(), conditions.getValue(profile)).keySet();

        final int pCount = count == null ? 0 : count.getValue(profile).intValue();
        if (pCount > 0 && partyMembers.size() < pCount) {
            return false;
        }

        return meetEveryoneConditions(everyone.getValue(profile), partyMembers) && meetAnyoneConditions(anyone.getValue(profile), partyMembers);
    }

    private boolean meetEveryoneConditions(final List<ConditionIdentifier> conditions, final Set<OnlineProfile> partyMembers) {
        final Stream<OnlineProfile> everyoneStream = Bukkit.isPrimaryThread() ? partyMembers.stream() : partyMembers.parallelStream();
        return everyoneStream.allMatch(member -> questTypeApi.conditions(member, conditions));
    }

    private boolean meetAnyoneConditions(final List<ConditionIdentifier> conditions, final Set<OnlineProfile> partyMembers) {
        final Stream<ConditionIdentifier> anyoneStream = Bukkit.isPrimaryThread() ? conditions.stream() : conditions.stream().parallel();
        return anyoneStream.allMatch(condition -> {
            final Stream<OnlineProfile> memberStream = Bukkit.isPrimaryThread() ? partyMembers.stream() : partyMembers.parallelStream();
            return memberStream.anyMatch(member -> questTypeApi.condition(member, condition));
        });
    }
}
