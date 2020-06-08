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
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.id.ItemID;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Displays a hologram relative to an npc
 * <p>
 * Some care is taken to optimize how holograms are displayed. They are destroyed when not needed, shared between players and
 * we only have a fast update when needed to ensure they are relative to the NPC position
 */

public class CitizensHologram extends BukkitRunnable implements Listener {

    private static CitizensHologram instance;

    // All NPC's with config
    private Map<String, List<NPCHologram>> npcs = new HashMap<>();

    private int interval = 100;
    private boolean follow = false;
    private boolean enabled;

    // Updater
    private BukkitRunnable updater;


    public CitizensHologram() {
        instance = this;

        // Start this when all plugins loaded
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BetonQuest.getInstance(), () -> {
            // loop across all packages
            for (ConfigPackage pack : Config.getPackages().values()) {

                // load all NPC's
                if (pack.getMain().getConfig().getConfigurationSection("npcs") != null) {
                    for (String npcID : pack.getMain().getConfig().getConfigurationSection("npcs").getKeys(false)) {
                        try {
                            NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(npcID));
                            if (npc != null) {
                                npcs.put(npcID, new ArrayList<>());
                            }
                        } catch (NumberFormatException e) {
                            LogUtils.logThrowableIgnore(e);
                        }
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
                    LogUtils.getLogger().log(Level.WARNING, "Could not load npc holograms of package " + pack.getName() + ": " +
                            "Check interval must be bigger than 0.");
                    return;
                }

                // load follow flag
                follow = section.getBoolean("follow", false);

                // loading hologram config
                for (String key : section.getKeys(false)) {
                    ConfigurationSection settings = section.getConfigurationSection(key);

                    // if the key is not a configuration section then it's not a hologram
                    if (settings == null) {
                        continue;
                    }

                    HologramConfig hologramConfig = new HologramConfig();
                    hologramConfig.pack = pack;

                    try {
                        String[] vectorParts = settings.getString("vector", "0;3;0").split(";");
                        hologramConfig.vector = new Vector(
                                Double.parseDouble(vectorParts[0]),
                                Double.parseDouble(vectorParts[1]),
                                Double.parseDouble(vectorParts[2])
                        );
                    } catch (NumberFormatException e) {
                        LogUtils.getLogger().log(Level.WARNING, pack.getName() + ": Invalid vector: " + settings.getString("vector"));
                        LogUtils.logThrowable(e);
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
                                LogUtils.getLogger().log(Level.WARNING, "Error while loading " + part + " condition for hologram " + pack.getName() + "."
                                        + key + ": " + e.getMessage());
                                LogUtils.logThrowable(e);
                            }
                        }
                    }

                    hologramConfig.settings = settings;

                    // load all NPCs for which this effect can be displayed
                    List<String> affectedNpcs = new ArrayList<>();
                    for (int id : settings.getIntegerList("npcs")) {
                        NPC npc = CitizensAPI.getNPCRegistry().getById(id);
                        if (npc != null && npcs.containsKey(String.valueOf(id))) {
                            affectedNpcs.add(String.valueOf(id));
                        }
                    }

                    for (String npcID : settings.getIntegerList("npcs").size() == 0 ? npcs.keySet() : affectedNpcs) {
                        NPCHologram npcHologram = new NPCHologram();
                        npcHologram.config = hologramConfig;
                        npcs.get(npcID).add(npcHologram);
                    }

                }
            }

            Bukkit.getPluginManager().registerEvents(instance, BetonQuest.getInstance());

            runTaskTimer(BetonQuest.getInstance(), 4, interval);
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
        for (String npcID : npcs.keySet()) {
            for (NPCHologram npcHologram : npcs.get(npcID)) {
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
        for (String npcID : npcs.keySet()) {
            for (NPCHologram npcHologram : npcs.get(npcID)) {
                boolean hologramEnabled = false;

                NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(npcID));

                if (npc == null) {
                    continue;
                }

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
                            Hologram hologram = HologramsAPI.createHologram(BetonQuest.getInstance(), npc.getStoredLocation().clone().add(npcHologram.config.vector));
                            hologram.getVisibilityManager().setVisibleByDefault(false);
                            for (String line : npcHologram.config.settings.getStringList("lines")) {
                                if (line.startsWith("item:")) {
                                    try {
                                        String args[] = line.substring(5).split(":");
                                        ItemID itemID = new ItemID(npcHologram.config.pack, args[0]);
                                        int stackSize = 1;
                                        try {
                                            stackSize = Integer.valueOf(args[1]);
                                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                                        }
                                        ItemStack stack = new QuestItem(itemID).generate(stackSize);
                                        stack.setAmount(stackSize);
                                        hologram.appendItemLine(stack);
                                    } catch (InstructionParseException e) {
                                        LogUtils.getLogger().log(Level.WARNING, "Could not parse item " + line.substring(5) + " hologram: "
                                                + e.getMessage());
                                        LogUtils.logThrowable(e);
                                    } catch (ObjectNotFoundException e) {
                                        LogUtils.getLogger().log(Level.WARNING, "Could not find item in " + line.substring(5).split(":")[0]
                                                + " hologram: " + e.getMessage());
                                        LogUtils.logThrowable(e);
                                        
                                        //TODO Remove this code in the version 1.13 or later
                                        //This support the old implementation of Items 
                                        Material material = Material.matchMaterial(line.substring(5));
                                        if(material != null) {
                                            LogUtils.getLogger().log(Level.WARNING, "You use the Old method to define a hover item, this still work, but use the new method,"
                                                    + " defining it as a BetonQuest Item in the items.yml. The compatibility will be removed in 1.13");
                                            hologram.appendItemLine(new ItemStack(material));
                                        }
                                        //Remove up to here
                                    }
                                } else {
                                    hologram.appendTextLine(line.replace('&', '§'));
                                }
                            }
                            npcHologram.hologram = hologram;
                        }

                        // We do this a tick later to work around a bug where holograms simply don't appear
                        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(BetonQuest.getInstance(), () -> {
                            if (npcHologram.hologram != null) {
                                npcHologram.hologram.getVisibilityManager().showTo(player);
                            }
                        }, 5);

                    } else {
                        if (npcHologram.hologram != null) {
                            Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(BetonQuest.getInstance(), () -> {
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
                        for (String npcID : npcs.keySet()) {
                            for (NPCHologram npcHologram : npcs.get(npcID)) {
                                if (npcHologram.hologram != null) {
                                    NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(npcID));
                                    if (npc != null) {
                                        npcHologram.hologram.teleport(npc.getStoredLocation().clone().add(npcHologram.config.vector));
                                    }
                                }
                            }

                        }
                    }
                };
                if (follow) {
                    updater.runTaskTimer(BetonQuest.getInstance(), 1L, 1L);
                } else {
                    updater.runTaskLater(BetonQuest.getInstance(), 1L);
                }
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
        private ConfigPackage pack;
    }
}
