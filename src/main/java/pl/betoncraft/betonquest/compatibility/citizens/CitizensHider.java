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
package pl.betoncraft.betonquest.compatibility.citizens;

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
public class CitizensHider extends BukkitRunnable implements Listener {

    private static CitizensHider instance = null;

    final private Map<Integer, Set<ConditionID>> npcs;

    private CitizensHider() {
        super();
        npcs = new HashMap<>();
        final int updateInterval = BetonQuest.getInstance().getConfig().getInt("hidden_npcs_check_interval", 5 * 20);
        loadFromConfig();
        runTaskTimer(BetonQuest.getInstance(), 0, updateInterval);
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    /**
     * Starts (or restarts) the CitizensHider. It loads the current configuration for hidden NPCs
     */
    public static void start() {
        if (instance != null) {
            instance.stop();
        }
        instance = new CitizensHider();
    }

    /**
     * @return the currently used CitizensHider instance
     */
    public static CitizensHider getInstance() {
        return instance;
    }

    private void loadFromConfig() {

        for (final ConfigPackage cfgPackage : Config.getPackages().values()) {
            final FileConfiguration custom = cfgPackage.getCustom().getConfig();
            if (custom == null) {
                continue;
            }
            final ConfigurationSection section = custom.getConfigurationSection("hide_npcs");
            if (section == null) {
                continue;
            }
            npcs:
            for (final String npcID : section.getKeys(false)) {
                final int id;
                try {
                    id = Integer.parseInt(npcID);
                } catch (final NumberFormatException e) {
                    LogUtils.getLogger().log(Level.WARNING, "NPC ID '" + npcID + "' is not a valid number, in custom.yml hide_npcs");
                    LogUtils.logThrowable(e);
                    continue;
                }
                final Set<ConditionID> conditions = new HashSet<>();
                final String conditionsString = section.getString(npcID);

                for (final String condition : conditionsString.split(",")) {
                    try {
                        conditions.add(new ConditionID(cfgPackage, condition));
                    } catch (final ObjectNotFoundException e) {
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
     * Stops the CitizensHider, cleaning up all listeners, runnables etc.
     */
    public void stop() {
        cancel();
        HandlerList.unregisterAll(this);
    }

    /**
     * Updates the visibility of the specified NPC for this player.
     *
     * @param player the player
     * @param npcID  ID of the NPC
     */
    public void applyVisibility(final Player player, final Integer npcID) {
        boolean hidden = true;
        final Set<ConditionID> conditions = npcs.get(npcID);
        if (conditions == null || conditions.isEmpty()) {
            hidden = false;
        } else {
            for (final ConditionID condition : conditions) {
                if (!BetonQuest.condition(PlayerConverter.getID(player), condition)) {
                    hidden = false;
                    break;
                }
            }
        }

        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcID);
        if (npc == null) {
            LogUtils.getLogger().log(Level.WARNING, "CitizensHider could not update visibility for npc " + npcID + ": No npc with this id found!");
            return;
        }
        if (npc.isSpawned()) {
            final Player playerNPC = (Player)npc.getEntity();
            if (hidden) {
                player.hidePlayer(BetonQuest.getInstance(), playerNPC);
            } else {
                player.showPlayer(BetonQuest.getInstance(), playerNPC);
            }
        }
    }

    /**
     * Updates the visibility of all NPCs for this player.
     *
     * @param player the player
     */
    public void applyVisibility(final Player player) {
        for (final Integer npcID : npcs.keySet()) {
            applyVisibility(player, npcID);
        }
    }

    /**
     * Updates the visibility of this NPC for all players.
     *
     * @param npcID ID of the NPC
     */
    public void applyVisibility(final NPC npcID) {
        //check if the npc is in the default registry
        if (npcID.getOwningRegistry() != CitizensAPI.getNPCRegistry()) {
            return;
        }
        for (final Player p : Bukkit.getOnlinePlayers()) {
            applyVisibility(p, npcID.getId());
        }
    }

    /**
     * Updates the visibility of all NPCs for all players.
     */
    public void applyVisibility() {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            for (final Integer npcID : npcs.keySet()) {
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
    public boolean isInvisible(final Player player, final NPC npc) {
        return player.canSee((Player)npc.getEntity());
    }

    @EventHandler(ignoreCancelled = true)
    public void onNPCSpawn(final NPCSpawnEvent event) {
        applyVisibility(event.getNPC());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        applyVisibility(event.getPlayer());
    }
}
