package pl.betoncraft.betonquest.compatibility.citizens;

import net.citizensnpcs.api.event.CitizensReloadEvent;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.conversation.CombatTagger;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Starts new conversations with NPCs
 */
public class CitizensListener implements Listener {

    private RightClickListener rightClick;
    private LeftClickListener leftClick;

    /**
     * Initializes the listener
     */
    public CitizensListener() {
        reload();
    }

    public void reload() {
        if (rightClick != null) {
            HandlerList.unregisterAll(rightClick);
        }
        if (leftClick != null) {
            HandlerList.unregisterAll(leftClick);
        }


        final BetonQuest plugin = BetonQuest.getInstance();

        rightClick = new RightClickListener();
        Bukkit.getPluginManager().registerEvents(rightClick, plugin);

        if (plugin.getConfig().getBoolean("acceptNPCLeftClick")) {
            leftClick = new LeftClickListener();
            Bukkit.getPluginManager().registerEvents(leftClick, plugin);
        }
    }

    private class RightClickListener implements Listener {

        public RightClickListener() {
        }

        @EventHandler
        public void onNPCClick(final NPCRightClickEvent event) {
            interactLogic(event);
        }
    }

    private class LeftClickListener implements Listener {

        public LeftClickListener() {
        }

        @EventHandler
        public void onNPCClick(final NPCLeftClickEvent event) {
            interactLogic(event);
        }
    }

    public void interactLogic(final NPCClickEvent event) {
        if (!event.getClicker().hasPermission("betonquest.conversation")) {
            return;
        }
        if (NPCMoveEvent.blocksTalking(event.getNPC())) {
            return;
        }
        final String playerID = PlayerConverter.getID(event.getClicker());
        if (CombatTagger.isTagged(playerID)) {
            Config.sendNotify(null, playerID, "busy", "busy,error");
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
