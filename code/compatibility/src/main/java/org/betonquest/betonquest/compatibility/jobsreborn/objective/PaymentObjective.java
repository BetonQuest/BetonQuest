package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.api.JobsPaymentEvent;
import com.gamingmesh.jobs.container.CurrencyType;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.parser.NumberParser;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.ObjectiveData;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
    public PaymentObjective(final ObjectiveFactoryService service, final Argument<Number> targetAmount, final IngameNotificationSender paymentSender) throws QuestException {
        super(service);
        this.targetAmount = targetAmount;
        this.paymentSender = paymentSender;
        service.setDefaultData(this::getDefaultDataInstruction);
        service.getProperties().setProperty("amount", profile -> Optional.ofNullable(getPaymentData(profile))
                .map(data -> data.amount).map(Object::toString).orElse(""));
        service.getProperties().setProperty("left", profile -> Optional.ofNullable(getPaymentData(profile))
                .map(data -> data.targetAmount - data.amount).map(Object::toString).orElse(""));
        service.getProperties().setProperty("total", profile -> Optional.ofNullable(getPaymentData(profile))
                .map(data -> data.targetAmount).map(Object::toString).orElse(""));
    }

    /**
     * Check if the player has received a payment.
     *
     * @param event   the event that triggered the payment
     * @param profile the profile of the player that received the payment
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onJobsPaymentEvent(final JobsPaymentEvent event, final Profile profile) throws QuestException {
        final PaymentData playerData = getPaymentData(profile);
        if (playerData == null) {
            getLogger().warn("Could not access PaymentData for profile '" + profile + "'.");
            return;
        }
        final double previousAmount = playerData.amount;
        playerData.add(event.get(CurrencyType.MONEY));

        if (playerData.isCompleted()) {
            getService().complete(profile);
        } else {
            final int interval = getService().getServiceDataProvider().getNotificationInterval(profile);
            if (interval > 0 && ((int) playerData.amount) / interval != ((int) previousAmount) / interval && profile.getOnlineProfile().isPresent()) {
                paymentSender.sendNotification(profile,
                        new VariableReplacement("amount", Component.text(playerData.targetAmount - playerData.amount)));
            }
        }
    }

    private String getDefaultDataInstruction(final Profile profile) throws QuestException {
        return String.valueOf(targetAmount.getValue(profile).doubleValue());
    }

    /**
     * Get the {@link PaymentData} for the given {@link Profile}.
     */
    @Nullable
    private PaymentData getPaymentData(final Profile profile) {
        final String data = getService().getData().getOrDefault(profile, "");
        try {
            return new PaymentData(data, profile, getObjectiveID());
        } catch (final QuestException e) {
            getLogger().error("Could not access PaymentData for profile '" + profile + "': " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Data class for the Payment objective.
     *
     * @deprecated do not use this class. it's scheduled for removal in future versions
     */
    @Deprecated
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
            final ObjectiveFactoryService service;
            try {
                service = BetonQuest.getInstance().getQuestTypeApi().getObjective(objID).getService();
            } catch (final QuestException e) {
                throw new IllegalStateException("Could not get objective service for objective '" + objID + "'", e);
            }
            this.amount += amount;
            update(service);
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
