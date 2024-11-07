package org.betonquest.betonquest.compatibility.fancynpcs;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.api.events.NpcRemoveEvent;
import de.oliver.fancynpcs.api.events.NpcSpawnEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.betonquest.betonquest.compatibility.holograms.HologramLoop;
import org.betonquest.betonquest.compatibility.holograms.HologramProvider;
import org.betonquest.betonquest.compatibility.holograms.HologramWrapper;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
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
public class FancyNpcsHologramLoop extends HologramLoop implements Listener {
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
    public FancyNpcsHologramLoop(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log) {
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
    protected List<BetonHologram> getHologramsFor(final QuestPackage pack, final ConfigurationSection section) throws InstructionParseException {
        final Vector vector = new Vector(0, 3, 0);
        final String stringVector = section.getString("vector");
        if (stringVector != null) {
            try {
                vector.add(new VariableVector(BetonQuest.getInstance().getVariableProcessor(), pack, "(" + stringVector + ")").getValue(null));
            } catch (final QuestRuntimeException | InstructionParseException e) {
                throw new InstructionParseException("Could not parse vector '" + stringVector + "': " + e.getMessage(), e);
            }
        }
        final List<String> npcIDs = getNPCs(pack, section);
        final boolean follow = section.getBoolean("follow", false);
        final Map<String, BetonHologram> npcBetonHolograms = new HashMap<>();
        final List<BetonHologram> holograms = new ArrayList<>();
        npcIDs.forEach(npcID -> {
            final Npc npc = FancyNpcsPlugin.get().getNpcManager().getNpcById(npcID);
            if (npc == null) {
                npcBetonHolograms.put(npcID, null);
            } else {
                final BetonHologram hologram = HologramProvider.getInstance()
                        .createHologram(npc.getData().getLocation().add(vector));
                npcBetonHolograms.put(npcID, hologram);
                holograms.add(hologram);
            }
        });
        npcHolograms.add(new NPCHologram(npcBetonHolograms, holograms, vector, follow));
        return holograms;
    }

    private List<String> getNPCs(final QuestPackage pack, final ConfigurationSection section) {
        final List<String> npcIDs = new ArrayList<>();
        for (final String stringID : section.getStringList("npcs")) {
            final String subst = GlobalVariableResolver.resolve(pack, stringID);
            npcIDs.add(subst);
        }
        return npcIDs;
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    private void updateHologram(final NPCHologram npcHologram) {
        npcHologram.npcHolograms().entrySet().forEach(entry -> {
                    final String npcID = entry.getKey();
                    final BetonHologram hologram = entry.getValue();
                    final Npc npc = FancyNpcsPlugin.get().getNpcManager().getNpcById(npcID);
                    if (npc == null) {
                        if (hologram == null) {
                            return;
                        }
                        hologram.disable();
                    } else {
                        final Location location = npc.getData().getLocation().add(npcHologram.vector());
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
     * Update the hologram when the NPC spawns.
     *
     * @param event The event.
     */
    @EventHandler
    public void onNPCSpawn(final NpcSpawnEvent event) {
        npcHolograms.stream()
                .filter(npcHologram -> npcHologram.npcHolograms().containsKey(event.getNpc().getData().getId()))
                .forEach(hologram -> Bukkit.getServer().getScheduler().runTask(BetonQuest.getInstance(), () -> updateHologram(hologram)));
    }

    /**
     * Update the hologram when the NPC despawns.
     *
     * @param event The event.
     */
    @EventHandler
    public void onNPCDespawn(final NpcRemoveEvent event) {
        npcHolograms.stream()
                .filter(npcHologram -> npcHologram.npcHolograms().containsKey(event.getNpc().getData().getId()))
                .forEach(hologram -> Bukkit.getServer().getScheduler().runTask(BetonQuest.getInstance(), () -> updateHologram(hologram)));
    }

    /**
     * Update the hologram when the NPC moves.
     *
     * @param event The event.
     */
    @EventHandler
    public void onNPCTeleport(final NpcModifyEvent event) {
        if (event.getModification() != NpcModifyEvent.NpcModification.LOCATION) return;
        npcHolograms.stream()
                .filter(npcHologram -> npcHologram.npcHolograms().containsKey(event.getNpc().getData().getId()))
                .forEach(hologram -> Bukkit.getServer().getScheduler().runTask(BetonQuest.getInstance(), () -> updateHologram(hologram)));
    }

    /**
     * Link a list of NPC IDs to a list of holograms.
     *
     * @param npcHolograms the list of NPC IDs and there linked holograms.
     * @param holograms    The holograms.
     */
    private record NPCHologram(Map<String, BetonHologram> npcHolograms, List<BetonHologram> holograms,
                               Vector vector, boolean follow) {
    }
}
