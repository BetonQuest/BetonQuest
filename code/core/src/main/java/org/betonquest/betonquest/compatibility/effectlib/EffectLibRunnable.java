package org.betonquest.betonquest.compatibility.effectlib;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.util.DynamicLocation;
import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.id.NpcID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link BukkitRunnable} that shows an EffectLib effect to all players that meet the required conditions.
 */
public class EffectLibRunnable extends BukkitRunnable {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * The Feature API.
     */
    private final FeatureAPI featureAPI;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Effect manager which will create and control the particles.
     */
    private final EffectManager manager;

    /**
     * The configuration of the effect to show.
     */
    private final EffectConfiguration effectConfiguration;

    /**
     * All player profiles that meet the conditions for this classes' effect.
     */
    private List<OnlineProfile> activeProfiles;

    /**
     * The last game tick the conditions were checked on.
     */
    private int lastConditionCheckTick;

    /**
     * Constructs this runnable with the given effect.
     *
     * @param log                 the logger that will be used for logging
     * @param questTypeAPI        the Quest Type API
     * @param featureAPI          the Feature API
     * @param profileProvider     the profile provider instance
     * @param manager             the effect manager which will create and control the particles
     * @param effectConfiguration the effect to show
     */
    public EffectLibRunnable(final BetonQuestLogger log, final QuestTypeAPI questTypeAPI, final FeatureAPI featureAPI, final ProfileProvider profileProvider,
                             final EffectManager manager, final EffectConfiguration effectConfiguration) {
        super();
        this.log = log;
        this.questTypeAPI = questTypeAPI;
        this.featureAPI = featureAPI;
        this.profileProvider = profileProvider;
        this.manager = manager;
        this.effectConfiguration = effectConfiguration;
        this.activeProfiles = new ArrayList<>();
    }

    @Override
    public void run() {
        if (Bukkit.getCurrentTick() - lastConditionCheckTick >= effectConfiguration.conditionCheckInterval()) {
            activeProfiles = checkActiveEffects();
            lastConditionCheckTick = Bukkit.getCurrentTick();
        }
        activateEffects(activeProfiles);
    }

    private List<OnlineProfile> checkActiveEffects() {
        final List<OnlineProfile> activePlayerEffects = new ArrayList<>();
        for (final OnlineProfile onlineProfile : profileProvider.getOnlineProfiles()) {
            try {
                if (questTypeAPI.conditions(onlineProfile, effectConfiguration.conditions().getValue(onlineProfile))) {
                    activePlayerEffects.add(onlineProfile);
                }
            } catch (final QuestException e) {
                log.warn("Could not check conditions for effectlib effect '" + effectConfiguration.effectClass()
                        + "' for profile '" + onlineProfile + "': " + e.getMessage(), e);
            }
        }
        return activePlayerEffects;
    }

    private void activateEffects(final List<OnlineProfile> activePlayers) {
        for (final OnlineProfile currentPlayer : activePlayers) {
            runNPCEffects(effectConfiguration, currentPlayer);
            runLocationEffects(currentPlayer, effectConfiguration);
        }
    }

    private void runNPCEffects(final EffectConfiguration effect, final OnlineProfile profile) {
        try {
            for (final NpcID npcId : effect.npcs().getValue(profile)) {
                final Npc<?> npc;
                try {
                    npc = featureAPI.getNpc(npcId, profile);
                } catch (final QuestException exception) {
                    log.debug("Could not get Npc for id '" + npcId.getFullID() + "' in effects!", exception);
                    continue;
                }
                final Player player = profile.getPlayer();

                if (!npc.getLocation().getWorld().equals(player.getWorld()) || featureAPI.getNpcHider().isHidden(npcId, profile)) {
                    continue;
                }

                manager.start(effect.effectClass(), effect.settings(), new NpcDynamicLocation(npc),
                        new DynamicLocation(null, null), (ConfigurationSection) null, player);
            }
        } catch (final QuestException e) {
            log.warn("Could not resolve npcs for effectlib effect '" + effect.effectClass() + "': " + e.getMessage(), e);
        }
    }

    private void runLocationEffects(final OnlineProfile profile, final EffectConfiguration effect) {
        try {
            for (final Location location : effect.locations().getValue(profile)) {
                manager.start(effect.effectClass(), effect.settings(), location, profile.getPlayer());
            }
        } catch (final QuestException e) {
            log.warn("Could not resolve locations for effectlib effect '" + effect.effectClass() + "': " + e.getMessage(), e);
        }
    }

    /**
     * A dynamic location that has a Npc instead a Bukkit Entity.
     */
    private static class NpcDynamicLocation extends DynamicLocation {
        /**
         * The Npc reference.
         */
        private final WeakReference<Npc<?>> npcWeakReference;

        /**
         * Create a new Dynamic Location with a Npc as "Entity".
         *
         * @param npc the npc to get the position
         */
        public NpcDynamicLocation(final Npc<?> npc) {
            super(npc.getEyeLocation());
            this.npcWeakReference = new WeakReference<>(npc);
        }

        @Override
        public void update() {
            final Npc<?> entityReference = npcWeakReference.get();
            if (entityReference == null) {
                return;
            }

            final Location currentLocation = entityReference.getEyeLocation();
            setDirection(currentLocation.getDirection());
            updateFrom(currentLocation);
        }
    }
}
