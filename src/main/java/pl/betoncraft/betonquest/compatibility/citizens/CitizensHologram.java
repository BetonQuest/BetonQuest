package pl.betoncraft.betonquest.compatibility.citizens;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
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
 * Some care is taken to optimize how holograms are displayed. They are destroyed when not needed,
 * shared between players and
 * we only have a fast update when needed to ensure they are relative to the NPC position
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.GodClass", "PMD.AssignmentToNonFinalStatic"})
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

    private void initHolograms() {
        int interval = 100;
        for (final ConfigPackage pack : Config.getPackages().values()) {
            final ConfigurationSection npcsSection = pack.getMain().getConfig().getConfigurationSection("npcs");
            if (npcsSection != null) {
                for (final String npcID : npcsSection.getKeys(false)) {
                    try {
                        npcs.put(Integer.parseInt(npcID), new ArrayList<>());
                    } catch (final NumberFormatException exception) {
                        LogUtils.getLogger().log(Level.WARNING, "Could not parse number of NPC '" + npcID + "'");
                    }
                }
            }

            final ConfigurationSection hologramsSection = pack.getCustom().getConfig().getConfigurationSection("npc_holograms");
            if (hologramsSection == null) {
                continue;
            }
            if ("true".equalsIgnoreCase(hologramsSection.getString("disabled"))) {
                return;
            }
            interval = hologramsSection.getInt("check_interval", 100);
            if (interval <= 0) {
                LogUtils.getLogger().log(Level.WARNING, "Could not load npc holograms of package " + pack.getName() + ": " +
                        "Check interval must be bigger than 0.");
                return;
            }
            follow = hologramsSection.getBoolean("follow", false);

            initHologramsConfig(pack, hologramsSection);
        }

        updateTask = runTaskTimer(BetonQuest.getInstance(), 1, interval);
    }

    private void initHologramsConfig(final ConfigPackage pack, final ConfigurationSection hologramsSection) {
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

    private Vector getVector(final ConfigPackage pack, final String key, final String vector) {
        if (vector != null) {
            try {
                final String[] vectorParts = vector.split(";");
                return new Vector(Double.parseDouble(vectorParts[0]), Double.parseDouble(vectorParts[1]), Double.parseDouble(vectorParts[2]));
            } catch (final NumberFormatException e) {
                LogUtils.getLogger().log(Level.WARNING, pack.getName() + ": Invalid vector in Hologram '" + key + "': " + vector);
                LogUtils.logThrowable(e);
            }
        }
        return new Vector(0, 3, 0);
    }

    private List<ConditionID> initHologramsConfigConditions(final ConfigPackage pack, final String key, final String rawConditions) {
        final ArrayList<ConditionID> conditions = new ArrayList<>();
        if (rawConditions != null) {
            for (final String part : rawConditions.split(",")) {
                try {
                    conditions.add(new ConditionID(pack, part));
                } catch (final ObjectNotFoundException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Error while loading " + part + " condition for hologram " + pack.getName() + "."
                            + key + ": " + e.getMessage());
                    LogUtils.logThrowable(e);
                }
            }
        }
        return conditions;
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
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
                        npcHologram.hologram.teleport(npc.getStoredLocation().clone().add(npcHologram.vector));
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
                    final Hologram hologram = HologramsAPI.createHologram(BetonQuest.getInstance(), npc.getStoredLocation().clone().add(npcHologram.vector));
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
                    LogUtils.getLogger().log(Level.WARNING, "Could not parse item in " + npcHologram.pack.getName() + " hologram: " + e.getMessage());
                    LogUtils.logThrowable(e);
                } catch (final ObjectNotFoundException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Could not find item in " + npcHologram.pack.getName() + " hologram: " + e.getMessage());
                    LogUtils.logThrowable(e);

                    //TODO Remove this code in the version 1.13 or later
                    //This support the old implementation of Items
                    final Material material = Material.matchMaterial(line.substring(5));
                    if (material != null) {
                        LogUtils.getLogger().log(Level.WARNING, "You use the Old method to define a hover item, this still work, but use the new method,"
                                + " defining it as a BetonQuest Item in the items.yml. The compatibility will be removed in 1.13");
                        hologram.appendItemLine(new ItemStack(material));
                    }
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
        private ConfigPackage pack;
        private Hologram hologram;

        public NPCHologram() {
        }
    }
}
