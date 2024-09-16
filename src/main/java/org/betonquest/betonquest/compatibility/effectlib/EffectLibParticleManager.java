package org.betonquest.betonquest.compatibility.effectlib;

import de.slikey.effectlib.EffectManager;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.kernel.processor.quest.NpcProcessor;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Displays a particle above NPCs with conversations.
 */
public class EffectLibParticleManager {
    /**
     * The config section for the location and npc settings.
     */
    private static final String EFFECTLIB_CONFIG_SECTION = "effectlib";

    /**
     * The config section for the npcs.
     */
    private static final String NPCS_CONFIG_SECTION = "npcs";

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Effect Manager starting and controlling particles.
     */
    private final EffectManager manager;

    /**
     * Processor to get npc.
     */
    private final NpcProcessor npcProcessor;

    /**
     * All active {@link EffectLibRunnable}s managed by this class.
     */
    private final List<EffectLibRunnable> activeParticles = new ArrayList<>();

    /**
     * Loads the particle configuration and starts the effects.
     *
     * @param log             the custom logger for this class
     * @param loggerFactory   the logger factory to create new custom loggers
     * @param questTypeAPI    the Quest Type API
     * @param profileProvider the profile provider instance
     * @param manager         the effect manager starting and controlling particles
     * @param npcProcessor    the processor to get npc
     */
    public EffectLibParticleManager(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                                    final QuestTypeAPI questTypeAPI, final ProfileProvider profileProvider, final EffectManager manager,
                                    final NpcProcessor npcProcessor) {
        this.loggerFactory = loggerFactory;
        this.log = log;
        this.questTypeAPI = questTypeAPI;
        this.profileProvider = profileProvider;
        this.manager = manager;
        this.npcProcessor = npcProcessor;
        loadParticleConfiguration();
    }

    @SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.CognitiveComplexity"})
    private void loadParticleConfiguration() {
        for (final QuestPackage pack : Config.getPackages().values()) {
            final ConfigurationSection section = pack.getConfig().getConfigurationSection(EFFECTLIB_CONFIG_SECTION);
            if (section == null) {
                continue;
            }

            for (final String key : section.getKeys(false)) {
                final ConfigurationSection settings = section.getConfigurationSection(key);
                if (settings == null) {
                    continue;
                }

                final String effectClass = settings.getString("class");
                if (effectClass == null) {
                    log.warn(pack, "Could not load npc effect '" + key + "' in package " + pack.getQuestPath() + ": "
                            + "No effect class given.");
                    continue;
                }

                final int interval = settings.getInt("interval", 100);
                if (interval <= 0) {
                    log.warn(pack, "Could not load npc effect '" + key + "' in package " + pack.getQuestPath() + ": "
                            + "Effect interval must be bigger than 0.");
                    continue;
                }

                final int conditionsCheckInterval = settings.getInt("checkinterval", 100);
                if (conditionsCheckInterval <= 0) {
                    log.warn(pack, "Could not load npc effect '" + key + "' in package " + pack.getQuestPath() + ": "
                            + "Check interval must be bigger than 0.");
                    continue;
                }

                final Set<NpcID> npcs = loadNpcs(settings, pack);
                final List<VariableLocation> locations = loadLocations(pack, settings, key);
                final List<ConditionID> conditions = loadConditions(pack, key, settings);

                final EffectConfiguration effect = new EffectConfiguration(effectClass, locations, npcs, conditions, settings, conditionsCheckInterval);
                final EffectLibRunnable particleRunnable = new EffectLibRunnable(loggerFactory.create(EffectLibRunnable.class),
                        questTypeAPI, profileProvider, manager, effect, npcProcessor);

                activeParticles.add(particleRunnable);
                particleRunnable.runTaskTimer(BetonQuest.getInstance(), 1, interval);
            }
        }
    }

    /**
     * Reloads the particle effect.
     */
    public void reload() {
        for (final EffectLibRunnable activeParticle : activeParticles) {
            activeParticle.cancel();
        }
        activeParticles.clear();
        loadParticleConfiguration();
    }

    private List<VariableLocation> loadLocations(final QuestPackage pack, final ConfigurationSection settings, final String key) {
        final List<VariableLocation> locations = new ArrayList<>();
        if (settings.isList("locations")) {
            for (final String rawLocation : settings.getStringList("locations")) {
                if (rawLocation == null) {
                    continue;
                }
                try {
                    locations.add(new VariableLocation(BetonQuest.getInstance().getVariableProcessor(), pack, GlobalVariableResolver.resolve(pack, rawLocation)));
                } catch (final QuestException exception) {
                    log.warn(pack, "Could not load npc effect '" + key + "' in package " + pack.getQuestPath() + ": "
                            + "Location is invalid:" + exception.getMessage());
                }
            }
        }
        return locations;
    }

    private List<ConditionID> loadConditions(final QuestPackage pack, final String key, final ConfigurationSection settings) {
        final List<ConditionID> conditions = new ArrayList<>();
        for (final String rawConditionID : settings.getStringList("conditions")) {
            try {
                conditions.add(new ConditionID(pack, GlobalVariableResolver.resolve(pack, rawConditionID)));
            } catch (final QuestException exception) {
                log.warn(pack, "Error while loading npc_effects '" + key + "': " + exception.getMessage(), exception);
            }
        }
        return conditions;
    }

    private Set<NpcID> loadNpcs(final ConfigurationSection settings, final QuestPackage pack) {
        final Set<NpcID> npcs = new HashSet<>();
        if (settings.isList(NPCS_CONFIG_SECTION)) {
            final List<String> rawIds = settings.getStringList(NPCS_CONFIG_SECTION);
            for (final String rawId : rawIds) {
                try {
                    npcs.add(new NpcID(pack, GlobalVariableResolver.resolve(pack, rawId)));
                } catch (final QuestException exception) {
                    log.warn(pack, "Error while loading npc id '" + rawId + "': " + exception.getMessage(), exception);
                }
            }
        }
        return npcs;
    }
}
