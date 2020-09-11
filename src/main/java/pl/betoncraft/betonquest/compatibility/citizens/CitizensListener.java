package pl.betoncraft.betonquest.compatibility.citizens;

import net.citizensnpcs.api.event.CitizensReloadEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.conversation.CombatTagger;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Starts new conversations with NPCs
 */
public class CitizensListener implements Listener {

    /**
     * Initializes the listener
     */
    public CitizensListener() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onNPCClick(final NPCRightClickEvent event) {
        if (!event.getClicker().hasPermission("betonquest.conversation")) {
            return;
        }
        if (NPCMoveEvent.blocksTalking(event.getNPC())) {
            return;
        }
        final String playerID = PlayerConverter.getID(event.getClicker());
        if (CombatTagger.isTagged(playerID)) {
            Config.sendNotify(playerID, "busy", "busy,error");
            return;
        }
        final String npcId = String.valueOf(event.getNPC().getId());
        String assignment = Config.getNpc(npcId);
        if (Config.getString("config.citizens_npcs_by_name").equalsIgnoreCase("true")) {
            if (assignment == null) {
                assignment = Config.getNpc(event.getNPC().getName());
            }
        }
        if (assignment != null) {
            event.setCancelled(true);
            new CitizensConversation(playerID, assignment, event.getNPC().getEntity().getLocation(),
                    event.getNPC());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCitizensReload(final CitizensReloadEvent event) {
        CitizensHologram.reload();
    }
}
