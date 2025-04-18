package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;

/**
 * Factory for creating {@link PaymentObjective} instances from {@link Instruction}s.
 */
public class PaymentObjectiveFactory implements ObjectiveFactory {

    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Creates a new instance of the ObjectivePaymentEventFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public PaymentObjectiveFactory(final BetonQuestLoggerFactory loggerFactory, final PluginMessage pluginMessage) {
        this.loggerFactory = loggerFactory;
        this.pluginMessage = pluginMessage;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final VariableNumber targetAmount = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        final IngameNotificationSender paymentSender = new IngameNotificationSender(loggerFactory.create(PaymentObjective.class),
                pluginMessage, instruction.getPackage(), instruction.getID().getFullID(),
                NotificationLevel.INFO, "payment_to_receive");
        return new PaymentObjective(instruction, targetAmount, paymentSender);
    }
}
