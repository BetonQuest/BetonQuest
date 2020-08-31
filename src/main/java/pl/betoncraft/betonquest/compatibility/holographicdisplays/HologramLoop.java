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
package pl.betoncraft.betonquest.compatibility.holographicdisplays;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.id.ItemID;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

/**
 * Hides and shows holograms to players, based on conditions.
 *
 * @author Jakub Sapalski
 */
public class HologramLoop {

    private HashMap<Hologram, ConditionID[]> holograms = new HashMap<>();
    private HashMap<Hologram, BukkitRunnable> runnables = new HashMap<>();
    private BukkitRunnable runnable;

    /**
     * Starts a loop, which checks hologram conditions and shows them to players.
     */
    public HologramLoop() {
        // get all holograms and their condition
        for (final ConfigPackage pack : Config.getPackages().values()) {
            final String packName = pack.getName();
            final ConfigurationSection section = pack.getCustom().getConfig().getConfigurationSection("holograms");
            if (section == null) {
                continue;
            }
            for (final String key : section.getKeys(false)) {
                if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
                    LogUtils.getLogger().log(Level.WARNING, "Holograms won't be able to hide from players without ProtocolLib plugin! "
                            + "Install it to use conditioned holograms.");
                    return;
                }
                final List<String> lines = section.getStringList(key + ".lines");
                final String rawConditions = section.getString(key + ".conditions");
                final String rawLocation = section.getString(key + ".location");
                final int checkInterval = section.getInt(key + ".check_interval", 0);
                if (rawLocation == null) {
                    LogUtils.getLogger().log(Level.WARNING, "Location is not specified in " + key + " hologram");
                    continue;
                }
                ConditionID[] conditions = {};
                if (rawConditions != null) {
                    final String[] parts = rawConditions.split(",");
                    conditions = new ConditionID[parts.length];
                    for (int i = 0; i < conditions.length; i++) {
                        try {
                            conditions[i] = new ConditionID(pack, parts[i]);
                        } catch (ObjectNotFoundException e) {
                            LogUtils.getLogger().log(Level.WARNING, "Error while loading " + parts[i] + " condition for hologram " + packName + "."
                                    + key + ": " + e.getMessage());
                            LogUtils.logThrowable(e);
                        }
                    }
                }
                Location location = null;
                try {
                    location = new LocationData(packName, pack.subst(rawLocation)).getLocation(null);
                } catch (QuestRuntimeException | InstructionParseException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Could not parse location in " + key + " hologram: " + e.getMessage());
                    LogUtils.logThrowable(e);
                    continue;
                }
                final Hologram hologram = HologramsAPI.createHologram(BetonQuest.getInstance(), location);
                hologram.getVisibilityManager().setVisibleByDefault(false);
                for (final String line : lines) {
                    // If line begins with 'item:', then we will assume its a
                    // floating item
                    if (line.startsWith("item:")) {
                        try {
                            final String[] args = line.substring(5).split(":");
                            final ItemID itemID = new ItemID(pack, args[0]);
                            int stackSize = 1;
                            try {
                                stackSize = Integer.valueOf(args[1]);
                            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            }
                            final ItemStack stack = new QuestItem(itemID).generate(stackSize);
                            hologram.appendItemLine(stack);
                        } catch (InstructionParseException e) {
                            LogUtils.getLogger().log(Level.WARNING, "Could not parse item in " + key + " hologram: " + e.getMessage());
                            LogUtils.logThrowable(e);
                        } catch (ObjectNotFoundException e) {
                            LogUtils.getLogger().log(Level.WARNING, "Could not find item in " + key + " hologram: " + e.getMessage());
                            LogUtils.logThrowable(e);

                            //TODO Remove this code in the version 1.13 or later
                            //This support the old implementation of Items
                            final Material material = Material.matchMaterial(line.substring(5));
                            if (material != null) {
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
                if (checkInterval == 0) {
                    holograms.put(hologram, conditions);
                } else {
                    final ConditionID[] conditionsList = conditions;
                    final BukkitRunnable runnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            player:
                            for (final Player player : Bukkit.getOnlinePlayers()) {
                                final String playerID = PlayerConverter.getID(player);
                                for (final ConditionID condition : conditionsList) {
                                    if (!BetonQuest.condition(playerID, condition)) {
                                        hologram.getVisibilityManager().hideTo(player);
                                        continue player;
                                    }
                                }
                                hologram.getVisibilityManager().showTo(player);
                            }
                        }
                    };
                    runnable.runTaskTimerAsynchronously(BetonQuest.getInstance(), 20, checkInterval);
                    runnables.put(hologram, runnable);
                }
            }
        }
        // loop the holograms to show/hide them
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final String playerID = PlayerConverter.getID(player);
                    holograms:
                    for (final Entry<Hologram, ConditionID[]> entry : holograms.entrySet()) {
                        for (final ConditionID condition : entry.getValue()) {
                            if (!BetonQuest.condition(playerID, condition)) {
                                entry.getKey().getVisibilityManager().hideTo(player);
                                continue holograms;
                            }
                        }
                        entry.getKey().getVisibilityManager().showTo(player);
                    }
                }
            }
        };
        runnable.runTaskTimerAsynchronously(BetonQuest.getInstance(), 20, BetonQuest.getInstance().getConfig()
                .getInt("hologram_update_interval", 20 * 10));
    }

    /**
     * Cancels hologram updating loop and removes all BetonQuest-registered holograms.
     */
    public void cancel() {
        if (runnable != null) {
            runnable.cancel();
            for (final Hologram hologram : holograms.keySet()) {
                hologram.delete();
            }
        }
        for (final Entry<Hologram, BukkitRunnable> h : runnables.entrySet()) {
            h.getValue().cancel();
            h.getKey().delete();
        }
    }

}
