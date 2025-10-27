package org.betonquest.betonquest.api.schedule;

import com.cronutils.model.Cron;
import com.cronutils.model.RebootCron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import org.betonquest.betonquest.api.quest.event.EventID;

import java.util.List;

/**
 * A schedule using <a href="https://crontab.guru/">cron syntax</a> for defining time instructions.
 */
public class CronSchedule extends Schedule {

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
     * Creates new instance of the Cron schedule.
     *
     * @param scheduleID    the schedule id
     * @param events        the events to execute
     * @param catchup       the catchup strategy
     * @param timeCron      the Cron to schedule execution
     * @param executionTime the time when the schedule should be executed
     */
    public CronSchedule(final ScheduleID scheduleID, final List<EventID> events, final CatchupStrategy catchup,
                        final Cron timeCron, final ExecutionTime executionTime) {
        super(scheduleID, events, catchup);
        this.timeCron = timeCron;
        this.executionTime = executionTime;
    }

    /**
     * Get the cron expression that defines when the events from this schedule shall run.
     *
     * @return parsed cron expression
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
