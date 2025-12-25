package org.betonquest.betonquest.quest.variable.location;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.DefaultVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.online.OnlineVariableAdapter;

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
            mode = LocationFormationMode.getMode(instruction.nextElement());
        } else {
            mode = LocationFormationMode.ULF_LONG;
        }

        final Argument<Number> decimalPlaces;
        if (instruction.hasNext()) {
            decimalPlaces = instruction.number().get();
        } else {
            decimalPlaces = new DefaultVariable<>(0);
        }

        return new OnlineVariableAdapter(new LocationVariable(mode, decimalPlaces));
    }
}
