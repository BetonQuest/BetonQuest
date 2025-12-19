package org.betonquest.betonquest.quest.variable.location;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
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
            mode = LocationFormationMode.getMode(instruction.next());
        } else {
            mode = LocationFormationMode.ULF_LONG;
        }

        final Variable<Number> decimalPlaces;
        if (instruction.hasNext()) {
            decimalPlaces = instruction.get(Argument.NUMBER);
        } else {
            decimalPlaces = new Variable<>(0);
        }

        return new OnlineVariableAdapter(new LocationVariable(mode, decimalPlaces));
    }
}
