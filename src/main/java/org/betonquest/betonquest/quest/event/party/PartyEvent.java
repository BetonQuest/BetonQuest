package org.betonquest.betonquest.quest.event.party;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.util.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Fires specified events for every player in the party.
 */
public class PartyEvent implements OnlineEvent {

    /**
     * The range of the party.
     */
    private final VariableNumber range;

    /**
     * The optional maximum amount of players affected by this party event.
     */
    @Nullable
    private final VariableNumber amount;

    /**
     * The conditions that must be met by the party members.
     */
    private final List<ConditionID> conditions;

    /**
     * The events to fire.
     */
    private final List<EventID> events;

    /**
     * Creates a new PartyEvent instance.
     *
     * @param range      the range of the party
     * @param amount     the optional maximum amount of players affected by this party,
     *                   null or negative values sets no maximum amount
     * @param conditions the conditions that must be met by the party members
     * @param events     the events to fire
     */
    public PartyEvent(final VariableNumber range, @Nullable final VariableNumber amount, final List<ConditionID> conditions,
                      final List<EventID> events) {
        this.range = range;
        this.amount = amount;
        this.conditions = List.copyOf(conditions);
        this.events = List.copyOf(events);
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        for (final OnlineProfile member : getMemberList(profile)) {
            for (final EventID event : events) {
                BetonQuest.event(member, event);
            }
        }
    }

    private Set<OnlineProfile> getMemberList(final OnlineProfile profile) throws QuestException {
        final int toExecute = amount != null ? amount.getValue(profile).intValue() : -1;
        final Map<OnlineProfile, Double> members = Utils.getParty(profile.getPlayer().getLocation(), range.getValue(profile).doubleValue(),
                conditions);

        if (toExecute < 0) {
            return members.keySet();
        }

        return members.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(toExecute)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
