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
import org.bukkit.Location;

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
        final List<OnlineProfile> members = Utils.getParty(profile.getOnlineProfile().get(), range.getDouble(profile),
                conditions);
        int toExecute;
        if (amount != null) {
            toExecute = amount.getInt(profile);
            sortPartyByDistance(profile.getOnlineProfile().get().getPlayer().getLocation(), members);
        } else {
            toExecute = -1;
        }
        for (final OnlineProfile member : members) {
            if (toExecute == 0) {
                return;
            }
            for (final EventID event : events) {
                BetonQuest.event(member, event);
            }
            toExecute--;
        }
    }

    private void sortPartyByDistance(final Location center, final List<OnlineProfile> members) {
        members.sort((o1, o2) -> {
            final double distance1 = center.distanceSquared(o1.getPlayer().getLocation());
            final double distance2 = center.distanceSquared(o2.getPlayer().getLocation());
            if (distance1 < distance2) {
                return -1;
            } else if (distance1 > distance2) {
                return 1;
            }
            return 0;
        });
    }
}
