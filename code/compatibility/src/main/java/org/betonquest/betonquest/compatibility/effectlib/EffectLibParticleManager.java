package org.betonquest.betonquest.compatibility.effectlib;

import de.slikey.effectlib.EffectManager;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.InstructionApi;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.compatibility.effectlib.identifier.ParticleIdentifier;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Displays a particle effect at the location of an NPC or a list of locations.
 */
public class EffectLibParticleManager extends SectionProcessor<ParticleIdentifier, EffectLibRunnable> {

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
     * @param log               the custom logger for this class
     * @param loggerFactory     the logger factory to create new custom loggers
     * @param questTypeApi      the Quest Type API
     * @param featureApi        the Feature API
     * @param profileProvider   the profile provider instance
     * @param instructionApi    the instruction api to use
     * @param identifierFactory the identifier factory
     * @param manager           the effect manager starting and controlling particles
     * @param plugin            the plugin to start new tasks with
     */
    public EffectLibParticleManager(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory,
                                    final QuestTypeApi questTypeApi, final FeatureApi featureApi,
                                    final ProfileProvider profileProvider, final InstructionApi instructionApi,
                                    final IdentifierFactory<ParticleIdentifier> identifierFactory,
                                    final EffectManager manager, final Plugin plugin) {
        super(log, instructionApi, identifierFactory, "Effect", "effectlib");
        this.loggerFactory = loggerFactory;
        this.questTypeApi = questTypeApi;
        this.featureApi = featureApi;
        this.profileProvider = profileProvider;
        this.manager = manager;
        this.plugin = plugin;
    }

    @Override
    protected Map.Entry<ParticleIdentifier, EffectLibRunnable> loadSection(final String sectionName, final SectionInstruction instruction) throws QuestException {
        final Argument<String> effectClass = instruction.read().value("class").string().get();
        final Argument<Number> interval = instruction.read().value("interval").number().atLeast(1).getOptional(100);
        final Argument<Number> checkInterval = instruction.read().value("checkinterval").number().atLeast(1).getOptional(100);
        final Argument<List<Location>> locations = instruction.read().value("locations").location().list().getOptional(Collections.emptyList());
        final Argument<List<NpcIdentifier>> npcs = instruction.read().value("npcs")
                .identifier(NpcIdentifier.class).list().getOptional(Collections.emptyList());
        final Argument<List<ConditionIdentifier>> conditions = instruction.read().value("conditions")
                .identifier(ConditionIdentifier.class).list().getOptional(Collections.emptyList());

        final EffectConfiguration configuration = new EffectConfiguration(effectClass, locations, npcs, conditions, instruction.getSection(), checkInterval);
        final EffectLibRunnable libRunnable = new EffectLibRunnable(loggerFactory.create(EffectLibRunnable.class), questTypeApi, featureApi, profileProvider, manager, configuration);
        libRunnable.runTaskTimer(plugin, 1, interval.getValue(null).intValue());

        return Map.entry(getIdentifier(instruction.getPackage(), sectionName), libRunnable);
    }

    @Override
    public void clear() {
        for (final EffectLibRunnable activeParticle : values.values()) {
            activeParticle.cancel();
        }
        super.clear();
    }
}
