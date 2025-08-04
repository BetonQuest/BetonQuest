package org.betonquest.betonquest.api.quest.npc.feature;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.kernel.processor.quest.NpcProcessor;
import org.betonquest.betonquest.kernel.registry.quest.NpcTypeRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

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
     * Processor to get Npcs.
     */
    private final NpcProcessor npcProcessor;

    /**
     * The Quest Type API to check hiding conditions.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Npc types to get NpcIds from a Npc.
     */
    private final NpcTypeRegistry npcTypes;

    /**
     * Npc ids mapped to their hide conditions.
     */
    private final Map<NpcID, Set<ConditionID>> npcs;

    /**
     * The task refreshing npc visibility.
     */
    @Nullable
    private BukkitTask task;

    /**
     * Create and start a new Npc Hider.
     *
     * @param log             the custom logger for this class
     * @param npcProcessor    the processor to get nps
     * @param questTypeApi    the Quest Type API to check hiding conditions
     * @param profileProvider the profile provider instance
     * @param npcTypes        the Npc types to get NpcIds
     */
    public NpcHider(final BetonQuestLogger log, final NpcProcessor npcProcessor,
                    final QuestTypeApi questTypeApi, final ProfileProvider profileProvider, final NpcTypeRegistry npcTypes) {
        this.log = log;
        this.npcProcessor = npcProcessor;
        this.questTypeApi = questTypeApi;
        this.profileProvider = profileProvider;
        this.npcTypes = npcTypes;
        this.npcs = new HashMap<>();
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
     * @param packages       the quest packages to load
     * @param updateInterval the interval in ticks to check refresh hiding
     * @param plugin         the plugin instance to schedule update
     */
    public void reload(final Collection<QuestPackage> packages, final int updateInterval, final Plugin plugin) {
        if (task != null) {
            task.cancel();
        }
        npcs.clear();
        loadFromConfig(packages);
        task = new BukkitRunnable() {
            @Override
            public void run() {
                applyVisibility();
            }
        }.runTaskTimer(plugin, 0, updateInterval);
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
        return questTypeApi.conditions(profile, conditions);
    }

    /**
     * Allows to check if a Npc should be hidden.
     *
     * @param npc    the Npc to check
     * @param player the player to check conditions with
     * @return if the Npc is hidden with a Npc Hider
     */
    public boolean isHidden(final Npc<?> npc, final Player player) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(player);
        final Set<NpcID> identifier = npcTypes.getIdentifier(npc, onlineProfile);
        if (identifier.isEmpty()) {
            return false;
        }
        for (final NpcID npcID : identifier) {
            if (isHidden(npcID, onlineProfile)) {
                return true;
            }
        }
        return false;
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
            npc = npcProcessor.get(npcId).getNpc(onlineProfile);
        } catch (final QuestException exception) {
            log.warn("NPCHider could not update visibility for npc '" + npcId + "': " + exception.getMessage(), exception);
            return;
        }
        if (npc.isSpawned()) {
            final Set<ConditionID> conditions = npcs.get(npcId);
            if (conditions == null || conditions.isEmpty() || !questTypeApi.conditions(onlineProfile, conditions)) {
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
        for (final OnlineProfile onlineProfile : profileProvider.getOnlineProfiles()) {
            applyVisibility(onlineProfile, npcId);
        }
    }

    /**
     * Updates the visibility of all Npcs for all OnlineProfiles.
     */
    public void applyVisibility() {
        for (final OnlineProfile onlineProfile : profileProvider.getOnlineProfiles()) {
            for (final NpcID npcId : npcs.keySet()) {
                applyVisibility(onlineProfile, npcId);
            }
        }
    }
}
