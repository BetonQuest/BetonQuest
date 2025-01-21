package org.betonquest.betonquest.quest.condition.party;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.Bukkit;
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
    private final VariableLocation location;

    /**
     * The range to check for party members.
     */
    private final VariableNumber range;

    /**
     * The conditions to check for to be a party member.
     */
    private final List<ConditionID> conditions;

    /**
     * The conditions that everyone in the party must meet.
     */
    private final List<ConditionID> everyone;

    /**
     * The conditions that at least one party member must meet.
     */
    private final List<ConditionID> anyone;

    /**
     * The minimum number of party members.
     */
    @Nullable
    private final VariableNumber count;

    /**
     * Create a new party condition.
     *
     * @param location   the location to check for party members
     * @param range      the range to check for party members
     * @param conditions the conditions to check for to be a party member
     * @param everyone   the conditions that everyone in the party must meet
     * @param anyone     the conditions that at least one party member must meet
     * @param count      the minimum number of party members
     */
    public PartyCondition(final VariableLocation location, final VariableNumber range, final List<ConditionID> conditions, final List<ConditionID> everyone,
                          final List<ConditionID> anyone, @Nullable final VariableNumber count) {
        this.location = location;
        this.range = range;
        this.conditions = List.copyOf(conditions);
        this.everyone = List.copyOf(everyone);
        this.anyone = List.copyOf(anyone);
        this.count = count;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final Set<OnlineProfile> partyMembers = Utils.getParty(location.getValue(profile), range.getValue(profile).doubleValue(), conditions).keySet();

        final int pCount = count == null ? 0 : count.getValue(profile).intValue();
        if (pCount > 0 && partyMembers.size() < pCount) {
            return false;
        }

        return meetEveryoneConditions(partyMembers) && meetAnyoneConditions(partyMembers);
    }

    private boolean meetEveryoneConditions(final Set<OnlineProfile> partyMembers) {
        final Stream<OnlineProfile> everyoneStream = Bukkit.isPrimaryThread() ? partyMembers.stream() : partyMembers.parallelStream();
        return everyoneStream.allMatch(member -> BetonQuest.conditions(member, everyone));
    }

    private boolean meetAnyoneConditions(final Set<OnlineProfile> partyMembers) {
        final Stream<ConditionID> anyoneStream = Bukkit.isPrimaryThread() ? anyone.stream() : anyone.stream().parallel();
        return anyoneStream.allMatch(condition -> {
            final Stream<OnlineProfile> memberStream = Bukkit.isPrimaryThread() ? partyMembers.stream() : partyMembers.parallelStream();
            return memberStream.anyMatch(member -> BetonQuest.condition(member, condition));
        });
    }
}
