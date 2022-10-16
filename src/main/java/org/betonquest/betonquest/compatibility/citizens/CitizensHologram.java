package org.betonquest.betonquest.compatibility.citizens;

import lombok.CustomLog;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.PlaceholderSetting;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays a hologram relative to an npc
 * <p>
 * Some care is taken to optimize how holograms are displayed. They are destroyed when not needed,
 * shared between players and
 * we only have a fast update when needed to ensure they are relative to the NPC position
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.GodClass", "PMD.AssignmentToNonFinalStatic"})
@CustomLog
public class CitizensHologram extends BukkitRunnable {
    /**
     * Singleton instance of CitizensHologram
     */
    private static CitizensHologram instance;
    /**
     * Instance of HolographicDisplaysAPI
     */
    private final HolographicDisplaysAPI holographicDisplaysAPI;

    private final Map<Integer, List<NPCHologram>> npcs = new HashMap<>();
    private boolean follow;
    private BukkitTask followTask;
    private BukkitTask updateTask;

    public CitizensHologram() {
        super();
        holographicDisplaysAPI = HolographicDisplaysAPI.get(BetonQuest.getInstance());
        if (instance != null) {
            return;
        }
        instance = this;


        initHolograms();
    }

    /**
     * Reloads the particle effect
     */
    public static void reload() {
        synchronized (CitizensHologram.class) {
            if (instance != null) {
                instance.cancel();
                instance = null;
                new CitizensHologram();
            }
        }
    }

    @Override
    public void run() {
        updateHolograms();
    }

    @Override
    public void cancel() {
        if (updateTask != null) {
            super.cancel();
        }
        if (followTask != null) {
            followTask.cancel();
            followTask = null;
        }

        for (final List<NPCHologram> holograms : npcs.values()) {
            for (final NPCHologram npcHologram : holograms) {
                if (npcHologram.hologram != null) {
                    npcHologram.hologram.getVisibilitySettings().clearIndividualVisibilities();
                    npcHologram.hologram.delete();
                    npcHologram.hologram = null;
                }
            }
        }
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private void initHolograms() {
        int interval = 0;
        for (final QuestPackage pack : Config.getPackages().values()) {
            final ConfigurationSection hologramsSection = pack.getConfig().getConfigurationSection("npc_holograms");
            if (hologramsSection == null || "true".equalsIgnoreCase(hologramsSection.getString("disabled"))) {
                continue;
            }
            interval = hologramsSection.getInt("check_interval", interval);
            follow = hologramsSection.getBoolean("follow", false);
            initHologramsConfig(pack, hologramsSection);
        }
        if (interval <= 0) {
            interval = 100;
        }
        updateTask = runTaskTimer(BetonQuest.getInstance(), 1, interval);
    }

    private void initHologramsConfig(final QuestPackage pack, final ConfigurationSection hologramsSection) {
        for (final String key : hologramsSection.getKeys(false)) {
            final ConfigurationSection settingsSection = hologramsSection.getConfigurationSection(key);
            if (settingsSection == null) {
                continue;
            }
            final Vector vector = getVector(pack, key, settingsSection.getString("vector"));
            final List<String> lines = settingsSection.getStringList("lines");
            final List<ConditionID> conditions = initHologramsConfigConditions(pack, key, settingsSection.getString("conditions"));

            for (final int id : settingsSection.getIntegerList("npcs")) {
                npcs.putIfAbsent(id, new ArrayList<>());
                npcs.get(id).add(new NPCHologram(pack, vector, lines, conditions));
            }
        }
    }

    private Vector getVector(final QuestPackage pack, final String key, final String vector) {
        if (vector != null) {
            try {
                final String[] vectorParts = vector.split(";");
                return new Vector(Double.parseDouble(vectorParts[0]), Double.parseDouble(vectorParts[1]), Double.parseDouble(vectorParts[2]));
            } catch (final NumberFormatException e) {
                LOG.warn(pack, pack.getPackagePath() + ": Invalid vector in Hologram '" + key + "': " + vector, e);
            }
        }
        return new Vector(0, 3, 0);
    }

    private List<ConditionID> initHologramsConfigConditions(final QuestPackage pack, final String key, final String rawConditions) {
        final ArrayList<ConditionID> conditions = new ArrayList<>();
        if (rawConditions != null) {
            for (final String part : rawConditions.split(",")) {
                try {
                    conditions.add(new ConditionID(pack, part));
                } catch (final ObjectNotFoundException e) {
                    LOG.warn(pack, "Error while loading " + part + " condition for hologram " + pack.getPackagePath() + "."
                            + key + ": " + e.getMessage(), e);
                }
            }
        }
        return conditions;
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    private void updateHolograms() {
        boolean npcUpdater = false;
        for (final Map.Entry<Integer, List<NPCHologram>> entry : npcs.entrySet()) {
            for (final NPCHologram npcHologram : entry.getValue()) {
                final NPC npc = CitizensAPI.getNPCRegistry().getById(entry.getKey());
                if (npc == null) {
                    continue;
                }

                if (updateHologramsForPlayers(npcHologram, npc)) {
                    npcUpdater = true;
                } else if (npcHologram.hologram != null) {
                    npcHologram.hologram.getVisibilitySettings().clearIndividualVisibilities();
                    npcHologram.hologram.delete();
                    npcHologram.hologram = null;
                }
            }

        }

        if (npcUpdater) {
            if (followTask == null) {
                if (follow) {
                    followTask = Bukkit.getServer().getScheduler().runTaskTimer(BetonQuest.getInstance(), this::followUpdate, 1L, 1L);
                } else {
                    followTask = Bukkit.getServer().getScheduler().runTask(BetonQuest.getInstance(), this::followUpdate);
                }
            }
        } else {
            if (followTask != null) {
                followTask.cancel();
                followTask = null;
            }
        }
    }

    private void followUpdate() {
        for (final Map.Entry<Integer, List<NPCHologram>> entry : npcs.entrySet()) {
            for (final NPCHologram npcHologram : entry.getValue()) {
                if (npcHologram.hologram != null) {
                    final NPC npc = CitizensAPI.getNPCRegistry().getById(entry.getKey());
                    if (npc != null) {
                        npcHologram.hologram.setPosition(npc.getStoredLocation().add(npcHologram.vector));
                    }
                }
            }
        }
    }

    private boolean updateHologramsForPlayers(final NPCHologram npcHologram, final NPC npc) {
        boolean hologramEnabled = false;
        for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
            if (BetonQuest.conditions(onlineProfile, npcHologram.conditions)) {
                hologramEnabled = true;
                if (npcHologram.hologram == null) {
                    final Hologram hologram = holographicDisplaysAPI.createHologram(npc.getStoredLocation().add(npcHologram.vector));
                    hologram.setPlaceholderSetting(PlaceholderSetting.ENABLE_ALL);
                    hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
                    updateHologramForPlayersLines(npcHologram, hologram);
                    npcHologram.hologram = hologram;
                }
                if (!npcHologram.hologram.getVisibilitySettings().isVisibleTo(onlineProfile.getOnlinePlayer())) {
                    npcHologram.hologram.getVisibilitySettings().setIndividualVisibility(onlineProfile.getOnlinePlayer(), VisibilitySettings.Visibility.VISIBLE);
                }
            } else {
                if (npcHologram.hologram != null && npcHologram.hologram.getVisibilitySettings().isVisibleTo(onlineProfile.getOnlinePlayer())) {
                    npcHologram.hologram.getVisibilitySettings().setIndividualVisibility(onlineProfile.getOnlinePlayer(), VisibilitySettings.Visibility.HIDDEN);
                }
            }
        }
        return hologramEnabled;
    }

    private void updateHologramForPlayersLines(final NPCHologram npcHologram, final Hologram hologram) {
        for (final String line : npcHologram.lines) {
            if (line.startsWith("item:")) {
                try {
                    final String[] args = line.substring(5).split(":");
                    final ItemID itemID = new ItemID(npcHologram.pack, args[0]);
                    int stackSize;
                    try {
                        stackSize = Integer.parseInt(args[1]);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        stackSize = 1;
                    }
                    final ItemStack stack = new QuestItem(itemID).generate(stackSize);
                    hologram.getLines().appendItem(stack);
                } catch (final InstructionParseException e) {
                    LOG.warn(npcHologram.pack, "Could not parse item in " + npcHologram.pack.getPackagePath() + " hologram: " + e.getMessage(), e);
                } catch (final ObjectNotFoundException e) {
                    LOG.warn(npcHologram.pack, "Could not find item in " + npcHologram.pack.getPackagePath() + " hologram: " + e.getMessage(), e);
                }
            } else {
                hologram.getLines().appendText(line.replace('&', '§'));
            }
        }
    }

    private static class NPCHologram {
        private final QuestPackage pack;
        private final Vector vector;
        private final List<String> lines;
        private final List<ConditionID> conditions;
        private Hologram hologram;

        public NPCHologram(final QuestPackage pack, final Vector vector, final List<String> lines, final List<ConditionID> conditions) {
            this.pack = pack;
            this.vector = vector;
            this.lines = lines;
            this.conditions = conditions;
            this.hologram = null;
        }
    }
}
