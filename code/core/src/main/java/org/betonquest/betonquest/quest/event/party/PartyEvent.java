package org.betonquest.betonquest.quest.event.party;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.util.Utils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Fires specified events for every player in the party.
 */
public class PartyEvent implements OnlineEvent {

    /**
     * The Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The range of the party.
     */
    private final Argument<Number> range;

    /**
     * The maximum amount of players affected by this party event or a negative value for no maximum.
     */
    private final Argument<Number> amount;

    /**
     * The conditions that must be met by the party members.
     */
    private final Argument<List<ConditionID>> conditions;

    /**
     * The events to fire.
     */
    private final Argument<List<EventID>> events;

    /**
     * Creates a new PartyEvent instance.
     *
     * @param questTypeApi    the Quest Type API
     * @param profileProvider the profile provider instance
     * @param range           the range of the party
     * @param amount          the maximum amount of players affected by this party event or a negative value for no maximum
     * @param conditions      the conditions that must be met by the party members
     * @param events          the events to fire
     */
    public PartyEvent(final QuestTypeApi questTypeApi, final ProfileProvider profileProvider,
                      final Argument<Number> range, final Argument<Number> amount,
                      final Argument<List<ConditionID>> conditions, final Argument<List<EventID>> events) {
        this.questTypeApi = questTypeApi;
        this.profileProvider = profileProvider;
        this.range = range;
        this.amount = amount;
        this.conditions = conditions;
        this.events = events;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        for (final OnlineProfile member : getMemberList(profile)) {
            questTypeApi.events(member, events.getValue(profile));
        }
    }

    private Set<OnlineProfile> getMemberList(final OnlineProfile profile) throws QuestException {
        final int toExecute = amount != null ? amount.getValue(profile).intValue() : -1;
        final Map<OnlineProfile, Double> members = Utils.getParty(questTypeApi, profileProvider.getOnlineProfiles(),
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
