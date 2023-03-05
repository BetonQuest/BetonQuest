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

import java.util.List;

/**
 * Fires specified events for every player in the party
 */
public class PartyEvent implements Event {

    /**
     * The range of the party.
     */
    private final VariableNumber range;

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
     * @param conditions the conditions that must be met by the party members
     * @param events     the events to fire
     */
    public PartyEvent(final VariableNumber range, final ConditionID[] conditions, final EventID... events) {
        this.range = range;
        this.conditions = conditions.clone();
        this.events = events.clone();
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final List<OnlineProfile> members = Utils.getParty(profile.getOnlineProfile().get(), range.getDouble(profile),
                conditions);
        for (final OnlineProfile member : members) {
            for (final EventID event : events) {
                BetonQuest.event(member, event);
            }
        }
    }
}
