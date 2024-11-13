package org.betonquest.betonquest.modules.schedule;

import org.betonquest.betonquest.api.bukkit.config.custom.unmodifiable.UnmodifiableConfigurationSection;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.api.schedule.Scheduler;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;

/**
 * Class responsible for managing schedule types, their schedulers, as well as parsing schedules from config.
 */
public class EventScheduling {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Map that contains all types of schedulers,
     * with keys being their names and values holding the scheduler and schedule class.
     */
    private final Map<String, ScheduleType<?, ?>> scheduleTypes;

    /**
     * Creates a new instance of the event scheduling class.
     *
     * @param log           the logger that will be used for logging
     * @param scheduleTypes map containing the schedule types, provided by {@link org.betonquest.betonquest.BetonQuest}
     */
    public EventScheduling(final BetonQuestLogger log, final Map<String, ScheduleType<?, ?>> scheduleTypes) {
        this.log = log;
        this.scheduleTypes = scheduleTypes;
    }

    /**
     * Method used for loading all schedules of a quest package and registering them in the correct schedulers.
     *
     * @param questPackage package to load
     */
    public void loadData(final QuestPackage questPackage) {
        log.debug(questPackage, "Parsing schedules for package '" + questPackage.getQuestPath() + "'.");
        final ConfigurationSection configuration = questPackage.getConfig().getConfigurationSection("schedules");
        if (configuration == null) {
            log.debug(questPackage, "Package contains no schedules.");
            return;
        }
        for (final String key : configuration.getKeys(false)) {
            if (key.contains(" ")) {
                log.warn(questPackage,
                        "Schedule name cannot contain spaces: '" + key + "' (in " + questPackage.getQuestPath() + " package)");
                continue;
            }

            try {
                final ScheduleID scheduleID = new ScheduleID(questPackage, key);
                try {
                    final ConfigurationSection scheduleConfig = new UnmodifiableConfigurationSection(
                            questPackage.getConfig().getConfigurationSection("schedules." + scheduleID.getBaseID())
                    );
                    final String type = Optional.ofNullable(scheduleConfig.getString("type"))
                            .orElseThrow(() -> new InstructionParseException("Missing type instruction"));
                    final ScheduleType<?, ?> scheduleType = Optional.ofNullable(scheduleTypes.get(type))
                            .orElseThrow(() -> new InstructionParseException("The schedule type '" + type + "' is not defined"));
                    scheduleType.createAndScheduleNewInstance(scheduleID, scheduleConfig);
                    log.debug(questPackage, "Parsed schedule '" + scheduleID + "'.");
                } catch (final InstructionParseException e) {
                    log.warn(questPackage, "Error loading schedule '" + scheduleID + "':" + e.getMessage(), e);
                } catch (final InvocationTargetException | NoSuchMethodException | InstantiationException
                               | IllegalAccessException e) {
                    log.reportException(questPackage, e);
                }
            } catch (final ObjectNotFoundException e) {
                log.warn(questPackage, "Cannot load schedule with name '" + key + "': " + e.getMessage(), e);
            }
        }
        log.debug(questPackage, "Finished loading schedules from package '" + questPackage.getQuestPath() + "'.");
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

    /**
     * Stop all schedulers and disable all schedules.
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void stopAll() {
        log.debug("Stopping schedulers...");
        for (final ScheduleType<?, ?> type : scheduleTypes.values()) {
            try {
                type.scheduler.stop();
            } catch (final Exception e) {
                log.error("Error while enabling " + type.scheduler + ": " + e.getMessage(), e);
            }
        }
    }

    /**
     * Helper class that holds all implementations needed for a specific schedule type.
     *
     * @param scheduleClass class of the schedule
     * @param scheduler     instance of the scheduler
     * @param <S>           type of the schedule.
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    public record ScheduleType<S extends Schedule, T>(Class<S> scheduleClass, Scheduler<S, T> scheduler) {
        /* default */ S newScheduleInstance(final ScheduleID scheduleID, final ConfigurationSection scheduleConfig)
                throws InstructionParseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            try {
                return scheduleClass
                        .getConstructor(ScheduleID.class, ConfigurationSection.class)
                        .newInstance(scheduleID, scheduleConfig);
            } catch (final InvocationTargetException e) {
                if (e.getCause() instanceof final InstructionParseException cause) {
                    throw cause;
                } else {
                    throw e;
                }
            }
        }

        /* default */ void createAndScheduleNewInstance(final ScheduleID scheduleID, final ConfigurationSection scheduleConfig)
                throws InstructionParseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            scheduler.addSchedule(newScheduleInstance(scheduleID, scheduleConfig));
        }
    }
}
