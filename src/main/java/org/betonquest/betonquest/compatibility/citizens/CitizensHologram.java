package org.betonquest.betonquest.compatibility.citizens;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import lombok.CustomLog;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
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

    private static CitizensHologram instance;

    private final Map<Integer, List<NPCHologram>> npcs = new HashMap<>();
    private final BukkitTask initializationTask;
    private boolean follow;
    private BukkitTask followTask;
    private BukkitTask updateTask;

    public CitizensHologram() {
        super();
        if (instance != null) {
            initializationTask = null;
            return;
        }
        instance = this;

        initializationTask = Bukkit.getServer().getScheduler().runTask(BetonQuest.getInstance(), this::initHolograms);
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

        if (initializationTask != null) {
            initializationTask.cancel();
        }
        if (followTask != null) {
            followTask.cancel();
            followTask = null;
        }

        for (final List<NPCHologram> holograms : npcs.values()) {
            for (final NPCHologram npcHologram : holograms) {
                if (npcHologram.hologram != null) {
                    npcHologram.hologram.getVisibilityManager().resetVisibilityAll();
                    npcHologram.hologram.delete();
                    npcHologram.hologram = null;
                }
            }
        }
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    private void initHolograms() {
        int interval = 100;
        for (final QuestPackage pack : Config.getPackages().values()) {
            final ConfigurationSection npcsSection = pack.getConfig().getConfigurationSection("npcs");
            if (npcsSection != null) {
                for (final String npcID : npcsSection.getKeys(false)) {
                    try {
                        npcs.put(Integer.parseInt(npcID), new ArrayList<>());
                    } catch (final NumberFormatException exception) {
                        LOG.warn(pack, "Could not parse number of NPC '" + npcID + "'");
                    }
                }
            }

            final ConfigurationSection hologramsSection = pack.getConfig().getConfigurationSection("npc_holograms");
            if (hologramsSection == null) {
                continue;
            }
            if ("true".equalsIgnoreCase(hologramsSection.getString("disabled"))) {
                return;
            }
            interval = hologramsSection.getInt("check_interval", 100);
            if (interval <= 0) {
                LOG.warn(pack, "Could not load npc holograms of package " + pack.getPackagePath() + ": " +
                        "Check interval must be bigger than 0.");
                return;
            }
            follow = hologramsSection.getBoolean("follow", false);

            initHologramsConfig(pack, hologramsSection);
        }

        updateTask = runTaskTimer(BetonQuest.getInstance(), 1, interval);
    }

    private void initHologramsConfig(final QuestPackage pack, final ConfigurationSection hologramsSection) {
        for (final String key : hologramsSection.getKeys(false)) {
            final ConfigurationSection settingsSection = hologramsSection.getConfigurationSection(key);
            if (settingsSection == null) {
                continue;
            }

            final NPCHologram hologramConfig = new NPCHologram();
            hologramConfig.pack = pack;
            hologramConfig.vector = getVector(pack, key, settingsSection.getString("vector"));
            hologramConfig.conditions = initHologramsConfigConditions(pack, key, settingsSection.getString("conditions"));
            hologramConfig.lines = settingsSection.getStringList("lines");

            final List<Integer> affectedNpcs = new ArrayList<>();
            for (final int id : settingsSection.getIntegerList("npcs")) {
                if (npcs.containsKey(id)) {
                    affectedNpcs.add(id);
                }
            }
            for (final int npcID : affectedNpcs.isEmpty() ? npcs.keySet() : affectedNpcs) {
                npcs.get(npcID).add(hologramConfig);
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
                    npcHologram.hologram.getVisibilityManager().resetVisibilityAll();
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
                        npcHologram.hologram.teleport(npc.getStoredLocation().add(npcHologram.vector));
                    }
                }
            }
        }
    }

    private boolean updateHologramsForPlayers(final NPCHologram npcHologram, final NPC npc) {
        boolean hologramEnabled = false;
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (BetonQuest.conditions(PlayerConverter.getID(player), npcHologram.conditions)) {
                hologramEnabled = true;
                if (npcHologram.hologram == null) {
                    final Hologram hologram = HologramsAPI.createHologram(BetonQuest.getInstance(), npc.getStoredLocation().add(npcHologram.vector));
                    hologram.getVisibilityManager().setVisibleByDefault(false);
                    updateHologramForPlayersLines(npcHologram, hologram);
                    npcHologram.hologram = hologram;
                }
                if (!npcHologram.hologram.getVisibilityManager().isVisibleTo(player)) {
                    npcHologram.hologram.getVisibilityManager().showTo(player);
                }
            } else {
                if (npcHologram.hologram != null && npcHologram.hologram.getVisibilityManager().isVisibleTo(player)) {
                    npcHologram.hologram.getVisibilityManager().hideTo(player);
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
                    hologram.appendItemLine(stack);
                } catch (final InstructionParseException e) {
                    LOG.warn(npcHologram.pack, "Could not parse item in " + npcHologram.pack.getPackagePath() + " hologram: " + e.getMessage(), e);
                } catch (final ObjectNotFoundException e) {
                    LOG.warn(npcHologram.pack, "Could not find item in " + npcHologram.pack.getPackagePath() + " hologram: " + e.getMessage(), e);
                }
            } else {
                hologram.appendTextLine(line.replace('&', '§'));
            }
        }
    }

    private static class NPCHologram {
        private List<ConditionID> conditions;
        private Vector vector;
        private List<String> lines;
        private QuestPackage pack;
        private Hologram hologram;

        public NPCHologram() {
        }
    }
}
