package org.betonquest.betonquest.quest.variable.location;

import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.online.OnlineVariableAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Factory to create location variables from {@link Instruction}s.
 */
public class LocationVariableFactory implements PlayerVariableFactory {

    /**
     * Create a new factory to create Location Variables.
     */
    public LocationVariableFactory() {
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        final LocationFormationMode mode;
        if (instruction.hasNext()) {
            mode = LocationFormationMode.getMode(instruction.next());
        } else {
            mode = LocationFormationMode.ULF_LONG;
        }

        final int decimalPlaces;
        if (instruction.hasNext()) {
            decimalPlaces = instruction.getInt();
        } else {
            decimalPlaces = 0;
        }

        return new OnlineVariableAdapter(new LocationVariable(mode, decimalPlaces));
    }
}
