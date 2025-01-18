package org.betonquest.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.api.JobsPaymentEvent;
import com.gamingmesh.jobs.container.CurrencyType;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Locale;
import java.util.Objects;

@SuppressWarnings("PMD.CommentRequired")
public class ObjectivePaymentEvent extends Objective implements Listener {
    private final VariableNumber targetAmount;

    public ObjectivePaymentEvent(final Instruction instructions) throws QuestException {
        super(instructions);
        template = PaymentData.class;
        targetAmount = instructions.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
    }

    @EventHandler(ignoreCancelled = true)
    public void onJobsPaymentEvent(final JobsPaymentEvent event) {
        final Profile profile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(profile) && checkConditions(profile)) {
            final PaymentData playerData = getPaymentData(profile);
            final double previousAmount = playerData.amount;
            playerData.add(event.get(CurrencyType.MONEY));

            if (playerData.isCompleted()) {
                completeObjective(profile);
            } else if (notify && ((int) playerData.amount) / notifyInterval != ((int) previousAmount) / notifyInterval && profile.getOnlineProfile().isPresent()) {
                sendNotify(profile.getOnlineProfile().get(), "payment_to_receive", playerData.amount);
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
        final double value = targetAmount.getDouble(profile);
        return value > 0 ? String.valueOf(value) : "1";
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
     * @throws NullPointerException when {@link #containsPlayer(Profile)} is false
     */
    private PaymentData getPaymentData(final Profile profile) {
        return Objects.requireNonNull((PaymentData) dataMap.get(profile));
    }

    public static class PaymentData extends ObjectiveData {
        private final double targetAmount;

        private double amount;

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
