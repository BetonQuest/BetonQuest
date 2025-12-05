package org.betonquest.betonquest.schedule.impl.realtime.cron;

import com.cronutils.model.definition.CronDefinition;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.api.schedule.CronSchedule;

/**
 * Factory to create {@link CronSchedule}s with the {@link CronSchedule#REBOOT_CRON_DEFINITION}.
 */
public class RealtimeCronScheduleFactory extends CronScheduleFactory {

    /**
     * Create a new Realtime Schedule Factory.
     *
     * @param variables   the variable processor to create and resolve variables
     * @param packManager the quest package manager to get quest packages from
     */
    public RealtimeCronScheduleFactory(final Variables variables, final QuestPackageManager packManager) {
        super(variables, packManager);
    }

    @Override
    protected CronDefinition parseCronDefinition() {
        return CronSchedule.REBOOT_CRON_DEFINITION;
    }
}
