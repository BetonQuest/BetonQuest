package org.betonquest.betonquest.compatibility.effectlib;

import de.slikey.effectlib.EffectManager;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.identifier.DefaultIdentifier;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Displays a particle effect at the location of an NPC or a list of locations.
 */
public class EffectLibParticleManager extends SectionProcessor<EffectLibParticleManager.ParticleID, EffectLibRunnable> {

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
    private final Variables variables;

    /**
     * Effect Manager starting and controlling particles.
     */
    private final EffectManager manager;

    /**
     * Plugin instance to start a new task.
     */
    private final Plugin plugin;

    /**
     * Loads the particle configuration and starts the effects.
     *
     * @param log             the custom logger for this class
     * @param loggerFactory   the logger factory to create new custom loggers
     * @param packManager     the quest package manager to get quest packages from
     * @param questTypeApi    the Quest Type API
     * @param featureApi      the Feature API
     * @param profileProvider the profile provider instance
     * @param variables       the variable processor to create and resolve variables
     * @param manager         the effect manager starting and controlling particles
     * @param plugin          the plugin to start new tasks with
     */
    public EffectLibParticleManager(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                                    final QuestPackageManager packManager, final QuestTypeApi questTypeApi, final FeatureApi featureApi,
                                    final ProfileProvider profileProvider, final Variables variables,
                                    final EffectManager manager, final Plugin plugin) {
        super(log, questTypeApi.variables(), packManager, "Effect", "effectlib");
        this.loggerFactory = loggerFactory;
        this.questTypeApi = questTypeApi;
        this.featureApi = featureApi;
        this.profileProvider = profileProvider;
        this.variables = variables;
        this.manager = manager;
        this.plugin = plugin;
    }

    @Override
    protected EffectLibRunnable loadSection(final QuestPackage pack, final ConfigurationSection settings) throws QuestException {
        final String effectClass = settings.getString("class");
        if (effectClass == null) {
            throw new QuestException("No effect 'class' given.");
        }

        final int interval = settings.getInt("interval", 100);
        if (interval <= 0) {
            throw new QuestException("Effect interval must be bigger than 0.");
        }

        final int conditionsCheckInterval = settings.getInt("checkinterval", 100);
        if (conditionsCheckInterval <= 0) {
            throw new QuestException("Check interval must be bigger than 0.");
        }

        final Variable<List<Location>> locations = load(pack, settings, "locations", Argument.LOCATION);
        final Variable<List<NpcID>> npcs = load(pack, settings, "npcs", value -> new NpcID(variables, packManager, pack, value));
        final Variable<List<ConditionID>> conditions = load(pack, settings, "conditions", value -> new ConditionID(variables, packManager, pack, value));

        final EffectConfiguration effect = new EffectConfiguration(effectClass, locations, npcs, conditions, settings, conditionsCheckInterval);
        final EffectLibRunnable particleRunnable = new EffectLibRunnable(loggerFactory.create(EffectLibRunnable.class),
                questTypeApi, featureApi, profileProvider, manager, effect);

        particleRunnable.runTaskTimer(plugin, 1, interval);
        return particleRunnable;
    }

    @Override
    public void clear() {
        for (final EffectLibRunnable activeParticle : values.values()) {
            activeParticle.cancel();
        }
        super.clear();
    }

    private <T> Variable<List<T>> load(final QuestPackage pack, final ConfigurationSection settings,
                                       final String entryName, final Argument<T> argument) throws QuestException {
        return new VariableList<>(variables, pack, settings.getString(entryName, ""), argument);
    }

    @Override
    protected ParticleID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new ParticleID(packManager, pack, identifier);
    }

    /**
     * Internal identifier/key for a Particle.
     */
    protected static class ParticleID extends DefaultIdentifier {

        /**
         * Creates a new ID.
         *
         * @param packManager the quest package manager to get quest packages from
         * @param pack        the package the ID is in
         * @param identifier  the id instruction string
         * @throws QuestException if the ID could not be parsed
         */
        protected ParticleID(final QuestPackageManager packManager, @Nullable final QuestPackage pack, final String identifier) throws QuestException {
            super(packManager, pack, identifier);
        }
    }
}
