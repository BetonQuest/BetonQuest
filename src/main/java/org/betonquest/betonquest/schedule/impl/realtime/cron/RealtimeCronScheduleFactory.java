package org.betonquest.betonquest.schedule.impl.realtime.cron;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.schedule.ScheduleID;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.schedule.impl.BaseScheduleFactory;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Factory to create {@link RealtimeCronSchedule}s.
 */
public class RealtimeCronScheduleFactory extends BaseScheduleFactory<RealtimeCronSchedule> {

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
    public RealtimeCronSchedule createNewInstance(final ScheduleID scheduleID, final ConfigurationSection instruction)
            throws QuestException {
        final ScheduleData scheduleData = parseScheduleData(scheduleID.getPackage(), instruction);
        return new RealtimeCronSchedule(scheduleID, scheduleData.events(), scheduleData.catchup(), scheduleData.time());
    }
}
