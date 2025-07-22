package org.betonquest.betonquest.api.schedule;

import com.cronutils.model.Cron;
import com.cronutils.model.RebootCron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.schedule.ScheduleID;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A schedule using <a href="https://crontab.guru/">cron syntax</a> for defining time instructions.
 */
public abstract class CronSchedule extends Schedule {

    /**
     * The unix cron syntax shall be used by default.
     * {@code @reboot} nickname is not supported by default.
     */
    public static final CronDefinition DEFAULT_CRON_DEFINITION = CronDefinitionBuilder.defineCron()
            .withMinutes().withValidRange(0, 59).withStrictRange().and()
            .withHours().withValidRange(0, 23).withStrictRange().and()
            .withDayOfMonth().withValidRange(1, 31).withStrictRange().and()
            .withMonth().withValidRange(1, 12).withStrictRange().and()
            .withDayOfWeek().withValidRange(0, 7).withMondayDoWValue(1).withIntMapping(7, 0).withStrictRange().and()
            .withSupportedNicknameYearly().withSupportedNicknameAnnually()
            .withSupportedNicknameMonthly().withSupportedNicknameWeekly()
            .withSupportedNicknameMidnight().withSupportedNicknameDaily()
            .withSupportedNicknameHourly()
            .instance();

    /**
     * The unix cron syntax but with support for {@code @reboot} nickname.
     */
    public static final CronDefinition REBOOT_CRON_DEFINITION = CronDefinitionBuilder.defineCron()
            .withMinutes().withValidRange(0, 59).withStrictRange().and()
            .withHours().withValidRange(0, 23).withStrictRange().and()
            .withDayOfMonth().withValidRange(1, 31).withStrictRange().and()
            .withMonth().withValidRange(1, 12).withStrictRange().and()
            .withDayOfWeek().withValidRange(0, 7).withMondayDoWValue(1).withIntMapping(7, 0).withStrictRange().and()
            .withSupportedNicknameYearly().withSupportedNicknameAnnually()
            .withSupportedNicknameMonthly().withSupportedNicknameWeekly()
            .withSupportedNicknameMidnight().withSupportedNicknameDaily()
            .withSupportedNicknameHourly()
            .withSupportedNicknameReboot()
            .instance();

    /**
     * Cron expression that defines when the events from this schedule shall run.
     */
    protected final Cron timeCron;

    /**
     * Provides information when the events from this schedule shall be executed.
     */
    protected final ExecutionTime executionTime;

    /**
     * Creates new instance of the schedule.
     * It should parse all options from the configuration section.
     * If anything goes wrong, throw {@link QuestException} with an error message describing the problem.
     *
     * @param scheduleId  id of the new schedule
     * @param instruction config defining the schedule
     * @throws QuestException if parsing the config failed
     */
    public CronSchedule(final ScheduleID scheduleId, final ConfigurationSection instruction) throws QuestException {
        this(scheduleId, instruction, DEFAULT_CRON_DEFINITION);
    }

    /**
     * Alternative constructor that provides a way to use a custom cron syntax for this schedule.
     * <b>Make sure to create a constructor with the following two arguments when extending this class:</b>
     * {@code ScheduleID id, ConfigurationSection instruction}
     *
     * @param scheduleID     id of the new schedule
     * @param instruction    config defining the schedule
     * @param cronDefinition a custom cron syntax, you may use {@link #DEFAULT_CRON_DEFINITION}
     * @throws QuestException if parsing the config failed
     */
    protected CronSchedule(final ScheduleID scheduleID, final ConfigurationSection instruction,
                           final CronDefinition cronDefinition) throws QuestException {
        super(scheduleID, instruction);
        try {
            this.timeCron = new CronParser(cronDefinition).parse(super.time).validate();
            this.executionTime = ExecutionTime.forCron(timeCron);
        } catch (final IllegalArgumentException e) {
            throw new QuestException("Time is no valid cron syntax: '" + super.time + "'", e);
        }
    }

    /**
     * Get the cron expression that defines when the events from this schedule shall run.
     *
     * @return cron expression parsed from {@link #getTime()} string
     */
    public Cron getTimeCron() {
        return timeCron;
    }

    /**
     * Check if the schedule should run on reboot.
     * If {@code  @reboot} statement is not supported by this schedule type this method will return false.
     *
     * @return true if schedule should run on reboot, false otherwise
     */
    public boolean shouldRunOnReboot() {
        return timeCron instanceof RebootCron;
    }

    /**
     * Get information when the events from this schedule shall be executed.
     *
     * @return execution time helper as defined by cron-utils
     */
    public ExecutionTime getExecutionTime() {
        return executionTime;
    }
}
