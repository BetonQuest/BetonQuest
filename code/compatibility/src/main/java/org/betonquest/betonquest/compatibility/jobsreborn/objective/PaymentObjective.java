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
import org.betonquest.betonquest.api.quest.objective.ObjectiveData;
import org.betonquest.betonquest.api.quest.objective.ObjectiveDataFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;

import java.util.Locale;
import java.util.Objects;

/**
 * Objective that tracks the payment received by a player.
 */
public class PaymentObjective extends DefaultObjective {

    /**
     * The Factory for the Payment Data.
     */
    private static final ObjectiveDataFactory PAYMENT_FACTORY = PaymentData::new;

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
    public PaymentObjective(final ObjectiveFactoryService service, final Argument<Number> targetAmount, final IngameNotificationSender paymentSender) throws QuestException {
        super(service, PAYMENT_FACTORY);
        this.targetAmount = targetAmount;
        this.paymentSender = paymentSender;
    }

    /**
     * Check if the player has received a payment.
     *
     * @param event   the event that triggered the payment
     * @param profile the profile of the player that received the payment
     */
    public void onJobsPaymentEvent(final JobsPaymentEvent event, final Profile profile) {
        if (containsPlayer(profile) && checkConditions(profile)) {
            final PaymentData playerData = getPaymentData(profile);
            final double previousAmount = playerData.amount;
            playerData.add(event.get(CurrencyType.MONEY));

            if (playerData.isCompleted()) {
                completeObjective(profile);
            } else {
                final int interval = getNotifyInterval(profile);
                if (interval > 0 && ((int) playerData.amount) / interval != ((int) previousAmount) / interval && profile.getOnlineProfile().isPresent()) {
                    paymentSender.sendNotification(profile,
                            new VariableReplacement("amount", Component.text(playerData.targetAmount - playerData.amount)));
                }
            }
        }
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) throws QuestException {
        return String.valueOf(targetAmount.getValue(profile).doubleValue());
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "amount" -> Double.toString(getPaymentData(profile).amount);
            case "left" -> {
                final PaymentData data = getPaymentData(profile);
                yield Double.toString(data.targetAmount - data.amount);
            }
            case "total" -> Double.toString(getPaymentData(profile).targetAmount);
            default -> "";
        };
    }

    /**
     * Get the {@link PaymentData} for the given {@link Profile}.
     *
     * @throws NullPointerException when {@link #containsPlayer(Profile)} is false
     */
    private PaymentData getPaymentData(final Profile profile) {
        return Objects.requireNonNull((PaymentData) dataMap.get(profile));
    }

    /**
     * Data class for the Payment objective.
     */
    public static class PaymentData extends ObjectiveData {

        /**
         * The amount of money the player has to earn to complete the objective.
         */
        private final double targetAmount;

        /**
         * The amount of money the player has earned. This is used to check if the objective is completed.
         */
        private double amount;

        /**
         * Constructor for the PaymentData class.
         *
         * @param instruction the instruction of the data object; parse it to get all required information
         * @param profile     the {@link Profile} to load the data for
         * @param objID       ID of the objective, used by BetonQuest to store this ObjectiveData in the database
         * @throws QuestException when the instruction is invalid
         */
        public PaymentData(final String instruction, final Profile profile, final ObjectiveID objID) throws QuestException {
            super(instruction, profile, objID);
            targetAmount = NumberParser.DEFAULT.apply(instruction).doubleValue();
        }

        private void add(final Double amount) {
            this.amount += amount;
            update();
        }

        private boolean isCompleted() {
            return amount >= targetAmount;
        }

        @Override
        public String toString() {
            return amount + "/" + targetAmount;
        }
    }
}
