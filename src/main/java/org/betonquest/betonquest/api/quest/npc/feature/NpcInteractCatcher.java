package org.betonquest.betonquest.api.quest.npc.feature;

import org.betonquest.betonquest.api.bukkit.event.npc.NpcInteractEvent;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.api.quest.npc.NpcRegistry;
import org.betonquest.betonquest.quest.objective.interact.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Set;

/**
 * Catches interaction of Third Party Npcs and calls them as {@link NpcInteractEvent}s.
 * <p>
 * The listener methods needs to be created and the {@linkplain #interactLogic} called in it.
 * The Integration also needs to register it as Listener.
 *
 * @param <T> the original npc type
 */
public abstract class NpcInteractCatcher<T> implements Listener {

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Processor to get Npc identifier from it.
     */
    private final NpcRegistry npcRegistry;

    /**
     * Create a new Interaction catcher.
     *
     * @param profileProvider the profile provider instance
     * @param npcRegistry     the registry to identify the clicked Npc
     */
    public NpcInteractCatcher(final ProfileProvider profileProvider, final NpcRegistry npcRegistry) {
        this.profileProvider = profileProvider;
        this.npcRegistry = npcRegistry;
    }

    /**
     * Calls a {@link NpcInteractEvent} to provide the interaction into BetonQuest.
     * <p>
     * Conversations will and objectives may cancel the event when matching.
     *
     * @param clicker     the player who clicked the Npc
     * @param npc         the supplier for lazy instantiation when the Npc is needed
     * @param interaction the type of interaction with the npc
     * @param cancelled   if the event should be fired in already cancelled state
     * @param isAsync     if the calling event is async
     * @return if the Npc interaction is cancelled and the source should cancel too
     */
    protected boolean interactLogic(final Player clicker, final Npc<T> npc, final Interaction interaction,
                                    final boolean cancelled, final boolean isAsync) {
        final OnlineProfile profile = profileProvider.getProfile(clicker);
        final Set<NpcID> identifier = npcRegistry.getIdentifier(npc, profile);
        final NpcInteractEvent npcInteractEvent = new NpcInteractEvent(profile, npc, identifier, interaction, isAsync);
        if (cancelled) {
            npcInteractEvent.setCancelled(true);
        }
        npcInteractEvent.callEvent();
        return npcInteractEvent.isCancelled();
    }
}
