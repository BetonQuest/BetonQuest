package org.betonquest.betonquest.api.quest.npc.feature;

import org.betonquest.betonquest.api.NpcInteractEvent;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.objective.EntityInteractObjective.Interaction;
import org.betonquest.betonquest.quest.registry.type.NpcTypeRegistry;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Bukkit;
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
     * Processor to get Npc identifier from it.
     */
    private final NpcTypeRegistry npcTypeRegistry;

    /**
     * Initializes the conversation starter. // TODO rephrase
     *
     * @param npcTypeRegistry the registry to identify the clicked Npc
     */
    public NpcInteractCatcher(final NpcTypeRegistry npcTypeRegistry) {
        this.npcTypeRegistry = npcTypeRegistry;
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
        final OnlineProfile profile = PlayerConverter.getID(clicker);
        final Set<NpcID> identifier = npcTypeRegistry.getIdentifier(npc);
        final NpcInteractEvent npcInteractEvent = new NpcInteractEvent(profile, clicker, npc, identifier, interaction, isAsync);
        if (cancelled) {
            npcInteractEvent.setCancelled(true);
        }
        Bukkit.getPluginManager().callEvent(npcInteractEvent);
        return npcInteractEvent.isCancelled();
    }
}
