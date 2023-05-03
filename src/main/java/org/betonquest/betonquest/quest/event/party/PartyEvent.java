package org.betonquest.betonquest.quest.event.party;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.utils.Utils;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Fires specified events for every player in the party
 */
public class PartyEvent implements Event {

    /**
     * The range of the party.
     */
    private final VariableNumber range;

    /**
     * The optional maximum amount of players affected by this party event.
     */
    private final VariableNumber amount;

    /**
     * The conditions that must be met by the party members.
     */
    private final ConditionID[] conditions;

    /**
     * The events to fire.
     */
    private final EventID[] events;

    /**
     * Creates a new PartyEvent instance.
     *
     * @param range      the range of the party
     * @param amount     the optional maximum amount of players affected by this party,
     *                   null or negative values sets no maximum amount
     * @param conditions the conditions that must be met by the party members
     * @param events     the events to fire
     */
    public PartyEvent(final VariableNumber range, final VariableNumber amount, final ConditionID[] conditions, final EventID... events) {
        this.range = range;
        this.amount = amount;
        this.conditions = conditions.clone();
        this.events = events.clone();
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        for (final OnlineProfile member : getMemberList(profile)) {
            for (final EventID event : events) {
                BetonQuest.event(member, event);
            }
        }
    }

    private Set<OnlineProfile> getMemberList(final Profile profile) {
        final int toExecute = amount != null ? amount.getInt(profile) : -1;
        final Map<OnlineProfile, Double> members = Utils.getParty(profile.getOnlineProfile().get(), range.getDouble(profile),
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
