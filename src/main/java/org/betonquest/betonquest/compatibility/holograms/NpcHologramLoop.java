package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.api.bukkit.event.npc.NpcVisibilityUpdateEvent;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.kernel.processor.StartTask;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.kernel.registry.quest.NpcTypeRegistry;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Hides and shows holograms to players at a Npcs location. Based on conditions.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class NpcHologramLoop extends HologramLoop implements Listener, StartTask {
    /**
     * The task that lets holograms follow NPCs.
     */
    private final BukkitTask followTask;

    /**
     * List of all {@link NpcHologram}s.
     */
    private final List<NpcHologram> npcHolograms;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * Plugin instance for task scheduling.
     */
    private final Plugin plugin;

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * The Npc Registry to create identifier strings from Npcs.
     */
    private final NpcTypeRegistry npcTypeRegistry;

    /**
     * Starts a loop, which checks hologram conditions and shows them to players.
     * Also starts the npc update listener.
     *
     * @param loggerFactory     logger factory to use
     * @param log               the logger that will be used for logging
     * @param packManager       the quest package manager to get quest packages from
     * @param plugin            the plugin to schedule tasks
     * @param variableProcessor the variable processor to use
     * @param hologramProvider  the hologram provider to create new holograms
     * @param featureApi        the Feature API to get NPC instances
     * @param npcTypeRegistry   the registry to create identifier strings from Npcs
     */
    public NpcHologramLoop(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log,
                           final QuestPackageManager packManager, final Plugin plugin,
                           final VariableProcessor variableProcessor, final HologramProvider hologramProvider,
                           final FeatureApi featureApi, final NpcTypeRegistry npcTypeRegistry) {
        super(loggerFactory, log, packManager, variableProcessor, hologramProvider, "Npc Hologram", "npc_holograms");
        this.packManager = packManager;
        this.plugin = plugin;
        this.featureApi = featureApi;
        this.npcTypeRegistry = npcTypeRegistry;
        npcHolograms = new ArrayList<>();
        followTask = plugin.getServer().getScheduler().runTaskTimer(plugin,
                () -> npcHolograms.stream().filter(NpcHologram::follow)
                        .forEach(this::updateHologram), 1L, 1L);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void clear() {
        npcHolograms.clear();
        super.clear();
    }

    /**
     * Stops the follow task and unregisters the update listener.
     */
    public void close() {
        followTask.cancel();
        HandlerList.unregisterAll(this);
        clear();
    }

    @Override
    protected List<BetonHologram> getHologramsFor(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final Vector vector = new Vector(0, 3, 0);
        final String stringVector = section.getString("vector");
        if (stringVector != null) {
            vector.add(new Variable<>(variableProcessor, pack, "(" + stringVector + ")", Argument.VECTOR).getValue(null));
        }
        final List<NpcID> npcIDs = getNpcs(pack, section);
        final boolean follow = section.getBoolean("follow", false);
        final Map<NpcID, BetonHologram> npcBetonHolograms = new HashMap<>();
        npcIDs.forEach(npcID -> npcBetonHolograms.put(npcID, null));
        final List<BetonHologram> holograms = new ArrayList<>();
        npcHolograms.add(new NpcHologram(npcBetonHolograms, holograms, vector, follow));
        return holograms;
    }

    /**
     * Delays the start task one tick further to allow loading of NPCs on server start.
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    @Override
    public void startAll() {
        log.debug("Delaying NPC Hologram creation…");
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            log.debug("Loading delayed NPC Holograms.");
            npcHolograms.forEach(holo -> {
                for (final Map.Entry<NpcID, BetonHologram> entry : holo.npcHolograms.entrySet()) {
                    final NpcID npcID = entry.getKey();
                    final Npc<?> npc;
                    try {
                        npc = featureApi.getNpc(npcID, null);
                    } catch (final QuestException exception) {
                        log.warn("Could not get Npc for id '" + npcID + "' at hologram creation: " + exception.getMessage(), exception);
                        continue;
                    }
                    if (!npc.isSpawned()) {
                        continue;
                    }
                    final Optional<Location> location = npc.getLocation();
                    if (location.isEmpty()) {
                        log.debug("Spawned Npc '" + npcID + "' has no location at hologram creation");
                        continue;
                    }
                    final BetonHologram hologram = hologramProvider.createHologram(location.get().add(holo.vector));
                    entry.setValue(hologram);
                    holo.holograms.add(hologram);
                    updateHologram(hologram);
                }
            });
            log.debug("Loaded NPC Holograms.");
        });
    }

    private List<NpcID> getNpcs(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        return new VariableList<>(variableProcessor, pack, section.getString("npcs", ""), value -> new NpcID(packManager, pack, value)).getValue(null);
    }

    private void updateHologram(final NpcHologram npcHologram) {
        npcHologram.npcHolograms().entrySet().forEach(entry -> {
                    final NpcID npcID = entry.getKey();
                    final BetonHologram hologram = entry.getValue();
                    final Npc<?> npc;
                    try {
                        npc = featureApi.getNpc(npcID, null);
                    } catch (final QuestException exception) {
                        log.warn("Could not get Npc for id '" + npcID + "' in hologram loop: " + exception.getMessage(), exception);
                        return;
                    }
                    final Optional<Location> npcLocation = npc.getLocation();
                    if (!npc.isSpawned() || npcLocation.isEmpty()) {
                        if (hologram != null) {
                            hologram.disable();
                        }
                        return;
                    }
                    final Location location = npcLocation.get().add(npcHologram.vector());
                    if (hologram == null) {
                        final BetonHologram newHologram = hologramProvider.createHologram(location);
                        entry.setValue(newHologram);
                        npcHologram.holograms().add(newHologram);
                        updateHologram(newHologram);
                    } else {
                        if (hologram.isDisabled()) {
                            hologram.enable();
                        }
                        hologram.move(location);
                    }
                }
        );
    }

    private void updateHologram(final BetonHologram hologram) {
        values.values().stream()
                .filter(hologramWrapper -> hologramWrapper.holograms().contains(hologram))
                .forEach(hologramWrapper -> {
                    hologramWrapper.initialiseContent();
                    hologramWrapper.updateVisibility();
                });
    }

    /**
     * Update the hologram on external triggers.
     *
     * @param event The event.
     */
    @EventHandler
    public void onExternalUpdate(final NpcVisibilityUpdateEvent event) {
        final Npc<?> npc = event.getNpc();
        if (npc == null) {
            npcHolograms.forEach(this::updateHologram);
            return;
        }
        final Set<NpcID> ids = npcTypeRegistry.getIdentifier(npc, null);
        if (ids.isEmpty()) {
            return;
        }
        final List<NpcHologram> list = npcHolograms.stream()
                .filter(npcHologram -> ids.stream().anyMatch(npcId -> npcHologram.npcHolograms().containsKey(npcId)))
                .toList();
        if (!list.isEmpty()) {
            plugin.getServer().getScheduler().runTask(plugin, () -> list.forEach(this::updateHologram));
        }
    }

    /**
     * Link a list of NPC IDs to a list of holograms.
     *
     * @param npcHolograms the list of NPC IDs and there linked holograms.
     * @param holograms    The holograms.
     */
    private record NpcHologram(Map<NpcID, BetonHologram> npcHolograms, List<BetonHologram> holograms,
                               Vector vector, boolean follow) {
    }
}
