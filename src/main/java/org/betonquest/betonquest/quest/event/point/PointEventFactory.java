package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.modules.data.PlayerDataStorage;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NoNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.betonquest.betonquest.quest.event.NotificationSender;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.utils.Utils;

import java.util.Locale;

/**
 * Factory to create points events from {@link Instruction}s.
 */
public class PointEventFactory implements EventFactory {

    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The variable processor to use.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Create the points event factory.
     *
     * @param loggerFactory     logger factory to use
     * @param variableProcessor variable processor to use
     * @param dataStorage       the storage providing player data
     */
    public PointEventFactory(final BetonQuestLoggerFactory loggerFactory, final VariableProcessor variableProcessor,
                             final PlayerDataStorage dataStorage) {
        this.loggerFactory = loggerFactory;
        this.variableProcessor = variableProcessor;
        this.dataStorage = dataStorage;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final String action = instruction.getOptional("action");
        Point type = Point.ADD;
        if (action != null) {
            try {
                type = Point.valueOf(action.toUpperCase(Locale.ROOT));
            } catch (final IllegalArgumentException e) {
                throw new QuestException("Unknown modification action: " + action, e);
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
            pointSender = new IngameNotificationSender(loggerFactory.create(PointEvent.class), instruction.getPackage(), instruction.getID().getFullID(), NotificationLevel.INFO, type.getNotifyCategory());
        } else {
            pointSender = new NoNotificationSender();
        }

        final VariableNumber amount = new VariableNumber(variableProcessor, instruction.getPackage(), number);
        return new PointEvent(pointSender, categoryName, category, amount, type, dataStorage);
    }
}
