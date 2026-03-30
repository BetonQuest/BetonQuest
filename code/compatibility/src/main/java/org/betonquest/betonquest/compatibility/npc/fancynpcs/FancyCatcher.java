package org.betonquest.betonquest.compatibility.npc.fancynpcs;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.betonquest.betonquest.api.bukkit.event.npc.NpcVisibilityUpdateEvent;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.NpcInteractCatcher;
import org.betonquest.betonquest.api.service.npc.NpcRegistry;
import org.betonquest.betonquest.quest.objective.interact.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

/**
 * Catches interaction with FancyNpcs.
 */
public class FancyCatcher extends NpcInteractCatcher<Npc> {

    /**
     * The plugin instance to run tasks on.
     */
    private final Plugin plugin;

    /**
     * Initializes the Fancy catcher.
     *
     * @param plugin          the plugin instance to run tasks on
     * @param profileProvider the profile provider instance
     * @param npcRegistry     the registry to identify the clicked Npc
     */
    public FancyCatcher(final Plugin plugin, final ProfileProvider profileProvider, final NpcRegistry npcRegistry) {
        super(profileProvider, npcRegistry);
        this.plugin = plugin;
    }

    /**
     * Catches clicks.
     *
     * @param event the Interact Event
     */
    @EventHandler(ignoreCancelled = true)
    public void onclick(final NpcInteractEvent event) {
        final Interaction interaction;
        if (event.getInteractionType() == ActionTrigger.LEFT_CLICK) {
            interaction = Interaction.LEFT;
        } else if (event.getInteractionType() == ActionTrigger.RIGHT_CLICK) {
            interaction = Interaction.RIGHT;
        } else {
            return;
        }

        if (interactLogic(event.getPlayer(), new FancyAdapter(plugin, event.getNpc()), interaction,
                false, event.isAsynchronous())) {
            event.setCancelled(true);
        }
    }

    /**
     * Update the Npc holograms when the Npc moves.
     *
     * @param event The event.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLocationChange(final NpcModifyEvent event) {
        if (event.getModification() == NpcModifyEvent.NpcModification.LOCATION) {
            new NpcVisibilityUpdateEvent(new FancyAdapter(plugin, event.getNpc())).callEvent();
        }
    }
}
