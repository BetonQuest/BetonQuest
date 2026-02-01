package org.betonquest.betonquest.quest.placeholder.location;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.OnlinePlaceholderAdapter;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;

/**
 * Factory to create location placeholders from {@link Instruction}s.
 */
public class LocationPlaceholderFactory implements PlayerPlaceholderFactory {

    /**
     * Create a new factory to create Location Placeholders.
     */
    public LocationPlaceholderFactory() {
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
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
            decimalPlaces = new DefaultArgument<>(0);
        }

        return new OnlinePlaceholderAdapter(new LocationPlaceholder(mode, decimalPlaces));
    }
}
