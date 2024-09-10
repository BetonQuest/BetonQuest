package org.betonquest.betonquest.api.quest.npc.conversation;

import org.betonquest.betonquest.BetonQuest;
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
 * Calls {@link NpcInteractEvent}s with supplied listeners.
 *
 * @param <T> the original npc type
 */
public abstract class NpcInteractCatcher<T> {
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
     * @param plugin     the plugin to register listener and load config
     * @param npcFactory the factory to identify the clicked Npc
     */
    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public NpcInteractCatcher(final BetonQuest plugin, final NpcFactory<T> npcFactory) {
        this.npcFactory = npcFactory;
        Bukkit.getPluginManager().registerEvents(getClickListener(), plugin);
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
     * @return if the Npc interaction is cancelled and the source should cancel too
     */
    protected boolean interactLogic(final Player clicker, final Npc<T> npc, final Interaction interaction,
                                    final boolean cancelled) {
        final OnlineProfile profile = PlayerConverter.getID(clicker);
        final String identifier = prefix + " " + npcFactory.npcToInstructionString(npc);
        final NpcInteractEvent npcInteractEvent = new NpcInteractEvent(profile, clicker, npc, identifier, interaction);
        if (cancelled) {
            npcInteractEvent.setCancelled(true);
        }
        Bukkit.getPluginManager().callEvent(npcInteractEvent);
        return npcInteractEvent.isCancelled();
    }

    /**
     * Gets a listener to get interactions with a Npc.
     * The listener has to catch original interactions and
     * redirect them to {@link #interactLogic(Player, Npc, Interaction, boolean)}.
     *
     * @return a new click listener
     */
    protected abstract Listener getClickListener();

    /**
     * Sets the prefix this starter uses to build the full instruction.
     *
     * @param prefix the prefix used to identify the used factory
     */
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
}
