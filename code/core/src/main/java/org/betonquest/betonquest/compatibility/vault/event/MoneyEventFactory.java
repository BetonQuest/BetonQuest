package org.betonquest.betonquest.compatibility.vault.event;

import net.milkbowl.vault.economy.Economy;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;

/**
 * Factory to create {@link MoneyEvent}s from {@link Instruction}s.
 */
public class MoneyEventFactory implements PlayerEventFactory {
    /**
     * Economy where the balance will be modified.
     */
    private final Economy economy;

    /**
     * Logger factory to create new logger instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create a new Factory to create Vault Money Events.
     *
     * @param economy           the economy where the balance will be modified
     * @param loggerFactory     the logger factory to create new logger instances.
     * @param data              the data used for primary server access
     * @param pluginMessage     the {@link PluginMessage} instance
     * @param variableProcessor the processor to create new variables
     */
    public MoneyEventFactory(final Economy economy, final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data,
                             final PluginMessage pluginMessage, final VariableProcessor variableProcessor) {
        this.economy = economy;
        this.loggerFactory = loggerFactory;
        this.data = data;
        this.pluginMessage = pluginMessage;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        String string = instruction.next();
        final boolean multi;
        if (!string.isEmpty() && string.charAt(0) == '*') {
            multi = true;
            string = string.replace("*", "");
        } else {
            multi = false;
        }
        final Variable<Number> amount;
        try {
            amount = new Variable<>(variableProcessor, instruction.getPackage(), string, Argument.NUMBER);
        } catch (final QuestException e) {
            throw new QuestException("Could not parse money amount: " + e.getMessage(), e);
        }
        final boolean notify = instruction.hasArgument("notify");
        final IngameNotificationSender givenSender;
        final IngameNotificationSender takenSender;
        if (notify) {
            final QuestPackage pack = instruction.getPackage();
            final String fullID = instruction.getID().getFull();
            final BetonQuestLogger log = loggerFactory.create(MoneyEvent.class);
            givenSender = new IngameNotificationSender(log, pluginMessage, pack, fullID, NotificationLevel.INFO, "money_given");
            takenSender = new IngameNotificationSender(log, pluginMessage, pack, fullID, NotificationLevel.INFO, "money_taken");
        } else {
            givenSender = null;
            takenSender = null;
        }

        final PlayerEvent money = new MoneyEvent(economy, amount, multi, givenSender, takenSender);
        return new PrimaryServerThreadEvent(money, data);
    }
}
