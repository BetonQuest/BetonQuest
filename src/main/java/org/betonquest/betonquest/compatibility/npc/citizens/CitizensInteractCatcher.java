package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensReloadEvent;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.event.NPCTeleportEvent;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.bukkit.event.npc.NpcVisibilityUpdateEvent;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.feature.NpcInteractCatcher;
import org.betonquest.betonquest.compatibility.npc.citizens.event.move.CitizensMoveController;
import org.betonquest.betonquest.kernel.registry.quest.NpcTypeRegistry;
import org.betonquest.betonquest.quest.objective.interact.Interaction;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

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
     * @param profileProvider        the profile provider instance
     * @param npcTypeRegistry        the registry to identify the clicked Npc
     * @param citizensMoveController the move controller to check if the NPC currently blocks conversations
     */
    public CitizensInteractCatcher(final ProfileProvider profileProvider, final NpcTypeRegistry npcTypeRegistry, final CitizensMoveController citizensMoveController) {
        super(profileProvider, npcTypeRegistry);
        this.citizensMoveController = citizensMoveController;
    }

    private void interactLogic(final NPCClickEvent event, final Interaction interaction) {
        final NPC npc = event.getNPC();
        if (!npc.getOwningRegistry().equals(CitizensAPI.getNPCRegistry())) {
            return;
        }
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
        interactLogic(event, Interaction.RIGHT);
    }

    /**
     * Handles left click.
     *
     * @param event the event to handle
     */
    @EventHandler(ignoreCancelled = true)
    public void onNPCClick(final NPCLeftClickEvent event) {
        interactLogic(event, Interaction.LEFT);
    }

    /**
     * Update the hologram when the plugin reloads.
     *
     * @param event The event.
     */
    @EventHandler
    public void onCitizensReload(final CitizensReloadEvent event) {
        Bukkit.getPluginManager().callEvent(new NpcVisibilityUpdateEvent(null));
    }

    /**
     * Update the hologram when the NPC spawns.
     *
     * @param event The event.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCSpawn(final NPCSpawnEvent event) {
        updateHologram(event.getNPC());
    }

    /**
     * Update the hologram when the NPC despawns.
     *
     * @param event The event.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCDespawn(final NPCDespawnEvent event) {
        updateHologram(event.getNPC());
    }

    /**
     * Update the hologram when the NPC moves.
     *
     * @param event The event.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCTeleport(final NPCTeleportEvent event) {
        updateHologram(event.getNPC());
    }

    private void updateHologram(final NPC npc) {
        if (npc.getOwningRegistry().equals(CitizensAPI.getNPCRegistry())) {
            Bukkit.getPluginManager().callEvent(new NpcVisibilityUpdateEvent(new CitizensAdapter(npc)));
        }
    }
}
