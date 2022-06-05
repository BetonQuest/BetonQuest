package org.betonquest.betonquest.api.schedule;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ScheduleID;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A schedule using <a href="https://crontab.guru/">cron syntax</a> for defining time instructions.
 */
public abstract class CronSchedule extends Schedule {

    /**
     * The unix cron syntax shall be used by default
     */
    private static final CronDefinition DEFAULT_CRON_DEFINITION = CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX);

    /**
     * Cron expression that defines when the events from this schedule shall run
     */
    protected final Cron timeCron;

    /**
     * Provides information when the events from this schedule shall be executed
     */
    protected final ExecutionTime executionTime;

    /**
     * Creates new instance of the schedule.
     * It should parse all options from the configuration section.
     * If anything goes wrong, throw {@link InstructionParseException} with an error message describing the problem.
     *
     * @param id          id of the new schedule
     * @param instruction config defining the schedule
     * @throws InstructionParseException if parsing the config failed
     */
    public CronSchedule(ScheduleID id, ConfigurationSection instruction) throws InstructionParseException {
        super(id, instruction);
        try {
            this.timeCron = new CronParser(cronDefinition()).parse(super.time).validate();
        } catch (IllegalArgumentException e) {
            throw new InstructionParseException("Time is no valid cron syntax: '" + super.time + "'", e);
        }
        this.executionTime = ExecutionTime.forCron(timeCron);
    }

    /**
     * Override this method to use a custom cron definition for this schedule
     *
     * @return {@link #DEFAULT_CRON_DEFINITION} which is the unix syntax
     */
    protected CronDefinition cronDefinition() {
        return DEFAULT_CRON_DEFINITION;
    }
}
