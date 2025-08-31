package org.betonquest.betonquest.compatibility.npc.fancynpcs;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.betonquest.betonquest.api.bukkit.event.npc.NpcVisibilityUpdateEvent;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.feature.NpcInteractCatcher;
import org.betonquest.betonquest.kernel.registry.quest.NpcTypeRegistry;
import org.betonquest.betonquest.quest.objective.interact.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

/**
 * Catches interaction with FancyNpcs.
 */
public class FancyCatcher extends NpcInteractCatcher<Npc> {
    /**
     * Initializes the Fancy catcher.
     *
     * @param profileProvider the profile provider instance
     * @param npcTypeRegistry the registry to identify the clicked Npc
     */
    public FancyCatcher(final ProfileProvider profileProvider, final NpcTypeRegistry npcTypeRegistry) {
        super(profileProvider, npcTypeRegistry);
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

        if (interactLogic(event.getPlayer(), new FancyAdapter(event.getNpc()), interaction,
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
            new NpcVisibilityUpdateEvent(new FancyAdapter(event.getNpc())).callEvent();
        }
    }
}
