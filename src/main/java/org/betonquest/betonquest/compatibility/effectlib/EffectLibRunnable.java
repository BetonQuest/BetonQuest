package org.betonquest.betonquest.compatibility.effectlib;

import de.slikey.effectlib.util.DynamicLocation;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.compatibility.protocollib.hider.NPCHider;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
     * The configuration of the effect to show.
     */
    private final EffectConfiguration effectConfiguration;

    /**
     * All player profiles that meet the conditions for this classes' effect.
     */
    private List<OnlineProfile> activeProfiles;

    /**
     * The last game tick the conditions were checked on;
     */
    private int lastConditionCheckTick;

    /**
     * Constructs this runnable with the given effect.
     *
     * @param log                 the logger that will be used for logging
     * @param effectConfiguration the effect to show.
     */
    public EffectLibRunnable(final BetonQuestLogger log, final EffectConfiguration effectConfiguration) {
        super();
        this.log = log;
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
        for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
            if (!BetonQuest.conditions(onlineProfile, effectConfiguration.conditions())) {
                continue;
            }
            activePlayerEffects.add(onlineProfile);
        }
        return activePlayerEffects;
    }

    private void activateEffects(final List<OnlineProfile> activePlayers) {
        for (final OnlineProfile currentPlayer : activePlayers) {
            if (!effectConfiguration.npcs().isEmpty()) {
                runNPCEffects(currentPlayer, effectConfiguration);
            }
            if (!effectConfiguration.locations().isEmpty()) {
                runLocationEffects(currentPlayer, effectConfiguration);
            }
        }
    }

    private void runNPCEffects(final OnlineProfile profile, final EffectConfiguration effect) {
        for (final Integer npcId : effect.npcs()) {
            final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
            final Player player = profile.getPlayer();

            if (npc == null || !npc.getStoredLocation().getWorld().equals(player.getWorld())
                    || NPCHider.getInstance() != null && NPCHider.getInstance().isInvisible(profile, npc)) {
                continue;
            }

            EffectLibIntegrator.getEffectManager().start(effect.effectClass(), effect.settings(), new DynamicLocation(npc.getEntity()),
                    new DynamicLocation(null, null), (ConfigurationSection) null, player);
        }
    }

    private void runLocationEffects(final OnlineProfile profile, final EffectConfiguration effect) {
        for (final VariableLocation compoundLocation : effect.locations()) {
            final Location location;
            try {
                location = compoundLocation.getValue(profile);
                EffectLibIntegrator.getEffectManager().start(effect.effectClass(), effect.settings(), location, profile.getPlayer());
            } catch (final QuestRuntimeException exception) {
                log.warn("Error while resolving a location of an EffectLib particle effect of type '" + effect.effectClass() + "'. Check that your location (variables) are correct. Error:", exception);
            }
        }
    }
}
