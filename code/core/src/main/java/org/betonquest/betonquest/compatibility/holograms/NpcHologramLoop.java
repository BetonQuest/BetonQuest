package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.npc.NpcVisibilityUpdateEvent;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.npc.NpcManager;
import org.betonquest.betonquest.api.service.npc.NpcRegistry;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.kernel.processor.StartTask;
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
     * Plugin instance for task scheduling.
     */
    private final Plugin plugin;

    /**
     * The npc manager to get npcs from.
     */
    private final NpcManager npcManager;

    /**
     * The Npc Registry to create identifier strings from Npcs.
     */
    private final NpcRegistry npcRegistry;

    /**
     * Starts a loop, which checks hologram conditions and shows them to players.
     * Also starts the npc update listener.
     *
     * @param loggerFactory     logger factory to use
     * @param log               the logger that will be used for logging
     * @param instructions      the instruction api to use
     * @param packManager       the quest package manager to get quest packages from
     * @param plugin            the plugin to schedule tasks
     * @param hologramProvider  the hologram provider to create new holograms
     * @param configAccessor    the config accessor to read config values from
     * @param identifierFactory the identifier factory to create {@link HologramIdentifier}s for this type
     * @param conditionManager  the condition manager
     * @param npcManager        the npc manager to get npcs from
     * @param npcRegistry       the registry to create identifier strings from Npcs
     * @param textParser        the text parser used to parse text and colors
     * @param profileProvider   the profile provider instance
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public NpcHologramLoop(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log,
                           final Instructions instructions, final QuestPackageManager packManager, final Plugin plugin,
                           final HologramProvider hologramProvider, final ConfigAccessor configAccessor,
                           final IdentifierFactory<HologramIdentifier> identifierFactory, final ConditionManager conditionManager,
                           final NpcManager npcManager, final NpcRegistry npcRegistry, final TextParser textParser,
                           final ProfileProvider profileProvider) {
        super(loggerFactory, log, instructions, packManager, hologramProvider, "Npc Hologram",
                "npc_holograms", textParser, identifierFactory, configAccessor, conditionManager, profileProvider);
        this.plugin = plugin;
        this.npcManager = npcManager;
        this.npcRegistry = npcRegistry;
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
    protected List<BetonHologram> getHologramsFor(final SectionInstruction instruction) throws QuestException {
        final ConfigurationSection section = instruction.getSection();
        final Vector vector = new Vector(0, 3, 0);
        final String stringVector = section.getString("vector");
        if (stringVector != null) {
            vector.add(instruction.chainForArgument("(" + stringVector + ")").vector().get().getValue(null));
        }
        final List<NpcIdentifier> npcIDs = instruction.read().value("npcs").identifier(NpcIdentifier.class).list().get().getValue(null);
        final boolean follow = section.getBoolean("follow", false);
        final Map<NpcIdentifier, BetonHologram> npcBetonHolograms = new HashMap<>();
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
                for (final Map.Entry<NpcIdentifier, BetonHologram> entry : holo.npcHolograms.entrySet()) {
                    final NpcIdentifier npcID = entry.getKey();
                    final Npc<?> npc;
                    try {
                        npc = npcManager.get(null, npcID);
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

    private void updateHologram(final NpcHologram npcHologram) {
        npcHologram.npcHolograms().entrySet().forEach(entry -> {
                    final NpcIdentifier npcID = entry.getKey();
                    final BetonHologram hologram = entry.getValue();
                    final Npc<?> npc;
                    try {
                        npc = npcManager.get(null, npcID);
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
        final Set<NpcIdentifier> ids = npcRegistry.getIdentifier(npc, null);
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
     * @param npcHolograms the list of NPC IDs and their linked holograms
     * @param vector       The vector offset
     * @param follow       Whether the holograms should follow the NPC
     * @param holograms    The holograms
     */
    private record NpcHologram(Map<NpcIdentifier, BetonHologram> npcHolograms, List<BetonHologram> holograms,
                               Vector vector, boolean follow) {

    }
}
