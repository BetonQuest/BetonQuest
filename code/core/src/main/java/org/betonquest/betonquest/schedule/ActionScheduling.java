package org.betonquest.betonquest.schedule;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.api.schedule.ScheduleID;
import org.betonquest.betonquest.api.schedule.Scheduler;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.kernel.registry.feature.ScheduleRegistry;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Class responsible for managing schedule types, their schedulers, as well as parsing schedules from config.
 */
public class ActionScheduling extends SectionProcessor<ScheduleID, Schedule> {

    /**
     * Map that contains all types of schedulers,
     * with keys being their names and values holding the scheduler and schedule class.
     */
    private final ScheduleRegistry scheduleTypes;

    /**
     * Creates a new instance of the action scheduling class.
     *
     * @param loggerFactory logger factory to use
     * @param log           the logger that will be used for logging
     * @param placeholders  the {@link Placeholders} to create and resolve placeholders
     * @param packManager   the quest package manager to get quest packages from
     * @param scheduleTypes map containing the schedule types, provided by {@link org.betonquest.betonquest.BetonQuest}
     * @param parsers       the argument parsers
     */
    public ActionScheduling(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log, final Placeholders placeholders, final QuestPackageManager packManager,
                            final ScheduleRegistry scheduleTypes, final ArgumentParsers parsers) {
        super(loggerFactory, log, placeholders, packManager, parsers, "Schedules", "schedules");
        this.scheduleTypes = scheduleTypes;
    }

    @Override
    protected Map.Entry<ScheduleID, Schedule> loadSection(final String sectionName, final SectionInstruction instruction) throws QuestException {
        final ScheduleID scheduleID = getIdentifier(instruction.getPackage(), sectionName);
        log.debug(instruction.getPackage(), "Parse schedule '" + scheduleID + "'...");
        final ScheduleType<?, ?> scheduleType = instruction.read().value("type").parse(scheduleTypes::getFactory).get().getValue(null);
        final Schedule scheduleInstance = scheduleType.createAndScheduleNewInstance(scheduleID, instruction.getSection());
        return Map.entry(scheduleID, scheduleInstance);
    }

    /**
     * Start all schedulers and activate all schedules.
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void startAll() {
        log.debug("Starting schedulers...");
        for (final ScheduleType<?, ?> type : scheduleTypes.values()) {
            try {
                type.scheduler.start();
            } catch (final Exception e) {
                log.error("Error while enabling " + type.scheduler + ": " + e.getMessage(), e);
            }
        }
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @Override
    public void clear() {
        log.debug("Stopping schedulers...");
        for (final ScheduleType<?, ?> type : scheduleTypes.values()) {
            try {
                type.scheduler.stop();
            } catch (final Exception e) {
                log.error("Error while stopping " + type.scheduler + ": " + e.getMessage(), e);
            }
        }
        super.clear();
    }

    @Override
    protected ScheduleID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new ScheduleID(packManager, pack, identifier);
    }

    /**
     * Helper class that holds all implementations needed for a specific schedule type.
     *
     * @param scheduleFactory factory of the schedule
     * @param scheduler       instance of the scheduler
     * @param <S>             type of the schedule
     * @param <T>             type of time used by the scheduler
     */
    public record ScheduleType<S extends Schedule, T>(ScheduleFactory<S> scheduleFactory, Scheduler<S, T> scheduler) {

        /* default */ S newScheduleInstance(final ScheduleID scheduleID, final ConfigurationSection scheduleConfig)
                throws QuestException {
            return scheduleFactory.createNewInstance(scheduleID, scheduleConfig);
        }

        /* default */ Schedule createAndScheduleNewInstance(final ScheduleID scheduleID, final ConfigurationSection scheduleConfig)
                throws QuestException {
            final S schedule = newScheduleInstance(scheduleID, scheduleConfig);
            scheduler.addSchedule(schedule);
            return schedule;
        }
    }
}
