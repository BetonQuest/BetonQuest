package org.betonquest.betonquest.compatibility.effectlib;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.util.DynamicLocation;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.npc.NpcManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A {@link BukkitRunnable} that shows an EffectLib effect to all players that meet the required conditions.
 */
public class EffectLibRunnable extends BukkitRunnable {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Effect manager which will create and control the particles.
     */
    private final EffectManager manager;

    /**
     * The npc manager instance to retrieve NPCs.
     */
    private final NpcManager npcManager;

    /**
     * The condition manager instance handling conditions.
     */
    private final ConditionManager conditionManager;

    /**
     * The configuration of the effect to show.
     */
    private final EffectConfiguration effectConfiguration;

    /**
     * The condition check interval in ticks.
     */
    private final int conditionCheckInterval;

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
     * @param profileProvider     the profile provider instance
     * @param manager             the effect manager which will create and control the particles
     * @param effectConfiguration the effect to show
     * @param npcManager          the npc manager instance
     * @param conditionManager    the condition manager instance
     * @throws QuestException if the condition check interval could not be parsed
     */
    public EffectLibRunnable(final BetonQuestLogger log, final ProfileProvider profileProvider,
                             final EffectManager manager, final EffectConfiguration effectConfiguration,
                             final NpcManager npcManager, final ConditionManager conditionManager) throws QuestException {
        super();
        this.log = log;
        this.profileProvider = profileProvider;
        this.manager = manager;
        this.effectConfiguration = effectConfiguration;
        this.npcManager = npcManager;
        this.conditionManager = conditionManager;
        this.activeProfiles = new ArrayList<>();
        this.conditionCheckInterval = effectConfiguration.conditionCheckInterval().getValue(null).intValue();
    }

    @Override
    public void run() {
        if (Bukkit.getCurrentTick() - lastConditionCheckTick >= conditionCheckInterval) {
            activeProfiles = checkActiveEffects();
            lastConditionCheckTick = Bukkit.getCurrentTick();
        }
        activateEffects(activeProfiles);
    }

    private List<OnlineProfile> checkActiveEffects() {
        final List<OnlineProfile> activePlayerEffects = new ArrayList<>();
        for (final OnlineProfile onlineProfile : profileProvider.getOnlineProfiles()) {
            try {
                if (conditionManager.testAll(onlineProfile, effectConfiguration.conditions().getValue(onlineProfile))) {
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
            for (final NpcIdentifier npcId : effect.npcs().getValue(profile)) {
                final Npc<?> npc;
                try {
                    npc = npcManager.get(profile, npcId);
                } catch (final QuestException exception) {
                    log.debug("Could not get Npc for id '" + npcId + "' in effects: " + exception.getMessage(), exception);
                    continue;
                }
                if (!npc.isSpawned()) {
                    continue;
                }
                final Optional<Location> location = npc.getLocation();
                if (location.isEmpty()) {
                    log.debug("Spawned Npc '" + npcId + "' has no location in effects");
                    continue;
                }
                final Player player = profile.getPlayer();

                if (!location.get().getWorld().equals(player.getWorld()) || npcManager.isHidden(npcId, profile)) {
                    continue;
                }

                manager.start(effect.effectClass().getValue(profile), effect.settings(), new NpcDynamicLocation(npc),
                        new DynamicLocation(null, null), (ConfigurationSection) null, player);
            }
        } catch (final QuestException e) {
            log.warn("Could not resolve npcs for effectlib effect '" + effect.effectClass() + "': " + e.getMessage(), e);
        }
    }

    private void runLocationEffects(final OnlineProfile profile, final EffectConfiguration effect) {
        try {
            for (final Location location : effect.locations().getValue(profile)) {
                manager.start(effect.effectClass().getValue(profile), effect.settings(), location, profile.getPlayer());
            }
        } catch (final QuestException e) {
            log.warn("Could not resolve locations for effectlib effect '" + effect.effectClass() + "': " + e.getMessage(), e);
        }
    }

    /**
     * A dynamic location that has a Npc instead a Bukkit Entity.
     */
    private static final class NpcDynamicLocation extends DynamicLocation {

        /**
         * The Npc reference.
         */
        private final WeakReference<Npc<?>> npcWeakReference;

        /**
         * Create a new Dynamic Location with a Npc as "Entity".
         *
         * @param npc the npc to get the position
         */
        private NpcDynamicLocation(final Npc<?> npc) {
            super(npc.getEyeLocation().orElse(null));
            this.npcWeakReference = new WeakReference<>(npc);
        }

        @Override
        public void update() {
            final Npc<?> entityReference = npcWeakReference.get();
            if (entityReference == null) {
                return;
            }

            final Optional<Location> location = entityReference.getEyeLocation();
            if (location.isEmpty()) {
                return;
            }
            final Location currentLocation = location.get();
            setDirection(currentLocation.getDirection());
            updateFrom(currentLocation);
        }
    }
}
