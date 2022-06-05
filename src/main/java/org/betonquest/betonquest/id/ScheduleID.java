package org.betonquest.betonquest.id;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 * ID identifying a {@link Schedule}
 */
public class ScheduleID extends ID {

    /**
     * Construct a new ScheduleID in the given package from the provided identifier
     *
     * @param pack       package where the id is defined
     * @param identifier string that defines the id
     * @throws ObjectNotFoundException if no schedule with this id exists
     */
    public ScheduleID(QuestPackage pack, String identifier) throws ObjectNotFoundException {
        super(pack, identifier);
        try {
            if (getPackage().getConfig().getSourceConfigurationSection("schedules." + getBaseID()) == null) {
                throw new ObjectNotFoundException("Schedule '" + getFullID() + "' is not defined");
            }
        } catch (InvalidConfigurationException e) {
            throw new ObjectNotFoundException("Multiple schedules with id '" + this + "' exist in the same package!", e);
        }
    }

    /**
     * Schedules are defined as configuration section and therefore can't provide an instruction.
     * Therefore, this method will always throw an UnsupportedOperationException.
     *
     * @return nothing
     * @throws UnsupportedOperationException as described above
     */
    @Override
    public Instruction generateInstruction() {
        throw new UnsupportedOperationException("Schedules do not provide a single instruction string, instead they are defined as configuration section");
    }
}
