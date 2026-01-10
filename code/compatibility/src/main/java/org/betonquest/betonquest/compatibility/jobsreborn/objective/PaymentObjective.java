package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.api.JobsPaymentEvent;
import com.gamingmesh.jobs.container.CurrencyType;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.parser.NumberParser;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveProperties;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;

/**
 * Objective that tracks the payment received by a player.
 */
public class PaymentObjective extends DefaultObjective {

    /**
     * The target amount of money to be received.
     */
    private final Argument<Number> targetAmount;

    /**
     * The {@link IngameNotificationSender} to send notifications.
     */
    private final IngameNotificationSender paymentSender;

    /**
     * Constructor for the PaymentObjective.
     *
     * @param service       the objective factory service
     * @param targetAmount  the target amount of money to be received
     * @param paymentSender the {@link IngameNotificationSender} to send notifications
     * @throws QuestException if the instruction is invalid
     */
    public PaymentObjective(final ObjectiveService service, final Argument<Number> targetAmount, final IngameNotificationSender paymentSender) throws QuestException {
        super(service);
        this.targetAmount = targetAmount;
        this.paymentSender = paymentSender;
        service.setDefaultData(this::getDefaultDataInstruction);
        final ObjectiveProperties properties = service.getProperties();
        properties.setProperty("amount", profile -> String.valueOf(getAmount(profile)));
        properties.setProperty("left", profile -> String.valueOf(getRemainingAmount(profile)));
        properties.setProperty("total", profile -> targetAmount.getValue(profile).toString());
    }

    /**
     * Check if the player has received a payment.
     *
     * @param event   the event that triggered the payment
     * @param profile the profile of the player that received the payment
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onJobsPaymentEvent(final JobsPaymentEvent event, final Profile profile) throws QuestException {
        final double previousAmount = getAmount(profile);
        add(profile, event.get(CurrencyType.MONEY));

        if (isCompleted(profile)) {
            getService().complete(profile);
            return;
        }
        final int interval = getService().getServiceDataProvider().getNotificationInterval(profile);
        if (interval > 0 && ((int) getAmount(profile)) / interval != ((int) previousAmount) / interval && profile.getOnlineProfile().isPresent()) {
            paymentSender.sendNotification(profile,
                    new VariableReplacement("amount", Component.text(getRemainingAmount(profile))));
        }
    }

    private String getDefaultDataInstruction(final Profile profile) throws QuestException {
        return String.valueOf(targetAmount.getValue(profile).doubleValue());
    }

    private double getRemainingAmount(final Profile profile) throws QuestException {
        return targetAmount.getValue(profile).doubleValue() - getAmount(profile);
    }

    private boolean isCompleted(final Profile profile) throws QuestException {
        return getAmount(profile) >= targetAmount.getValue(profile).doubleValue();
    }

    private void add(final Profile profile, final double amount) throws QuestException {
        final double newAmount = getAmount(profile) + amount;
        getService().getData().put(profile, String.valueOf(newAmount));
        getService().updateData(profile);
    }

    private double getAmount(final Profile profile) throws QuestException {
        final String data = getService().getData().get(profile);
        if (data == null) {
            return 0;
        }
        return NumberParser.DEFAULT.apply(data).doubleValue();
    }
}
