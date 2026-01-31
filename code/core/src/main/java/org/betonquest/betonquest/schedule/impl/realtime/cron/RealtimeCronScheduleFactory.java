package org.betonquest.betonquest.schedule.impl.realtime.cron;

import com.cronutils.model.definition.CronDefinition;
import org.betonquest.betonquest.api.schedule.CronSchedule;

/**
 * Factory to create {@link CronSchedule}s with the {@link CronSchedule#REBOOT_CRON_DEFINITION}.
 */
public class RealtimeCronScheduleFactory extends CronScheduleFactory {

    /**
     * Create a new Realtime Schedule Factory.
     */
    public RealtimeCronScheduleFactory() {
        super();
    }

    @Override
    protected CronDefinition parseCronDefinition() {
        return CronSchedule.REBOOT_CRON_DEFINITION;
    }
}
