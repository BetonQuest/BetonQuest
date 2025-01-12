package org.betonquest.betonquest.compatibility.vault.event;

import net.milkbowl.vault.economy.Economy;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;

/**
 * Factory to create {@link MoneyEvent}s from {@link Instruction}s.
 */
public class MoneyEventFactory implements EventFactory {
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
     * Processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create a new Factory to create Vault Money Events.
     *
     * @param economy           the economy where the balance will be modified
     * @param loggerFactory     the logger factory to create new logger instances.
     * @param data              the data used for primary server access
     * @param variableProcessor the processor to create new variables
     */
    public MoneyEventFactory(final Economy economy, final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data,
                             final VariableProcessor variableProcessor) {
        this.economy = economy;
        this.loggerFactory = loggerFactory;
        this.data = data;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        String string = instruction.next();
        final boolean multi;
        if (!string.isEmpty() && string.charAt(0) == '*') {
            multi = true;
            string = string.replace("*", "");
        } else {
            multi = false;
        }
        final VariableNumber amount;
        try {
            amount = new VariableNumber(variableProcessor, instruction.getPackage(), string);
        } catch (final QuestException e) {
            throw new QuestException("Could not parse money amount: " + e.getMessage(), e);
        }
        final boolean notify = instruction.hasArgument("notify");
        final IngameNotificationSender givenSender;
        final IngameNotificationSender takenSender;
        if (notify) {
            final QuestPackage pack = instruction.getPackage();
            final String fullID = instruction.getID().getFullID();
            final BetonQuestLogger log = loggerFactory.create(MoneyEvent.class);
            givenSender = new IngameNotificationSender(log, pack, fullID, NotificationLevel.INFO, "money_given");
            takenSender = new IngameNotificationSender(log, pack, fullID, NotificationLevel.INFO, "money_taken");
        } else {
            givenSender = null;
            takenSender = null;
        }

        final Event money = new MoneyEvent(economy, amount, multi, givenSender, takenSender);
        return new PrimaryServerThreadEvent(money, data);
    }
}
