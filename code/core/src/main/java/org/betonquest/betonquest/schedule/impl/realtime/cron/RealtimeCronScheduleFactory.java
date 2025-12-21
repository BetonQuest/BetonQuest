package org.betonquest.betonquest.schedule.impl.realtime.cron;

import com.cronutils.model.definition.CronDefinition;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.schedule.CronSchedule;

/**
 * Factory to create {@link CronSchedule}s with the {@link CronSchedule#REBOOT_CRON_DEFINITION}.
 */
public class RealtimeCronScheduleFactory extends CronScheduleFactory {

    /**
     * Create a new Realtime Schedule Factory.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     * @param packManager  the quest package manager to get quest packages from
     */
    public RealtimeCronScheduleFactory(final Placeholders placeholders, final QuestPackageManager packManager) {
        super(placeholders, packManager);
    }

    @Override
    protected CronDefinition parseCronDefinition() {
        return CronSchedule.REBOOT_CRON_DEFINITION;
    }
}
