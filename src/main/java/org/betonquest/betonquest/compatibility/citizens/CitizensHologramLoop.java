package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensReloadEvent;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.event.NPCTeleportEvent;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.betonquest.betonquest.compatibility.holograms.HologramLoop;
import org.betonquest.betonquest.compatibility.holograms.HologramProvider;
import org.betonquest.betonquest.compatibility.holograms.HologramWrapper;
import org.betonquest.betonquest.instruction.variable.location.VariableVector;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hides and shows holograms to players at an NPC's location. Based on conditions.
 */
public class CitizensHologramLoop extends HologramLoop implements Listener {
    /**
     * The task that lets holograms follow NPCs.
     */
    private final BukkitTask followTask;

    /**
     * List of all {@link NPCHologram}s.
     */
    private final List<NPCHologram> npcHolograms;

    /**
     * List of all {@link HologramWrapper}s.
     */
    private final List<HologramWrapper> holograms;

    /**
     * Starts a loop, which checks hologram conditions and shows them to players.
     *
     * @param loggerFactory logger factory to use
     * @param log           the logger that will be used for logging
     */
    public CitizensHologramLoop(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log) {
        super(loggerFactory, log);
        npcHolograms = new ArrayList<>();
        holograms = initialize("npc_holograms");
        followTask = Bukkit.getServer().getScheduler().runTaskTimer(BetonQuest.getInstance(),
                () -> npcHolograms.stream().filter(NPCHologram::follow)
                        .forEach(this::updateHologram), 1L, 1L);
        Bukkit.getServer().getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    /**
     * Stops the follow task.
     */
    public void close() {
        followTask.cancel();
        HandlerList.unregisterAll(this);
    }

    @Override
    protected List<BetonHologram> getHologramsFor(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final Vector vector = new Vector(0, 3, 0);
        final String stringVector = section.getString("vector");
        if (stringVector != null) {
            try {
                vector.add(new VariableVector(BetonQuest.getInstance().getVariableProcessor(), pack, "(" + stringVector + ")").getValue(null));
            } catch (final QuestException e) {
                throw new QuestException("Could not parse vector '" + stringVector + "': " + e.getMessage(), e);
            }
        }
        final List<Integer> npcIDs = getNPCs(pack, section);
        final boolean follow = section.getBoolean("follow", false);
        final Map<Integer, BetonHologram> npcBetonHolograms = new HashMap<>();
        final List<BetonHologram> holograms = new ArrayList<>();
        npcIDs.forEach(npcID -> {
            final NPC npc = CitizensAPI.getNPCRegistry().getById(npcID);
            if (npc == null || !npc.isSpawned()) {
                npcBetonHolograms.put(npcID, null);
            } else {
                final BetonHologram hologram = HologramProvider.getInstance()
                        .createHologram(npc.getStoredLocation().add(vector));
                npcBetonHolograms.put(npcID, hologram);
                holograms.add(hologram);
            }
        });
        npcHolograms.add(new NPCHologram(npcBetonHolograms, holograms, vector, follow));
        return holograms;
    }

    private List<Integer> getNPCs(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final List<Integer> npcIDs = new ArrayList<>();
        for (final String stringID : section.getStringList("npcs")) {
            final String subst = GlobalVariableResolver.resolve(pack, stringID);
            try {
                npcIDs.add(Integer.parseInt(subst));
            } catch (final NumberFormatException e) {
                throw new QuestException("Could not parse NPC ID '" + subst + "': " + e.getMessage(), e);
            }
        }
        return npcIDs;
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    private void updateHologram(final NPCHologram npcHologram) {
        npcHologram.npcHolograms().entrySet().forEach(entry -> {
                    final Integer npcID = entry.getKey();
                    final BetonHologram hologram = entry.getValue();
                    final NPC npc = CitizensAPI.getNPCRegistry().getById(npcID);
                    if (npc == null || !npc.isSpawned()) {
                        if (hologram == null) {
                            return;
                        }
                        hologram.disable();
                    } else {
                        final Location location = npc.getStoredLocation().add(npcHologram.vector());
                        if (hologram == null) {
                            final BetonHologram newHologram = HologramProvider.getInstance().createHologram(location);
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
                }
        );
    }

    private void updateHologram(final BetonHologram hologram) {
        holograms.stream()
                .filter(hologramWrapper -> hologramWrapper.holograms().contains(hologram))
                .forEach(hologramWrapper -> {
                    hologramWrapper.initialiseContent();
                    hologramWrapper.updateVisibility();
                });
    }

    /**
     * Update the hologram when the plugin reloads.
     *
     * @param event The event.
     */
    @EventHandler
    public void onCitizensReload(final CitizensReloadEvent event) {
        npcHolograms.forEach(this::updateHologram);
    }

    /**
     * Update the hologram when the NPC spawns.
     *
     * @param event The event.
     */
    @EventHandler
    public void onNPCSpawn(final NPCSpawnEvent event) {
        npcHolograms.stream()
                .filter(npcHologram -> npcHologram.npcHolograms().containsKey(event.getNPC().getId()))
                .forEach(hologram -> Bukkit.getServer().getScheduler().runTask(BetonQuest.getInstance(), () -> updateHologram(hologram)));
    }

    /**
     * Update the hologram when the NPC despawns.
     *
     * @param event The event.
     */
    @EventHandler
    public void onNPCDespawn(final NPCDespawnEvent event) {
        npcHolograms.stream()
                .filter(npcHologram -> npcHologram.npcHolograms().containsKey(event.getNPC().getId()))
                .forEach(hologram -> Bukkit.getServer().getScheduler().runTask(BetonQuest.getInstance(), () -> updateHologram(hologram)));
    }

    /**
     * Update the hologram when the NPC moves.
     *
     * @param event The event.
     */
    @EventHandler
    public void onNPCTeleport(final NPCTeleportEvent event) {
        npcHolograms.stream()
                .filter(npcHologram -> npcHologram.npcHolograms().containsKey(event.getNPC().getId()))
                .forEach(hologram -> Bukkit.getServer().getScheduler().runTask(BetonQuest.getInstance(), () -> updateHologram(hologram)));
    }

    /**
     * Link a list of NPC IDs to a list of holograms.
     *
     * @param npcHolograms the list of NPC IDs and there linked holograms.
     * @param holograms    The holograms.
     */
    private record NPCHologram(Map<Integer, BetonHologram> npcHolograms, List<BetonHologram> holograms,
                               Vector vector, boolean follow) {
    }
}
