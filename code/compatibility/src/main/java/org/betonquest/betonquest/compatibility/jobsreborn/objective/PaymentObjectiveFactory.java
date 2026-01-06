package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.api.JobsPaymentEvent;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;
import org.betonquest.betonquest.quest.action.NotificationLevel;

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
     * Creates a new instance of the ObjectivePaymentActionFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public PaymentObjectiveFactory(final BetonQuestLoggerFactory loggerFactory, final PluginMessage pluginMessage) {
        this.loggerFactory = loggerFactory;
        this.pluginMessage = pluginMessage;
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        final BetonQuestLogger log = loggerFactory.create(PaymentObjective.class);
        final IngameNotificationSender paymentSender = new IngameNotificationSender(log,
                pluginMessage, instruction.getPackage(), instruction.getID().getFull(),
                NotificationLevel.INFO, "payment_to_receive");
        final PaymentObjective objective = new PaymentObjective(instruction, targetAmount, paymentSender);
        service.request(JobsPaymentEvent.class).handler(objective::onJobsPaymentEvent)
                .offlinePlayer(JobsPaymentEvent::getPlayer).subscribe(true);
        return objective;
    }
}
