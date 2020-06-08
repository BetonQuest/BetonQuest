/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.compatibility.protocollib;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * @author Namnodorel
 * @author Jakub Sapalski
 */
public class NPCHider extends BukkitRunnable implements Listener {

    private static NPCHider instance = null;

    private EntityHider hider;
    private Map<Integer, Set<ConditionID>> npcs;
    private Integer updateInterval;

    private NPCHider() {
        npcs = new HashMap<>();
        updateInterval = BetonQuest.getInstance().getConfig().getInt("hidden_npcs_check_interval", 5 * 20);
        hider = new EntityHider(BetonQuest.getInstance(), EntityHider.Policy.BLACKLIST);
        loadFromConfig();
        runTaskTimer(BetonQuest.getInstance(), 0, updateInterval);
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    /**
     * Starts (or restarts) the NPCHider. It loads the current configuration for hidden NPCs
     */
    public static void start() {
        if (instance != null) {
            instance.stop();
        }
        instance = new NPCHider();
    }

    /**
     * @return the currently used NPCHider instance
     */
    public static NPCHider getInstance() {
        return instance;
    }

    private void loadFromConfig() {

        for (ConfigPackage cfgPackage : Config.getPackages().values()) {
            FileConfiguration custom = cfgPackage.getCustom().getConfig();
            if (custom == null) {
                continue;
            }
            ConfigurationSection section = custom.getConfigurationSection("hide_npcs");
            if (section == null) {
                continue;
            }
            npcs:
            for (String npcID : section.getKeys(false)) {
                int id;
                try {
                    id = Integer.parseInt(npcID);
                } catch (NumberFormatException e) {
                    LogUtils.getLogger().log(Level.WARNING, "NPC ID '" + npcID + "' is not a valid number, in custom.yml hide_npcs");
                    LogUtils.logThrowable(e);
                    continue npcs;
                }
                Set<ConditionID> conditions = new HashSet<>();
                String conditionsString = section.getString(npcID);

                for (String condition : conditionsString.split(",")) {
                    try {
                        conditions.add(new ConditionID(cfgPackage, condition));
                    } catch (ObjectNotFoundException e) {
                        LogUtils.getLogger().log(Level.WARNING, "Condition '" + condition +
                                "' does not exist, in custom.yml hide_npcs with ID " + npcID);
                        LogUtils.logThrowable(e);
                        continue npcs;
                    }
                }

                if (npcs.containsKey(id)) {
                    npcs.get(id).addAll(conditions);
                } else {
                    npcs.put(id, conditions);
                }
            }
        }

    }

    @Override
    public void run() {
        applyVisibility();
    }

    /**
     * Stops the NPCHider, cleaning up all listeners, runnables etc.
     */
    public void stop() {
        hider.close();
        cancel();
        HandlerList.unregisterAll(this);
    }

    /**
     * Updates the visibility of the specified NPC for this player.
     *
     * @param player the player
     * @param npcID  ID of the NPC
     */
    public void applyVisibility(Player player, Integer npcID) {
        boolean hidden = true;
        Set<ConditionID> conditions = npcs.get(npcID);
        if (conditions == null || conditions.isEmpty()) {
            hidden = false;
        } else {
            for (ConditionID condition : conditions) {
                if (!BetonQuest.condition(PlayerConverter.getID(player), condition)) {
                    hidden = false;
                    break;
                }
            }
        }

        NPC npc = CitizensAPI.getNPCRegistry().getById(npcID);
        if (npc == null) {
            LogUtils.getLogger().log(Level.WARNING, "NPCHider could not update visibility for npc " + npcID + ": No npc with this id found!");
            return;
        }
        if (npc.isSpawned()) {
            if (hidden) {
                hider.hideEntity(player, npc.getEntity());
            } else {
                hider.showEntity(player, npc.getEntity());
            }
        }
    }

    /**
     * Updates the visibility of all NPCs for this player.
     *
     * @param player the player
     */
    public void applyVisibility(Player player) {
        for (Integer npcID : npcs.keySet()) {
            applyVisibility(player, npcID);
        }
    }

    /**
     * Updates the visibility of this NPC for all players.
     *
     * @param npcID ID of the NPC
     */
    public void applyVisibility(NPC npcID) {
        //check if the npc is in the default registry
        if (npcID.getOwningRegistry() != CitizensAPI.getNPCRegistry()) {
            System.out.println("X");
            return;
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            applyVisibility(p, npcID.getId());
        }
    }

    /**
     * Updates the visibility of all NPCs for all players.
     */
    public void applyVisibility() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (Integer npcID : npcs.keySet()) {
                applyVisibility(p, npcID);
            }
        }
    }

    /**
     * Checks whenever the NPC is visible to the player.
     *
     * @param player the player
     * @param npc    ID of the NPC
     * @return true if the NPC is visible to that player, false otherwise
     */
    public boolean isInvisible(Player player, NPC npc) {
        return !hider.isVisible(player, npc.getEntity().getEntityId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onNPCSpawn(NPCSpawnEvent event) {
        applyVisibility(event.getNPC());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        applyVisibility(event.getPlayer());
    }
}
