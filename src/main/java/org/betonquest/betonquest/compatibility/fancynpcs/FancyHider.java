package org.betonquest.betonquest.compatibility.fancynpcs;

import de.oliver.fancynpcs.api.events.NpcSpawnEvent;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.feature.NpcHider;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.kernel.registry.quest.NpcTypeRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Set;

/**
 * Prevents respawning of hidden Npcs.
 */
public class FancyHider implements Listener {

    /**
     * Profile Provider for Players.
     */
    private final ProfileProvider profileProvider;

    /**
     * Npc Hider to check current status of Npcs.
     */
    private final NpcHider npcHider;

    /**
     * Npc Registry to get Ids from Npcs.
     */
    private final NpcTypeRegistry npcTypeRegistry;

    /**
     * Create a new Fancy Hider to force Npc hiding.
     *
     * @param profileProvider the profile provider for player profiles
     * @param npcHider        the npc hider to check if Npc is hidden
     * @param npcTypeRegistry the registry to get Npcs from Ids
     */
    public FancyHider(final ProfileProvider profileProvider, final NpcHider npcHider, final NpcTypeRegistry npcTypeRegistry) {
        this.profileProvider = profileProvider;
        this.npcHider = npcHider;
        this.npcTypeRegistry = npcTypeRegistry;
    }

    private boolean isHidden(final Npc<?> npc, final Player player) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(player);
        final Set<NpcID> identifier = npcTypeRegistry.getIdentifier(npc, onlineProfile);
        if (identifier.isEmpty()) {
            return false;
        }
        for (final NpcID npcID : identifier) {
            if (npcHider.isHidden(npcID, onlineProfile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Cancels npc sending to player if the hide conditions are met.
     *
     * @param event the spawn event to listen
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSpawn(final NpcSpawnEvent event) {
        if (isHidden(new FancyAdapter(event.getNpc()), event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
