package org.betonquest.betonquest.compatibility.npc.znpcsplus;

import lol.pyr.znpcsplus.api.event.NpcInteractEvent;
import lol.pyr.znpcsplus.api.interaction.InteractionType;
import lol.pyr.znpcsplus.api.npc.NpcEntry;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.feature.NpcInteractCatcher;
import org.betonquest.betonquest.kernel.registry.quest.NpcTypeRegistry;
import org.betonquest.betonquest.quest.objective.interact.Interaction;
import org.bukkit.event.EventHandler;

/**
 * Catches interaction with ZNPCsPlus Npcs.
 */
public class ZNPCsPlusCatcher extends NpcInteractCatcher<NpcEntry> {
    /**
     * Initializes the ZNpsPlus catcher.
     *
     * @param profileProvider the profile provider instance
     * @param npcTypeRegistry the registry to identify the clicked Npc
     */
    public ZNPCsPlusCatcher(final ProfileProvider profileProvider, final NpcTypeRegistry npcTypeRegistry) {
        super(profileProvider, npcTypeRegistry);
    }

    /**
     * Catches Npc interactions.
     *
     * @param event the Npc interact event
     */
    @EventHandler(ignoreCancelled = true)
    public void onClick(final NpcInteractEvent event) {
        final Interaction interaction;
        if (event.getClickType() == InteractionType.LEFT_CLICK) {
            interaction = Interaction.LEFT;
        } else if (event.getClickType() == InteractionType.RIGHT_CLICK) {
            interaction = Interaction.RIGHT;
        } else {
            return;
        }

        if (interactLogic(event.getPlayer(), new ZNPCsPlusAdapter(event.getEntry()), interaction,
                false, event.isAsynchronous())) {
            event.setCancelled(true);
        }
    }
}
