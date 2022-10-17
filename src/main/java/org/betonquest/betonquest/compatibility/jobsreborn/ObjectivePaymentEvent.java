package org.betonquest.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.api.JobsPaymentEvent;
import com.gamingmesh.jobs.container.CurrencyType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Locale;

@SuppressWarnings("PMD.CommentRequired")
public class ObjectivePaymentEvent extends Objective implements Listener {
    private final double nAmount;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public ObjectivePaymentEvent(final Instruction instructions) throws InstructionParseException {
        super(instructions);
        template = ObjectiveData.class;
        if (instructions.size() < 2) {
            throw new InstructionParseException("Not enough arguments");
        }
        try {
            nAmount = Double.parseDouble(instructions.getPart(1));
        } catch (final NumberFormatException e) {
            throw new InstructionParseException("Could not parse amount", e);
        }
        if (nAmount <= 0) {
            throw new InstructionParseException("Amount needs to be one or more");
        }
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @EventHandler(ignoreCancelled = true)
    public void onJobsPaymentEvent(final JobsPaymentEvent event) {
        final Profile profile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(profile) && checkConditions(profile)) {
            final PaymentData playerData = (PaymentData) dataMap.get(profile);
            final double previousAmount = playerData.getAmount();
            playerData.subtract(event.get(CurrencyType.MONEY));

            if (playerData.isZero()) {
                completeObjective(profile);
            } else if (notify && ((int) playerData.getAmount()) / notifyInterval != ((int) previousAmount) / notifyInterval) {
                sendNotify(profile.getOnlineProfile(), "payment_to_receive", playerData.getAmount());
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
    public String getDefaultDataInstruction() {
        return Double.toString(nAmount);
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        switch (name.toLowerCase(Locale.ROOT)) {
            case "amount":
                return Double.toString(nAmount - ((PaymentData) dataMap.get(profile)).getAmount());
            case "left":
                return Double.toString(((PaymentData) dataMap.get(profile)).getAmount());
            case "total":
                return Double.toString(nAmount);
            default:
                return "";
        }
    }

    public static class PaymentData extends ObjectiveData {

        private double amount;

        public PaymentData(final String instruction, final Profile profile, final String objID) {
            super(instruction, profile, objID);
            amount = Double.parseDouble(instruction);
        }

        private double getAmount() {
            return amount;
        }

        private void subtract(final Double amount) {
            this.amount -= amount;
            update();
        }

        private boolean isZero() {
            return amount <= 0;
        }

        @Override
        public String toString() {
            return Double.toString(amount);
        }

    }
}
