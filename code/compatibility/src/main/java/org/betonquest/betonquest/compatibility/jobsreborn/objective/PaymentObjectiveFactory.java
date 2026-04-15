package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.api.JobsPaymentEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
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
     * The {@link Localizations} instance.
     */
    private final Localizations localizations;

    /**
     * Creates a new instance of the ObjectivePaymentActionFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     * @param localizations the {@link Localizations} instance
     */
    public PaymentObjectiveFactory(final BetonQuestLoggerFactory loggerFactory, final Localizations localizations) {
        this.loggerFactory = loggerFactory;
        this.localizations = localizations;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        final BetonQuestLogger log = loggerFactory.create(PaymentObjective.class);
        final IngameNotificationSender paymentSender = new IngameNotificationSender(log,
                localizations, instruction.getPackage(), instruction.getID().getFull(),
                NotificationLevel.INFO, "payment_to_receive");
        final PaymentObjective objective = new PaymentObjective(service, targetAmount, paymentSender);
        service.request(JobsPaymentEvent.class).handler(objective::onJobsPaymentEvent)
                .offlinePlayer(JobsPaymentEvent::getPlayer).subscribe(true);
        return objective;
    }
}
