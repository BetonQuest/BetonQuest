package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.bukkit.event.PlayerConversationEndEvent;
import org.betonquest.betonquest.api.bukkit.event.PlayerConversationStartEvent;
import org.betonquest.betonquest.api.quest.npc.feature.NpcConversation;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * Prevents Citizens NPCs from walking around when in conversation with the player.
 */
public class CitizensWalkingListener implements Listener {

    /**
     * Source Registry of NPCs to use.
     */
    public final NPCRegistry registry;

    /**
     * Map of NPCs which are currently in walking, and their locations to walk to.
     */
    private final Map<NPC, Pair<Integer, Location>> npcs = new HashMap<>();

    /**
     * Creates new listener which prevents Citizens NPCs from walking around when in conversation.
     *
     * @param registry the registry of NPCs to use
     */
    public CitizensWalkingListener(final NPCRegistry registry) {
        this.registry = registry;
    }

    /**
     * Called when a player starts a conversation with an NPC.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onConversationStart(final PlayerConversationStartEvent event) {
        if (event.getConversation() instanceof final NpcConversation<?> npcConv
                && npcConv.getNPC().getOriginal() instanceof final NPC npc
                && npc.getOwningRegistry().equals(registry)) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    if (npcs.containsKey(npc)) {
                        npcs.computeIfPresent(npc, (k, pair) -> Pair.of(pair.getKey() + 1, pair.getValue()));
                    } else {
                        final Navigator nav = npc.getNavigator();
                        if (nav.isNavigating()) {
                            npcs.put(npc, Pair.of(1, nav.getTargetAsLocation()));
                            nav.setPaused(true);
                            nav.cancelNavigation();
                            nav.setTarget(npc.getEntity().getLocation());
                            nav.setPaused(true);
                            nav.cancelNavigation();
                        }
                    }
                }
            }.runTask(BetonQuest.getInstance());
        }
    }

    /**
     * Called when a player ends a conversation with an NPC.
     *
     * @param event the event
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    @EventHandler(ignoreCancelled = true)
    public void onConversationEnd(final PlayerConversationEndEvent event) {
        if (event.getConversation() instanceof final NpcConversation<?> npcConv
                && npcConv.getNPC().getOriginal() instanceof final NPC npc
                && npc.getOwningRegistry().equals(registry)) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    if (npcs.containsKey(npc)) {
                        final Pair<Integer, Location> pair = npcs.get(npc);
                        final int conversationsAmount = pair.getKey() - 1;
                        if (conversationsAmount == 0) {
                            npcs.remove(npc);
                            if (npc.isSpawned()) {
                                final Navigator nav = npc.getNavigator();
                                nav.setPaused(false);
                                nav.setTarget(pair.getValue());
                            } else {
                                npc.spawn(pair.getValue(), SpawnReason.PLUGIN);
                            }
                        } else {
                            npcs.put(npc, Pair.of(conversationsAmount, pair.getValue()));
                        }
                    }
                }
            }.runTask(BetonQuest.getInstance());
        }
    }

    /**
     * Check if the npc is in a conversation and thus should not be moved.
     *
     * @param npc a npc to check for
     * @return true if the movement of the npc is paused because of a player talking with the npc
     */
    public boolean isMovementPaused(final NPC npc) {
        return npc.getOwningRegistry().equals(registry) && npcs.containsKey(npc);
    }

    /**
     * Sets a new target location to which the npc should move when the conversations end.
     * <p>
     * Check {@link #isMovementPaused(NPC)} before to make sure the npcs movement is currently paused
     *
     * @param npc      a npc
     * @param location the location to which the npc should move
     */
    public void setNewTargetLocation(final NPC npc, final Location location) {
        if (npc.getOwningRegistry().equals(registry)) {
            npcs.computeIfPresent(npc, (k, pair) -> Pair.of(pair.getKey(), location));
        }
    }
}
