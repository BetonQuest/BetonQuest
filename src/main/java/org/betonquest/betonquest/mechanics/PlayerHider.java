package org.betonquest.betonquest.mechanics;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitTask;

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
     * The running hider
     */
    private final BukkitTask bukkitTask;

    /**
     * Initialize and start a new {@link PlayerHider}
     *
     * @throws InstructionParseException Thrown if there is a configuration error.
     */
    public PlayerHider() throws InstructionParseException {
        hiders = new HashMap<>();

        for (final QuestPackage pack : Config.getPackages().values()) {
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

        final long period = BetonQuest.getInstance().getPluginConfig().getLong("player_hider_check_interval", 20);
        bukkitTask = Bukkit.getScheduler().runTaskTimer(BetonQuest.getInstance(), this::updateVisibility, 1, period);
    }

    /**
     * Stops the running {@link PlayerHider}.
     */
    public void stop() {
        bukkitTask.cancel();
    }

    private ConditionID[] getConditions(final QuestPackage pack, final String key, final String rawConditions) throws InstructionParseException {
        if (rawConditions == null) {
            return new ConditionID[0];
        }
        final String[] rawConditionsList = rawConditions.split(",");
        final ConditionID[] conditionList = new ConditionID[rawConditionsList.length];
        for (int i = 0; i < rawConditionsList.length; i++) {
            try {
                conditionList[i] = new ConditionID(pack, rawConditionsList[i]);
            } catch (final ObjectNotFoundException e) {
                throw new InstructionParseException("Error while loading " + rawConditionsList[i]
                        + " condition for player_hider " + pack.getQuestPath() + "." + key + ": " + e.getMessage(), e);
            }
        }
        return conditionList;
    }

    /**
     * Trigger an update for the visibility
     */
    public void updateVisibility() {
        final Collection<? extends OnlineProfile> onlineProfiles = PlayerConverter.getOnlineProfiles();
        final Map<OnlineProfile, List<OnlineProfile>> profilesToHide = getProfilesToHide(onlineProfiles);
        for (final OnlineProfile source : onlineProfiles) {
            updateVisibilityForProfiles(onlineProfiles, source, profilesToHide.get(source));
        }
    }

    private void updateVisibilityForProfiles(final Collection<? extends OnlineProfile> onlineProfiles, final OnlineProfile source, final List<OnlineProfile> profilesToHide) {
        if (profilesToHide == null) {
            for (final OnlineProfile target : onlineProfiles) {
                source.getPlayer().showPlayer(BetonQuest.getInstance(), target.getPlayer());
            }
        } else {
            for (final OnlineProfile target : onlineProfiles) {
                if (profilesToHide.contains(target)) {
                    source.getPlayer().hidePlayer(BetonQuest.getInstance(), target.getPlayer());
                } else {
                    source.getPlayer().showPlayer(BetonQuest.getInstance(), target.getPlayer());
                }
            }
        }
    }

    private Map<OnlineProfile, List<OnlineProfile>> getProfilesToHide(final Collection<? extends OnlineProfile> onlineProfiles) {
        final Map<OnlineProfile, List<OnlineProfile>> profilesToHide = new HashMap<>();
        for (final Map.Entry<ConditionID[], ConditionID[]> hider : hiders.entrySet()) {
            final List<OnlineProfile> targetProfiles = new ArrayList<>();
            for (final OnlineProfile target : onlineProfiles) {
                if (BetonQuest.conditions(target, hider.getValue())) {
                    targetProfiles.add(target);
                }
            }
            for (final OnlineProfile source : onlineProfiles) {
                if (!BetonQuest.conditions(source, hider.getKey())) {
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
