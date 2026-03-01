package org.betonquest.betonquest.playerhider;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@link PlayerHider} can hide others, if both the source and the target {@link Profile} meet all conditions.
 */
public class PlayerHider {

    /**
     * The map's key is an array containing the source {@link Profile}'s conditions
     * and the map's value is an array containing the target {@link Profile}'s conditions.
     */
    private final Map<Collection<ConditionIdentifier>, Collection<ConditionIdentifier>> hiders;

    /**
     * Plugin instance to show/hide players.
     */
    private final Plugin plugin;

    /**
     * The logger to use.
     */
    private final BetonQuestLogger log;

    /**
     * The condition manager instance.
     */
    private final ConditionManager conditionManager;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The running hider.
     */
    @Nullable
    private BukkitTask bukkitTask;

    /**
     * Initialize and start a new {@link PlayerHider}.
     *
     * @param plugin           the plugin instance
     * @param log              the logger to use
     * @param conditionManager the condition manager instance
     * @param profileProvider  the profile provider instance
     */
    public PlayerHider(final Plugin plugin, final BetonQuestLogger log,
                       final ConditionManager conditionManager, final ProfileProvider profileProvider) {
        this.plugin = plugin;
        this.log = log;
        this.conditionManager = conditionManager;
        this.profileProvider = profileProvider;
        hiders = new HashMap<>();
    }

    /**
     * Loads the hiders from the quest packages.
     *
     * @param questPackageManager the quest package manager
     * @param instructions        the instructions instance
     */
    public void load(final QuestPackageManager questPackageManager, final Instructions instructions) {
        hiders.clear();
        try {
            for (final QuestPackage pack : questPackageManager.getPackages().values()) {
                final ConfigurationSection hiderSection = pack.getConfig().getConfigurationSection("player_hider");
                if (hiderSection == null) {
                    continue;
                }
                for (final String key : hiderSection.getKeys(false)) {
                    final String rawConditionsSource = hiderSection.getString(key + ".source_player");
                    final String rawConditionsTarget = hiderSection.getString(key + ".target_player");
                    hiders.put(getConditions(instructions, pack, key, rawConditionsSource),
                            getConditions(instructions, pack, key, rawConditionsTarget));
                }
            }
        } catch (final QuestException e) {
            log.error("Could not load player_hider: %s".formatted(e.getMessage()), e);
        }
    }

    /**
     * Starts the {@link PlayerHider}.
     *
     * @param scheduler the scheduler to use
     * @param config    the config accessor
     */
    public void start(final BukkitScheduler scheduler, final ConfigAccessor config) {
        final long period = config.getLong("hider.player_update_interval", 20);
        bukkitTask = scheduler.runTaskTimer(plugin, this::updateVisibility, 1, period);
    }

    /**
     * Stops the running {@link PlayerHider}.
     */
    public void stop() {
        if (bukkitTask == null) {
            log.debug("Attempted to stop PlayerHider, but it was not running.");
            return;
        }
        bukkitTask.cancel();
    }

    private Collection<ConditionIdentifier> getConditions(final Instructions instructions, final QuestPackage pack, final String key,
                                                          @Nullable final String rawConditions) throws QuestException {
        if (rawConditions == null || rawConditions.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return instructions.createForArgument(pack, rawConditions).identifier(ConditionIdentifier.class)
                    .list().get().getValue(null);
        } catch (final QuestException e) {
            throw new QuestException("Error while loading conditions for player_hider '" + key + "' in Package '" + pack.getQuestPath() + "': " + e.getMessage(), e);
        }
    }

    /**
     * Trigger an update for the visibility.
     */
    public void updateVisibility() {
        final Collection<? extends OnlineProfile> onlineProfiles = profileProvider.getOnlineProfiles();
        final Map<OnlineProfile, List<OnlineProfile>> profilesToHide = getProfilesToHide(onlineProfiles);
        for (final OnlineProfile source : onlineProfiles) {
            updateVisibilityForProfiles(onlineProfiles, source, profilesToHide.get(source));
        }
    }

    private void updateVisibilityForProfiles(final Collection<? extends OnlineProfile> onlineProfiles, final OnlineProfile source,
                                             @Nullable final List<OnlineProfile> profilesToHide) {
        if (profilesToHide == null) {
            for (final OnlineProfile target : onlineProfiles) {
                source.getPlayer().showPlayer(plugin, target.getPlayer());
            }
        } else {
            for (final OnlineProfile target : onlineProfiles) {
                if (profilesToHide.contains(target)) {
                    source.getPlayer().hidePlayer(plugin, target.getPlayer());
                } else {
                    source.getPlayer().showPlayer(plugin, target.getPlayer());
                }
            }
        }
    }

    private Map<OnlineProfile, List<OnlineProfile>> getProfilesToHide(final Collection<? extends OnlineProfile> onlineProfiles) {
        final Map<OnlineProfile, List<OnlineProfile>> profilesToHide = new HashMap<>();
        for (final Map.Entry<Collection<ConditionIdentifier>, Collection<ConditionIdentifier>> hider : hiders.entrySet()) {
            final List<OnlineProfile> targetProfiles = new ArrayList<>();
            for (final OnlineProfile target : onlineProfiles) {
                if (conditionManager.testAll(target, hider.getValue())) {
                    targetProfiles.add(target);
                }
            }
            for (final OnlineProfile source : onlineProfiles) {
                if (!conditionManager.testAll(source, hider.getKey())) {
                    continue;
                }
                final List<OnlineProfile> hiddenProfiles = getOrCreateProfileList(source, profilesToHide);
                hiddenProfiles.addAll(targetProfiles);
                hiddenProfiles.remove(source);
                profilesToHide.put(source, hiddenProfiles);
            }
        }
        return profilesToHide;
    }

    private List<OnlineProfile> getOrCreateProfileList(final OnlineProfile onlineProfile, final Map<OnlineProfile, List<OnlineProfile>> profilesToHide) {
        final List<OnlineProfile> profiles = profilesToHide.get(onlineProfile);
        return profiles == null ? new ArrayList<>() : profiles;
    }
}
