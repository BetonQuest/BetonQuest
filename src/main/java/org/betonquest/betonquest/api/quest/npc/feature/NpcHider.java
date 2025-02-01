package org.betonquest.betonquest.api.quest.npc.feature;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.quest.registry.processor.NpcProcessor;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Hides (or shows) Npcs based on conditions defined in the {@code hide_npcs} section of a {@link QuestPackage}.
 */
public class NpcHider {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    protected final BetonQuestLogger log;

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Processor to get Npcs.
     */
    private final NpcProcessor npcProcessor;

    /**
     * The BetonQuest plugin instance used for tasks and configs.
     */
    private final BetonQuest plugin;

    /**
     * Npc ids mapped to their hide conditions.
     */
    private final Map<NpcID, Set<ConditionID>> npcs;

    /**
     * The task refreshing npc visibility.
     */
    private BukkitTask task;

    /**
     * Create and start a new Npc Hider.
     *
     * @param log          the custom logger for this class
     * @param questTypeAPI the Quest Type API
     * @param npcProcessor the processor to get nps
     * @param plugin       the plugin to get config and start the task
     * @param packages     the quest packages to load
     */
    public NpcHider(final BetonQuestLogger log, final QuestTypeAPI questTypeAPI, final NpcProcessor npcProcessor,
                    final BetonQuest plugin, final Collection<QuestPackage> packages) {
        this.log = log;
        this.npcProcessor = npcProcessor;
        this.plugin = plugin;
        this.questTypeAPI = questTypeAPI;
        this.npcs = new HashMap<>();
        load(packages);
    }

    private void load(final Collection<QuestPackage> packages) {
        final int updateInterval = plugin.getPluginConfig().getInt("npc_hider_check_interval", 5 * 20);
        loadFromConfig(packages);
        task = new BukkitRunnable() {
            @Override
            public void run() {
                applyVisibility();
            }
        }.runTaskTimer(plugin, 0, updateInterval);
    }

    private void loadFromConfig(final Collection<QuestPackage> packages) {
        for (final QuestPackage pack : packages) {
            final ConfigurationSection section = pack.getConfig().getConfigurationSection("hide_npcs");
            if (section != null) {
                addSection(pack, section);
            }
        }
    }

    private void addSection(final QuestPackage pack, final ConfigurationSection section) {
        npcs:
        for (final String idString : section.getKeys(false)) {
            final NpcID npcId;
            try {
                npcId = new NpcID(pack, idString);
            } catch (final QuestException exception) {
                log.warn(pack, "NpcId '" + idString + "' does not exist, in hide_npcs", exception);
                continue;
            }

            final Set<ConditionID> conditions = new HashSet<>();
            final String conditionsString = section.getString(idString);

            for (final String condition : conditionsString.split(",")) {
                try {
                    conditions.add(new ConditionID(pack, condition));
                } catch (final QuestException e) {
                    log.warn(pack, "Condition '" + condition + "' does not exist, in hide_npcs with ID " + idString, e);
                    continue npcs;
                }
            }

            if (npcs.containsKey(npcId)) {
                npcs.get(npcId).addAll(conditions);
            } else {
                npcs.put(npcId, conditions);
            }
        }
    }

    /**
     * Reloads the Npc Hider, restarting runnable etc.
     *
     * @param packages the quest packages to load
     */
    public void reload(final Collection<QuestPackage> packages) {
        task.cancel();
        npcs.clear();
        load(packages);
    }

    /**
     * Checks if the Npc should be invisible to the player.
     * <p>
     * This is primary used to cancel Npc spawn events.
     *
     * @param npcId   the id of the Npc
     * @param profile the profile to check conditions for
     * @return if the npc is stored and the hide conditions are met
     */
    public boolean isHidden(final NpcID npcId, final OnlineProfile profile) {
        final Set<ConditionID> conditions = npcs.get(npcId);
        if (conditions == null || conditions.isEmpty()) {
            return false;
        }
        return questTypeAPI.conditions(profile, conditions);
    }

    /**
     * Updates the visibility of the specified Npc for this player.
     *
     * @param onlineProfile the online profile of the player
     * @param npcId         the id of the Npc
     */
    public void applyVisibility(final OnlineProfile onlineProfile, final NpcID npcId) {
        final Npc<?> npc;
        try {
            npc = npcProcessor.getNpc(npcId);
        } catch (final QuestException exception) {
            log.warn("NPCHider could not update visibility for npc " + npcId.getFullID() + ": No npc with this id found!", exception);
            return;
        }
        if (npc.isSpawned()) {
            final Set<ConditionID> conditions = npcs.get(npcId);
            if (conditions == null || conditions.isEmpty() || !questTypeAPI.conditions(onlineProfile, conditions)) {
                npc.show(onlineProfile);
            } else {
                npc.hide(onlineProfile);
            }
        }
    }

    /**
     * Updates the visibility of all Npcs for this player.
     *
     * @param onlineProfile the online profile of the player
     */
    public void applyVisibility(final OnlineProfile onlineProfile) {
        for (final NpcID npcId : npcs.keySet()) {
            applyVisibility(onlineProfile, npcId);
        }
    }

    /**
     * Updates the visibility of this Npc for all players.
     *
     * @param npcId the id of the Npc
     */
    public void applyVisibility(final NpcID npcId) {
        for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
            applyVisibility(onlineProfile, npcId);
        }
    }

    /**
     * Updates the visibility of all Npcs for all OnlineProfiles.
     */
    public void applyVisibility() {
        for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
            for (final NpcID npcId : npcs.keySet()) {
                applyVisibility(onlineProfile, npcId);
            }
        }
    }
}
