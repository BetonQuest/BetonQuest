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

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.ConditionID;
import pl.betoncraft.betonquest.ObjectNotFoundException;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays a hologram relative to an npc
 * <p>
 * Some care is taken to optimize how holograms are displayed. They are destroyed when not needed, shared between players and
 * we only have a fast update when needed to ensure they are relative to the NPC position
 */

public class CitizensHologram extends BukkitRunnable implements Listener {

    private static CitizensHologram instance;

    // All NPC's with config
    private Map<NPC, List<NPCHologram>> npcs = new HashMap<>();

    private int interval;
    private boolean enabled;

    // Updater
    private BukkitRunnable updater;

    public CitizensHologram() {
        instance = this;

        // Start this when all plugins loaded
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BetonQuest.getInstance().getJavaPlugin(), () -> {
            // loop across all packages
            for (ConfigPackage pack : Config.getPackages().values()) {

                // load all NPC's
                for (String npcID : pack.getMain().getConfig().getConfigurationSection("npcs").getKeys(false)) {
                    try {
                        NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(npcID));
                        if (npc != null) {
                            npcs.put(npc, new ArrayList<>());
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }

                // npc_holograms contains all holograms for NPCs
                ConfigurationSection section = pack.getCustom().getConfig().getConfigurationSection("npc_holograms");

                // if it's not defined then we're not displaying holograms
                if (section == null) {
                    continue;
                }
                // there's a setting to disable npc holograms altogether
                if ("true".equalsIgnoreCase(section.getString("disabled"))) {
                    return;
                }

                // load the condition check interval
                interval = section.getInt("check_interval", 100);
                if (interval <= 0) {
                    Debug.error("Could not load npc holograms of package " + pack.getName() + ": " +
                            "Check interval must be bigger than 0.");
                    return;
                }

                // loading hologram config
                for (String key : section.getKeys(false)) {
                    ConfigurationSection settings = section.getConfigurationSection(key);

                    // if the key is not a configuration section then it's not a hologram
                    if (settings == null) {
                        continue;
                    }

                    HologramConfig hologramConfig = new HologramConfig();

                    try {
                        String[] vectorParts = settings.getString("vector", "0;3;0").split(";");
                        hologramConfig.vector = new Vector(
                                Double.parseDouble(vectorParts[0]),
                                Double.parseDouble(vectorParts[1]),
                                Double.parseDouble(vectorParts[2])
                        );
                    } catch (NumberFormatException e) {
                        Debug.error(pack.getName() + ": Invalid vector: " + settings.getString("vector"));
                        continue;
                    }

                    // load all conditions
                    hologramConfig.conditions = new ArrayList<>();
                    String rawConditions = settings.getString("conditions");
                    if (rawConditions != null) {
                        for (String part : rawConditions.split(",")) {
                            try {
                                hologramConfig.conditions.add(new ConditionID(pack, part));
                            } catch (ObjectNotFoundException e) {
                                Debug.error("Error while loading " + part + " condition for hologram " + pack.getName() + "."
                                        + key + ": " + e.getMessage());
                            }
                        }
                    }

                    hologramConfig.settings = settings;

                    // load all NPCs for which this effect can be displayed
                    List<NPC> affectedNpcs = new ArrayList<>();
                    for (int id : settings.getIntegerList("npcs")) {
                        NPC npc = CitizensAPI.getNPCRegistry().getById(id);
                        if (npc != null && npcs.containsKey(npc)) {
                            affectedNpcs.add(npc);
                        }
                    }

                    for (NPC npc : settings.getIntegerList("npcs").size() == 0 ? npcs.keySet() : affectedNpcs) {
                        NPCHologram npcHologram = new NPCHologram();
                        npcHologram.config = hologramConfig;
                        npcs.get(npc).add(npcHologram);
                    }

                }
            }

            Bukkit.getPluginManager().registerEvents(instance, BetonQuest.getInstance().getJavaPlugin());

            runTaskTimer(BetonQuest.getInstance().getJavaPlugin(), 4, interval);
        }, 3);

        enabled = true;
    }

    /**
     * Reloads the particle effect
     */
    public static void reload() {
        if (instance != null) {
            if (instance.enabled) {
                instance.cleanUp();

                instance.cancel();
            }
            new CitizensHologram();
        }
    }

    @Override
    public void run() {
        updateHolograms();
    }

    private void cleanUp() {
        // Cancel Updater
        if (updater != null) {
            updater.cancel();
            updater = null;
        }

        // Destroy all holograms
        for (NPC npc : npcs.keySet()) {
            for (NPCHologram npcHologram : npcs.get(npc)) {
                if (npcHologram.hologram != null) {
                    npcHologram.hologram.delete();
                    npcHologram.hologram = null;
                }
            }
        }
    }

    private void updateHolograms() {
        // If we need to update hologram positions
        boolean npcUpdater = false;

        // Handle updating each NPC
        for (NPC npc : npcs.keySet()) {
            for (NPCHologram npcHologram : npcs.get(npc)) {
                boolean hologramEnabled = false;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    boolean visible = true;

                    for (ConditionID condition : npcHologram.config.conditions) {
                        if (!BetonQuest.condition(PlayerConverter.getID(player), condition)) {
                            visible = false;
                            break;
                        }
                    }

                    if (visible) {
                        hologramEnabled = true;
                        if (npcHologram.hologram == null) {
                            Hologram hologram = HologramsAPI.createHologram(BetonQuest.getInstance().getJavaPlugin(), npc.getStoredLocation().clone().add(npcHologram.config.vector));
                            hologram.getVisibilityManager().setVisibleByDefault(false);
                            for (String line : npcHologram.config.settings.getStringList("lines")) {
                                if (line.startsWith("item:")) {
                                    hologram.appendItemLine(new ItemStack(Material.matchMaterial(line.substring(5))));
                                } else {
                                    hologram.appendTextLine(line.replace('&', '§'));
                                }
                            }
                            npcHologram.hologram = hologram;
                        }

                        // We do this a tick later to work around a bug where holograms simply don't appear
                        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(BetonQuest.getInstance().getJavaPlugin(), () -> {
                            if (npcHologram.hologram != null) {
                                npcHologram.hologram.getVisibilityManager().showTo(player);
                            }
                        }, 5);

                    } else {
                        if (npcHologram.hologram != null) {
                            Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(BetonQuest.getInstance().getJavaPlugin(), () -> {
                                if (npcHologram.hologram != null) {
                                    npcHologram.hologram.getVisibilityManager().hideTo(player);
                                }
                            }, 5);
                        }
                    }
                }

                if (hologramEnabled) {
                    npcUpdater = true;
                } else {
                    // Destroy hologram
                    if (npcHologram.hologram != null) {

                        npcHologram.hologram.delete();
                        npcHologram.hologram = null;
                    }
                }
            }

        }

        if (npcUpdater) {
            if (updater == null) {
                // The updater only runs when at least one hologram is visible to ensure it stays relative to npc location
                updater = new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (NPC npc : npcs.keySet()) {
                            for (NPCHologram npcHologram : npcs.get(npc)) {
                                if (npcHologram.hologram != null) {
                                    npcHologram.hologram.teleport(npc.getStoredLocation().clone().add(npcHologram.config.vector));
                                }
                            }

                        }
                    }
                };
                updater.runTaskTimer(BetonQuest.getInstance().getJavaPlugin(), 1L, 1L);
            }
        } else {
            if (updater != null) {
                updater.cancel();
                updater = null;
            }
        }
    }

    private class NPCHologram {
        HologramConfig config;
        Hologram hologram;
    }

    private class HologramConfig {
        private List<ConditionID> conditions;
        private Vector vector;
        private ConfigurationSection settings;

    }
}
