package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableIdentifier;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NoNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.betonquest.betonquest.quest.event.NotificationSender;

import java.util.Locale;

/**
 * Factory to create points events from {@link Instruction}s.
 */
public class PointEventFactory implements PlayerEventFactory {

    /**
     * Logger factory to create a logger for the events.
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
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Create the points event factory.
     *
     * @param loggerFactory     the logger factory to create a logger for the events
     * @param variableProcessor variable processor to use
     * @param dataStorage       the storage providing player data
     * @param pluginMessage     the {@link PluginMessage} instance
     */
    public PointEventFactory(final BetonQuestLoggerFactory loggerFactory, final VariableProcessor variableProcessor,
                             final PlayerDataStorage dataStorage, final PluginMessage pluginMessage) {
        this.loggerFactory = loggerFactory;
        this.variableProcessor = variableProcessor;
        this.dataStorage = dataStorage;
        this.pluginMessage = pluginMessage;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final String action = instruction.getOptional("action");
        Point type = Point.ADD;
        if (action != null) {
            try {
                type = Point.valueOf(action.toUpperCase(Locale.ROOT));
            } catch (final IllegalArgumentException e) {
                throw new QuestException("Unknown modification action: " + action, e);
            }
        }
        final VariableIdentifier category = instruction.get(VariableIdentifier::new);
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
            pointSender = new IngameNotificationSender(loggerFactory.create(PointEvent.class), pluginMessage,
                    instruction.getPackage(), instruction.getID().getFullID(), NotificationLevel.INFO, type.getNotifyCategory());
        } else {
            pointSender = new NoNotificationSender();
        }

        final VariableNumber amount = new VariableNumber(variableProcessor, instruction.getPackage(), number);
        return new PointEvent(pointSender, category, amount, type, dataStorage);
    }
}
