package org.betonquest.betonquest.kernel.processor;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.api.quest.objective.event.DefaultObjectiveService;
import org.betonquest.betonquest.bstats.InstructionMetricsSupplier;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.EventProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.betonquest.betonquest.kernel.registry.quest.BaseQuestTypeRegistries;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Stores the active core quest type Processors to store and execute type logic.
 *
 * @param conditions   Condition logic.
 * @param events       Event logic.
 * @param objectives   Objective logic.
 * @param placeholders Placeholder logic.
 */
public record CoreQuestRegistry(
        ConditionProcessor conditions,
        EventProcessor events,
        ObjectiveProcessor objectives,
        PlaceholderProcessor placeholders
) implements QuestTypeApi {

    /**
     * Create a new Registry for storing and using Core Quest Types.
     *
     * @param loggerFactory       the logger factory used for new custom logger instances
     * @param packManager         the quest package manager to get quest packages from
     * @param questTypeRegistries the available quest types
     * @param pluginManager       the manager to register listener
     * @param scheduler           the bukkit scheduler to run sync tasks
     * @param profileProvider     the profile provider instance
     * @param plugin              the plugin instance to associate registered listener with
     * @return the newly created quest type api
     */
    public static CoreQuestRegistry create(final BetonQuestLoggerFactory loggerFactory, final QuestPackageManager packManager,
                                           final BaseQuestTypeRegistries questTypeRegistries, final PluginManager pluginManager,
                                           final BukkitScheduler scheduler, final ProfileProvider profileProvider, final Plugin plugin) {
        final PlaceholderProcessor placeholderProcessor = new PlaceholderProcessor(loggerFactory.create(PlaceholderProcessor.class),
                packManager, questTypeRegistries.placeholder(), scheduler, plugin);
        final DefaultObjectiveService objectiveService = new DefaultObjectiveService(plugin, loggerFactory, profileProvider);
        return new CoreQuestRegistry(
                new ConditionProcessor(loggerFactory.create(ConditionProcessor.class), placeholderProcessor, packManager,
                        questTypeRegistries.condition(), scheduler, plugin),
                new EventProcessor(loggerFactory.create(EventProcessor.class), placeholderProcessor, packManager,
                        questTypeRegistries.event(), scheduler, plugin),
                new ObjectiveProcessor(loggerFactory.create(ObjectiveProcessor.class), placeholderProcessor, packManager,
                        questTypeRegistries.objective(), pluginManager, objectiveService, plugin), placeholderProcessor);
    }

    /**
     * Clears the loaded Core Quest Types. Used before reloading all QuestPackages.
     */
    public void clear() {
        conditions.clear();
        events.clear();
        objectives.clear();
        placeholders.clear();
    }

    /**
     * Load all Core Quest Types from the QuestPackage.
     *
     * @param pack to load the core quest types from
     */
    public void load(final QuestPackage pack) {
        events.load(pack);
        conditions.load(pack);
        objectives.load(pack);
        placeholders.load(pack);
    }

    /**
     * Gets the bstats metric supplier for registered and active quest types.
     *
     * @return instruction metrics for core quest types
     */
    public Map<String, InstructionMetricsSupplier<? extends InstructionIdentifier>> metricsSupplier() {
        return Map.ofEntries(
                conditions.metricsSupplier(),
                events.metricsSupplier(),
                objectives.metricsSupplier(),
                placeholders.metricsSupplier()
        );
    }

    /**
     * Gets the amount of current loaded Core Quest Types with their readable name.
     *
     * @return the value size with the identifier
     */
    public String readableSize() {
        return String.join(", ", conditions.readableSize(), events.readableSize(),
                objectives.readableSize(), placeholders.readableSize());
    }

    @Override
    public boolean conditions(@Nullable final Profile profile, final Collection<ConditionID> conditionIDs) {
        return conditions().checks(profile, conditionIDs, true);
    }

    @Override
    public boolean conditionsAny(@Nullable final Profile profile, final Collection<ConditionID> conditionIDs) {
        return conditions().checks(profile, conditionIDs, false);
    }

    @Override
    public boolean condition(@Nullable final Profile profile, final ConditionID conditionID) {
        return conditions().check(profile, conditionID);
    }

    @Override
    public boolean events(@Nullable final Profile profile, final Collection<EventID> eventIDs) {
        return events().executes(profile, eventIDs);
    }

    @Override
    public boolean event(@Nullable final Profile profile, final EventID eventID) {
        return events().execute(profile, eventID);
    }

    @Override
    public void newObjective(final Profile profile, final ObjectiveID objectiveID) {
        objectives().start(profile, objectiveID);
    }

    @Override
    public void resumeObjective(final Profile profile, final ObjectiveID objectiveID, final String instruction) {
        objectives().resume(profile, objectiveID, instruction);
    }

    @Override
    public void renameObjective(final ObjectiveID name, final ObjectiveID rename) {
        objectives().renameObjective(name, rename);
    }

    @Override
    public List<DefaultObjective> getPlayerObjectives(final Profile profile) {
        return objectives().getActive(profile);
    }

    @Override
    public DefaultObjective getObjective(final ObjectiveID objectiveID) throws QuestException {
        return objectives().get(objectiveID);
    }
}
