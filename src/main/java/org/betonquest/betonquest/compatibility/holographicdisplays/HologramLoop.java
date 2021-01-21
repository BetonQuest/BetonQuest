package org.betonquest.betonquest.compatibility.holographicdisplays;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.LogUtils;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

/**
 * Hides and shows holograms to players, based on conditions.
 */
@SuppressWarnings("PMD.CommentRequired")
public class HologramLoop {

    private final Map<Hologram, ConditionID[]> holograms = new HashMap<>();
    private final Map<Hologram, BukkitRunnable> runnables = new HashMap<>();
    private final BukkitRunnable runnable;

    /**
     * Starts a loop, which checks hologram conditions and shows them to players.
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength", "PMD.NcssCount", "PMD.NPathComplexity"})
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
                    runnable = null;
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
                        } catch (final ObjectNotFoundException e) {
                            LogUtils.getLogger().log(Level.WARNING, "Error while loading " + parts[i] + " condition for hologram " + packName + "."
                                    + key + ": " + e.getMessage());
                            LogUtils.logThrowable(e);
                        }
                    }
                }
                final Location location;
                try {
                    location = new CompoundLocation(packName, pack.subst(rawLocation)).getLocation(null);
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
                            int stackSize;
                            try {
                                stackSize = Integer.parseInt(args[1]);
                            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                                stackSize = 1;
                            }
                            final ItemStack stack = new QuestItem(itemID).generate(stackSize);
                            hologram.appendItemLine(stack);
                        } catch (final InstructionParseException e) {
                            LogUtils.getLogger().log(Level.WARNING, "Could not parse item in " + key + " hologram: " + e.getMessage());
                            LogUtils.logThrowable(e);
                        } catch (final ObjectNotFoundException e) {
                            LogUtils.getLogger().log(Level.WARNING, "Could not find item in " + key + " hologram: " + e.getMessage());
                            LogUtils.logThrowable(e);
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
                            for (final Player player : Bukkit.getOnlinePlayers()) {
                                final String playerID = PlayerConverter.getID(player);
                                if (!BetonQuest.conditions(playerID, conditionsList)) {
                                    hologram.getVisibilityManager().hideTo(player);
                                    continue;
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
                    for (final Entry<Hologram, ConditionID[]> entry : holograms.entrySet()) {
                        if (!BetonQuest.conditions(playerID, entry.getValue())) {
                            entry.getKey().getVisibilityManager().hideTo(player);
                            continue;
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
                hologram.getVisibilityManager().resetVisibilityAll();
                hologram.delete();
            }
        }
        for (final Entry<Hologram, BukkitRunnable> h : runnables.entrySet()) {
            h.getValue().cancel();
            h.getKey().delete();
        }
    }

}
