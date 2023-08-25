package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NoNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.betonquest.betonquest.quest.event.NotificationSender;
import org.betonquest.betonquest.utils.Utils;

import java.util.Locale;

/**
 * Factory to create points events from {@link Instruction}s.
 */
public class PointEventFactory implements EventFactory {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Create the points event factory.
     *
     * @param log the logger to use
     */
    public PointEventFactory(final BetonQuestLogger log) {
        this.log = log;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final String action = instruction.getOptional("action");
        Point type = Point.ADD;
        if (action != null) {
            try {
                type = Point.valueOf(action.toUpperCase(Locale.ROOT));
            } catch (final IllegalArgumentException e) {
                throw new InstructionParseException("Unknown modification action: " + action, e);
            }
        }
        final String categoryName = instruction.next();
        final String category = Utils.addPackage(instruction.getPackage(), categoryName);
        String number = instruction.next();
        if (!number.isEmpty() && number.charAt(0) == '*') {
            type = Point.MULTIPLY;
            number = number.replace("*", "");
        }
        if (number.isEmpty() || number.charAt(0) == '-') {
            type = Point.SUBTRACT;
            number = number.replace("-", "");
        }

        final NotificationSender pointSender;
        if (instruction.hasArgument("notify")) {
            pointSender = new IngameNotificationSender(log, instruction.getPackage(), instruction.getID().getFullID(), NotificationLevel.INFO, type.getNotifyCategory());
        } else {
            pointSender = new NoNotificationSender();
        }

        return new PointEvent(pointSender, categoryName, category, new VariableNumber(instruction.getPackage(), number), type);
    }
}
