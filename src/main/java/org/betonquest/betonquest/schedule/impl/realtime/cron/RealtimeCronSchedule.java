package org.betonquest.betonquest.schedule.impl.realtime.cron;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.api.schedule.CronSchedule;
import org.betonquest.betonquest.api.schedule.ScheduleID;

import java.util.List;

/**
 * Schedules events to occur at a specific real time, using cron syntax.
 */
public class RealtimeCronSchedule extends CronSchedule {

    /**
     * Creates new instance of the Cron schedule with the {@link #REBOOT_CRON_DEFINITION}.
     *
     * @param scheduleID the schedule id
     * @param events     the events to execute
     * @param catchup    the catchup strategy
     * @param expression the expression string to parse as default cron
     * @throws QuestException when the expression is invalid for the cron definition
     */
    public RealtimeCronSchedule(final ScheduleID scheduleID, final List<EventID> events, final CatchupStrategy catchup,
                                final String expression) throws QuestException {
        super(scheduleID, events, catchup, REBOOT_CRON_DEFINITION, expression);
    }
}
