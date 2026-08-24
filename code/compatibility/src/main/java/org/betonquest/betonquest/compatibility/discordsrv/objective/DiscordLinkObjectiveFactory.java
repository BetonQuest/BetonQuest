package org.betonquest.betonquest.compatibility.discordsrv.objective;

import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

import java.util.HashSet;
import java.util.Set;

/**
 * Factory for creating {@link DiscordLinkObjective} instances from {@link Instruction}s.
 */
public class DiscordLinkObjectiveFactory implements ObjectiveFactory {

    /**
     * The profile provider.
     */
    private final ProfileProvider profileProvider;

    /**
     * All active event hooks.
     */
    private final Set<DiscordLinkObjective> eventHooked;

    /**
     * Creates a new objective factory.
     *
     * @param profileProvider the profile provider
     */
    public DiscordLinkObjectiveFactory(final ProfileProvider profileProvider) {
        this.profileProvider = profileProvider;
        this.eventHooked = new HashSet<>();
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final DiscordLinkObjective linkObjective = new DiscordLinkObjective(service);
        eventHooked.add(linkObjective);
        return linkObjective;
    }

    /**
     * Called when the player has linked their Discord account.
     * Is hooked to the api of DiscordSRV.
     *
     * @param event the event
     */
    @Subscribe
    public void onLink(final AccountLinkedEvent event) {
        final Profile profile = profileProvider.getProfile(event.getPlayer());
        eventHooked.stream()
                .filter(obj -> obj.getService().containsProfile(profile))
                .forEach(obj -> obj.onLink(profile));
    }

    /**
     * Clears all event hooks.
     */
    public void clear() {
        eventHooked.clear();
    }
}
