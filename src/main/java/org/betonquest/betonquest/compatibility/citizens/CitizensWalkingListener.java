package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.bukkit.event.PlayerConversationEndEvent;
import org.betonquest.betonquest.api.bukkit.event.PlayerConversationStartEvent;
import org.betonquest.betonquest.api.quest.npc.conversation.NpcConversation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * Prevents Citizens NPCs from walking around when in conversation with the
 * player.
 */
@SuppressWarnings("PMD.CommentRequired")
public class CitizensWalkingListener implements Listener {

    @SuppressWarnings("NullAway.Init")
    private static CitizensWalkingListener instance;

    private final Map<NPC, Pair<Integer, Location>> npcs = new HashMap<>();

    /**
     * Creates new listener which prevents Citizens NPCs from walking around
     * when in conversation.
     */
    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    public CitizensWalkingListener() {
        instance = this;
        Bukkit.getServer().getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    /**
     * @return the currently active WalkingListener or null if citizens isn't hooked
     */
    public static CitizensWalkingListener getInstance() {
        return instance;
    }

    @EventHandler(ignoreCancelled = true)
    public void onConversationStart(final PlayerConversationStartEvent event) {
        if (event.getConversation() instanceof final NpcConversation<?> npcConv
                && npcConv.getNPC().getOriginal() instanceof final NPC npc) {
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

    @SuppressWarnings("PMD.CognitiveComplexity")
    @EventHandler(ignoreCancelled = true)
    public void onConversationEnd(final PlayerConversationEndEvent event) {
        if (event.getConversation() instanceof final NpcConversation<?> npcConv
                && npcConv.getNPC().getOriginal() instanceof final NPC npc) {
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
     * @param npc a npc to check for
     * @return true if the movement of the npc is paused because of a player talking with the npc
     */
    public boolean isMovementPaused(final NPC npc) {
        return npcs.containsKey(npc);
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
        npcs.computeIfPresent(npc, (k, pair) -> Pair.of(pair.getKey(), location));
    }
}
