package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.event.CitizensReloadEvent;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.event.NPCTeleportEvent;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.bukkit.event.npc.NpcExternalVisibilityChange;
import org.betonquest.betonquest.api.quest.npc.feature.NpcInteractCatcher;
import org.betonquest.betonquest.compatibility.citizens.event.move.CitizensMoveController;
import org.betonquest.betonquest.objective.EntityInteractObjective;
import org.betonquest.betonquest.quest.registry.type.NpcTypeRegistry;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

/**
 * Catches Citizens NPC interactions and adapts them into the BetonQuest event.
 */
public class CitizensInteractCatcher extends NpcInteractCatcher<NPC> {
    /**
     * Move Controller to check if the NPC blocks conversations while moving.
     */
    private final CitizensMoveController citizensMoveController;

    /**
     * Initializes the catcher for Citizens.
     *
     * @param npcTypeRegistry        the registry to identify the clicked Npc
     * @param citizensMoveController the move controller to check if the NPC currently blocks conversations
     */
    public CitizensInteractCatcher(final NpcTypeRegistry npcTypeRegistry, final CitizensMoveController citizensMoveController) {
        super(npcTypeRegistry);
        this.citizensMoveController = citizensMoveController;
    }

    private void interactLogic(final NPCClickEvent event, final EntityInteractObjective.Interaction interaction) {
        final NPC npc = event.getNPC();
        if (super.interactLogic(event.getClicker(), new CitizensAdapter(npc), interaction,
                citizensMoveController.blocksTalking(npc), event.isAsynchronous())) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles right clicks.
     *
     * @param event the event to handle
     */
    @EventHandler(ignoreCancelled = true)
    public void onNPCClick(final NPCRightClickEvent event) {
        interactLogic(event, EntityInteractObjective.Interaction.RIGHT);
    }

    /**
     * Handles left click.
     *
     * @param event the event to handle
     */
    @EventHandler(ignoreCancelled = true)
    public void onNPCClick(final NPCLeftClickEvent event) {
        interactLogic(event, EntityInteractObjective.Interaction.LEFT);
    }

    /**
     * Update the hologram when the plugin reloads.
     *
     * @param event The event.
     */
    @EventHandler
    public void onCitizensReload(final CitizensReloadEvent event) {
        Bukkit.getPluginManager().callEvent(new NpcExternalVisibilityChange(null));
    }

    /**
     * Update the hologram when the NPC spawns.
     *
     * @param event The event.
     */
    @EventHandler
    public void onNPCSpawn(final NPCSpawnEvent event) {
        updateHologram(event.getNPC());
    }

    /**
     * Update the hologram when the NPC despawns.
     *
     * @param event The event.
     */
    @EventHandler
    public void onNPCDespawn(final NPCDespawnEvent event) {
        updateHologram(event.getNPC());
    }

    /**
     * Update the hologram when the NPC moves.
     *
     * @param event The event.
     */
    @EventHandler
    public void onNPCTeleport(final NPCTeleportEvent event) {
        updateHologram(event.getNPC());
    }

    private void updateHologram(final NPC npc) {
        Bukkit.getPluginManager().callEvent(new NpcExternalVisibilityChange(new CitizensAdapter(npc)));
    }
}
