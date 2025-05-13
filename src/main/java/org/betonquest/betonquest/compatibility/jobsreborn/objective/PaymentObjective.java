package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import com.gamingmesh.jobs.api.JobsPaymentEvent;
import com.gamingmesh.jobs.container.CurrencyType;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Locale;
import java.util.Objects;

/**
 * Objective that tracks the payment received by a player.
 */
public class PaymentObjective extends Objective implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The target amount of money to be received.
     */
    private final Variable<Number> targetAmount;

    /**
     * The {@link IngameNotificationSender} to send notifications.
     */
    private final IngameNotificationSender paymentSender;

    /**
     * Constructor for the PaymentObjective.
     *
     * @param instruction   the instruction of the objective
     * @param log           the logger for this objective
     * @param targetAmount  the target amount of money to be received
     * @param paymentSender the {@link IngameNotificationSender} to send notifications
     * @throws QuestException if the instruction is invalid
     */
    public PaymentObjective(final Instruction instruction, final BetonQuestLogger log, final Variable<Number> targetAmount, final IngameNotificationSender paymentSender) throws QuestException {
        super(instruction, PaymentData.class);
        this.log = log;
        this.targetAmount = targetAmount;
        this.paymentSender = paymentSender;
    }

    /**
     * Check if the player has received a payment.
     *
     * @param event the event that triggered the payment
     */
    @EventHandler(ignoreCancelled = true)
    public void onJobsPaymentEvent(final JobsPaymentEvent event) {
        final Profile profile = profileProvider.getProfile(event.getPlayer());
        if (containsPlayer(profile) && checkConditions(profile)) {
            final PaymentData playerData = getPaymentData(profile);
            final double previousAmount = playerData.amount;
            playerData.add(event.get(CurrencyType.MONEY));

            if (playerData.isCompleted()) {
                completeObjective(profile);
            } else if (notify && ((int) playerData.amount) / notifyInterval != ((int) previousAmount) / notifyInterval && profile.getOnlineProfile().isPresent()) {
                paymentSender.sendNotification(profile,
                        new VariableReplacement("amount", Component.text(playerData.targetAmount - playerData.amount)));
            }
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public final String getDefaultDataInstruction() {
        return targetAmount.toString();
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        try {
            return String.valueOf(targetAmount.getValue(profile).doubleValue());
        } catch (final QuestException e) {
            log.warn(instruction.getPackage(), "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage(), e);
            return "1";
        }
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
         */
        public PaymentData(final String instruction, final Profile profile, final String objID) {
            super(instruction, profile, objID);
            targetAmount = Double.parseDouble(instruction);
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
