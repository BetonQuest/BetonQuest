package org.betonquest.betonquest.schedule.impl.realtime.cron;

import com.cronutils.model.definition.CronDefinition;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.schedule.CronSchedule;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;

/**
 * Factory to create {@link CronSchedule}s with the {@link CronSchedule#REBOOT_CRON_DEFINITION}.
 */
public class RealtimeCronScheduleFactory extends CronScheduleFactory {

    /**
     * Create a new Realtime Schedule Factory.
     *
     * @param variableProcessor the variable processor to create new variables
     * @param packManager       the quest package manager to get quest packages from
     */
    public RealtimeCronScheduleFactory(final VariableProcessor variableProcessor, final QuestPackageManager packManager) {
        super(variableProcessor, packManager);
    }

    @Override
    protected CronDefinition parseCronDefinition() {
        return CronSchedule.REBOOT_CRON_DEFINITION;
    }
}
