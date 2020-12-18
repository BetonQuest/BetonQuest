package pl.betoncraft.betonquest.compatibility.citizens;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.PlayerConversationEndEvent;
import pl.betoncraft.betonquest.api.PlayerConversationStartEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Prevents Citizens NPCs from walking around when in conversation with the
 * player
 */
@SuppressWarnings("PMD.CommentRequired")
public class CitizensWalkingListener implements Listener {

    private static CitizensWalkingListener instance;

    private final Map<NPC, Integer> npcs = new HashMap<>();
    private final Map<NPC, Location> locs = new HashMap<>();

    /**
     * Creates new listener which prevents Citizens NPCs from walking around
     * when in conversation
     */
    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
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
        if (event.isCancelled()) {
            return;
        }

        if (event.getConversation() instanceof CitizensConversation) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    final CitizensConversation conv = (CitizensConversation) event.getConversation();
                    final NPC npc = conv.getNPC();
                    if (npcs.containsKey(npc)) {
                        npcs.put(npc, npcs.get(npc) + 1);
                    } else {
                        final Navigator nav = npc.getNavigator();
                        npcs.put(npc, 1);
                        locs.put(npc, nav.getTargetAsLocation());
                        nav.setPaused(true);
                        nav.cancelNavigation();
                        nav.setTarget(conv.getNPC().getEntity().getLocation());
                        nav.setPaused(true);
                        nav.cancelNavigation();
                    }
                }
            }.runTask(BetonQuest.getInstance());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onConversationEnd(final PlayerConversationEndEvent event) {
        if (event.getConversation() instanceof CitizensConversation) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    final CitizensConversation conv = (CitizensConversation) event.getConversation();
                    final NPC npc = conv.getNPC();
                    Integer npcId = npcs.get(npc);
                    npcId--;
                    if (npcId == 0) {
                        npcs.remove(npc);
                        if (npc.isSpawned()) {
                            final Navigator nav = npc.getNavigator();
                            nav.setPaused(false);
                            nav.setTarget(locs.remove(npc));
                        }
                    } else {
                        npcs.put(npc, npcId);
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
     * Sets a new target location to which the npc should move when the conversations end
     * <p>
     * Check {@link #isMovementPaused(NPC)} before to make sure the npcs movement is currently paused
     *
     * @param npc      a npc
     * @param location the location to which the npc should move
     */
    public void setNewTargetLocation(final NPC npc, final Location location) {
        locs.put(npc, location);
    }

}
