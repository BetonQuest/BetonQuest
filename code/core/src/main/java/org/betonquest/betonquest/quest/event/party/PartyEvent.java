package org.betonquest.betonquest.quest.event.party;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.variable.Variable;
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
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The range of the party.
     */
    private final Variable<Number> range;

    /**
     * The optional maximum amount of players affected by this party event.
     */
    @Nullable
    private final Variable<Number> amount;

    /**
     * The conditions that must be met by the party members.
     */
    private final Variable<List<ConditionID>> conditions;

    /**
     * The events to fire.
     */
    private final Variable<List<EventID>> events;

    /**
     * Creates a new PartyEvent instance.
     *
     * @param questTypeAPI    the Quest Type API
     * @param profileProvider the profile provider instance
     * @param range           the range of the party
     * @param amount          the optional maximum amount of players affected by this party,
     *                        null or negative values sets no maximum amount
     * @param conditions      the conditions that must be met by the party members
     * @param events          the events to fire
     */
    public PartyEvent(final QuestTypeAPI questTypeAPI, final ProfileProvider profileProvider, final Variable<Number> range,
                      @Nullable final Variable<Number> amount, final Variable<List<ConditionID>> conditions, final Variable<List<EventID>> events) {
        this.questTypeAPI = questTypeAPI;
        this.profileProvider = profileProvider;
        this.range = range;
        this.amount = amount;
        this.conditions = conditions;
        this.events = events;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        for (final OnlineProfile member : getMemberList(profile)) {
            for (final EventID event : events.getValue(profile)) {
                questTypeAPI.event(member, event);
            }
        }
    }

    private Set<OnlineProfile> getMemberList(final OnlineProfile profile) throws QuestException {
        final int toExecute = amount != null ? amount.getValue(profile).intValue() : -1;
        final Map<OnlineProfile, Double> members = Utils.getParty(questTypeAPI, profileProvider.getOnlineProfiles(),
                profile.getPlayer().getLocation(), range.getValue(profile).doubleValue(), conditions.getValue(profile));

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
