package org.betonquest.betonquest.quest.condition.height;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.LocationParser;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.bukkit.Bukkit;

/**
 * Factory for {@link HeightCondition}s.
 */
public class HeightConditionFactory implements PlayerConditionFactory {

    /**
     * Create the height factory.
     */
    public HeightConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> height = instruction.parse(this::parseHeight).get();
        return new OnlineConditionAdapter(new HeightCondition(height));
    }

    private Number parseHeight(final String value) throws QuestException {
        try {
            if (value.matches("-?\\d+\\.?\\d*")) {
                return Double.parseDouble(value);
            }
            return new LocationParser(Bukkit.getServer()).apply(value).getY();
        } catch (final NumberFormatException e) {
            throw new QuestException("Could not parse number: " + value, e);
        }
    }
}
