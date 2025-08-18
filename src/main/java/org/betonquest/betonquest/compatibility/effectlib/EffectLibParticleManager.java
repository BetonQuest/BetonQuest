package org.betonquest.betonquest.compatibility.effectlib;

import de.slikey.effectlib.EffectManager;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a particle effect at the location of an NPC or a list of locations.
 */
public class EffectLibParticleManager {
    /**
     * The config section for the location and npc settings.
     */
    private static final String EFFECTLIB_CONFIG_SECTION = "effectlib";

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
    private final QuestTypeApi questTypeApi;

    /**
     * The Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The variable processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Effect Manager starting and controlling particles.
     */
    private final EffectManager manager;

    /**
     * All active {@link EffectLibRunnable}s managed by this class.
     */
    private final List<EffectLibRunnable> activeParticles = new ArrayList<>();

    /**
     * Loads the particle configuration and starts the effects.
     *
     * @param log               the custom logger for this class
     * @param loggerFactory     the logger factory to create new custom loggers
     * @param questTypeApi      the Quest Type API
     * @param featureApi        the Feature API
     * @param profileProvider   the profile provider instance
     * @param variableProcessor the variable processor to create new variables
     * @param manager           the effect manager starting and controlling particles
     */
    public EffectLibParticleManager(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                                    final QuestTypeApi questTypeApi, final FeatureApi featureApi,
                                    final ProfileProvider profileProvider, final VariableProcessor variableProcessor,
                                    final EffectManager manager) {
        this.loggerFactory = loggerFactory;
        this.log = log;
        this.questTypeApi = questTypeApi;
        this.featureApi = featureApi;
        this.profileProvider = profileProvider;
        this.variableProcessor = variableProcessor;
        this.manager = manager;
        loadParticleConfiguration();
    }

    @SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.CognitiveComplexity"})
    private void loadParticleConfiguration() {
        final QuestPackageManager packManager = BetonQuest.getInstance().getQuestPackageManager();
        for (final QuestPackage pack : packManager.getPackages().values()) {
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

                final Variable<List<Location>> locations = load(pack, settings, key, "locations", Argument.LOCATION);
                final Variable<List<NpcID>> npcs = load(pack, settings, key, "npcs", value -> new NpcID(packManager, pack, value));
                final Variable<List<ConditionID>> conditions = load(pack, settings, key, "conditions", value -> new ConditionID(packManager, pack, value));

                final EffectConfiguration effect = new EffectConfiguration(effectClass, locations, npcs, conditions, settings, conditionsCheckInterval);
                final EffectLibRunnable particleRunnable = new EffectLibRunnable(loggerFactory.create(EffectLibRunnable.class),
                        questTypeApi, featureApi, profileProvider, manager, effect);

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

    private <T> Variable<List<T>> load(final QuestPackage pack, final ConfigurationSection settings,
                                       final String effectKey, final String entryName,
                                       final Argument<T> argument) {
        try {
            return new VariableList<>(variableProcessor, pack, settings.getString(entryName, ""), argument);
        } catch (final QuestException exception) {
            log.warn(pack, "Could not load effectlib effect '" + effectKey + "' in package " + pack.getQuestPath() + ": "
                    + entryName + " are invalid: " + exception.getMessage());
        }
        return new VariableList<>();
    }
}
