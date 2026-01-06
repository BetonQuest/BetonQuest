package org.betonquest.betonquest.schedule.impl.realtime.cron;

import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.schedule.CronSchedule;
import org.betonquest.betonquest.api.schedule.ScheduleID;
import org.betonquest.betonquest.schedule.impl.BaseScheduleFactory;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Schedule Factory that parses common cron schedule data.
 *
 */
public abstract class CronScheduleFactory extends BaseScheduleFactory<CronSchedule> {

    /**
     * Create a new Cron Schedule Factory to create parse common cron schedule data.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     * @param packManager  the quest package manager to get quest packages from
     */
    public CronScheduleFactory(final Placeholders placeholders, final QuestPackageManager packManager) {
        super(placeholders, packManager);
    }

    @Override
    public CronSchedule createNewInstance(final ScheduleID scheduleID, final ConfigurationSection config) throws QuestException {
        final ScheduleData scheduleData = parseScheduleData(scheduleID.getPackage(), config);
        try {
            final Cron timeCron = new CronParser(parseCronDefinition()).parse(scheduleData.time()).validate();
            final ExecutionTime executionTime = ExecutionTime.forCron(timeCron);
            return new CronSchedule(scheduleID, scheduleData.actions(), scheduleData.catchup(), timeCron, executionTime);
        } catch (final IllegalArgumentException e) {
            throw new QuestException("Time is no valid cron syntax: '" + scheduleData.time() + "'", e);
        }
    }

    /**
     * Gets the cron definition to use.
     *
     * @return the cron definition for the schedule
     */
    protected abstract CronDefinition parseCronDefinition();
}
