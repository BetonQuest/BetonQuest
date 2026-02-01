package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.event.CitizensReloadEvent;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.event.NPCTeleportEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.betonquest.betonquest.api.bukkit.event.npc.NpcVisibilityUpdateEvent;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.NpcInteractCatcher;
import org.betonquest.betonquest.api.quest.npc.NpcRegistry;
import org.betonquest.betonquest.quest.objective.interact.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

import java.util.function.Predicate;

/**
 * Catches Citizens NPC interactions and adapts them into the BetonQuest event.
 */
public class CitizensInteractCatcher extends NpcInteractCatcher<NPC> {

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Source Registry of NPCs to consider.
     */
    private final NPCRegistry registry;

    /**
     * Move Controller to check if the NPC blocks conversations while moving.
     */
    private final Predicate<NPC> cancelPredicate;

    /**
     * Initializes the catcher for Citizens.
     *
     * @param plugin          the plugin instance
     * @param profileProvider the profile provider instance
     * @param npcRegistry     the registry to identify the clicked Npc
     * @param registry        the registry of NPCs to notice interactions
     * @param cancelPredicate the move predicate to check if the NPC currently blocks conversations
     *                        if the predicate test yields 'true' the adapted event will be fired cancelled
     */
    public CitizensInteractCatcher(final Plugin plugin, final ProfileProvider profileProvider, final NpcRegistry npcRegistry,
                                   final NPCRegistry registry, final Predicate<NPC> cancelPredicate) {
        super(profileProvider, npcRegistry);
        this.plugin = plugin;
        this.registry = registry;
        this.cancelPredicate = cancelPredicate;
    }

    private void interactLogic(final NPCClickEvent event, final Interaction interaction) {
        final NPC npc = event.getNPC();
        if (!npc.getOwningRegistry().equals(registry)) {
            return;
        }
        if (super.interactLogic(event.getClicker(), new CitizensAdapter(plugin, npc), interaction,
                cancelPredicate.test(npc), event.isAsynchronous())) {
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
        new NpcVisibilityUpdateEvent(null).callEvent();
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
        if (npc.getOwningRegistry().equals(registry)) {
            new NpcVisibilityUpdateEvent(new CitizensAdapter(plugin, npc)).callEvent();
        }
    }
}
