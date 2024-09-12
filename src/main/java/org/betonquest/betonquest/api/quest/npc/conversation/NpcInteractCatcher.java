package org.betonquest.betonquest.api.quest.npc.conversation;

import org.betonquest.betonquest.api.NpcInteractEvent;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.objective.EntityInteractObjective.Interaction;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

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
     * Factory to identify the clicked Npc.
     */
    private final NpcFactory<T> npcFactory;

    /**
     * Prefix to identify the used factory.
     */
    private String prefix = "";

    /**
     * Initializes the conversation starter.
     *
     * @param npcFactory the factory to identify the clicked Npc
     */
    public NpcInteractCatcher(final NpcFactory<T> npcFactory) {
        this.npcFactory = npcFactory;
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
        final String identifier = prefix + " " + npcFactory.npcToInstructionString(npc);
        final NpcInteractEvent npcInteractEvent = new NpcInteractEvent(profile, clicker, npc, identifier, interaction, isAsync);
        if (cancelled) {
            npcInteractEvent.setCancelled(true);
        }
        Bukkit.getPluginManager().callEvent(npcInteractEvent);
        return npcInteractEvent.isCancelled();
    }

    /**
     * Sets the prefix this starter uses to build the full instruction.
     *
     * @param prefix the prefix used to identify the used factory
     */
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
}
