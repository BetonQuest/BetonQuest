package org.betonquest.betonquest.playerhider;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
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
    private final Map<ConditionID[], ConditionID[]> hiders;

    /**
     * The running hider.
     */
    private final BukkitTask bukkitTask;

    /**
     * Plugin instance to show/hide players.
     */
    private final Plugin plugin;

    /**
     * The BetonQuest API instance.
     */
    private final BetonQuestApi api;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Initialize and start a new {@link PlayerHider}.
     *
     * @param betonQuest      the plugin instance to get config and start the bukkit task
     * @param api             the BetonQuest API instance
     * @param profileProvider the profile provider instance
     * @throws QuestException Thrown if there is a configuration error.
     */
    public PlayerHider(final BetonQuest betonQuest, final BetonQuestApi api, final ProfileProvider profileProvider) throws QuestException {
        this.plugin = betonQuest;
        this.profileProvider = profileProvider;
        hiders = new HashMap<>();
        this.api = api;

        for (final QuestPackage pack : betonQuest.getQuestPackageManager().getPackages().values()) {
            final ConfigurationSection hiderSection = pack.getConfig().getConfigurationSection("player_hider");
            if (hiderSection == null) {
                continue;
            }
            for (final String key : hiderSection.getKeys(false)) {
                final String rawConditionsSource = hiderSection.getString(key + ".source_player");
                final String rawConditionsTarget = hiderSection.getString(key + ".target_player");
                hiders.put(getConditions(pack, key, rawConditionsSource), getConditions(pack, key, rawConditionsTarget));
            }
        }

        final long period = betonQuest.getPluginConfig().getLong("hider.player_update_interval", 20);
        bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, this::updateVisibility, 1, period);
    }

    /**
     * Stops the running {@link PlayerHider}.
     */
    public void stop() {
        bukkitTask.cancel();
    }

    private ConditionID[] getConditions(final QuestPackage pack, final String key, @Nullable final String rawConditions) throws QuestException {
        if (rawConditions == null) {
            return new ConditionID[0];
        }
        final String[] rawConditionsList = rawConditions.split(",");
        final ConditionID[] conditionList = new ConditionID[rawConditionsList.length];
        for (int i = 0; i < rawConditionsList.length; i++) {
            try {
                conditionList[i] = new ConditionID(api.getQuestPackageManager(), pack, rawConditionsList[i]);
            } catch (final QuestException e) {
                throw new QuestException("Error while loading " + rawConditionsList[i]
                        + " condition for player_hider " + pack.getQuestPath() + "." + key + ": " + e.getMessage(), e);
            }
        }
        return conditionList;
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
        for (final Map.Entry<ConditionID[], ConditionID[]> hider : hiders.entrySet()) {
            final List<OnlineProfile> targetProfiles = new ArrayList<>();
            for (final OnlineProfile target : onlineProfiles) {
                if (api.getQuestTypeApi().conditions(target, hider.getValue())) {
                    targetProfiles.add(target);
                }
            }
            for (final OnlineProfile source : onlineProfiles) {
                if (!api.getQuestTypeApi().conditions(source, hider.getKey())) {
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
