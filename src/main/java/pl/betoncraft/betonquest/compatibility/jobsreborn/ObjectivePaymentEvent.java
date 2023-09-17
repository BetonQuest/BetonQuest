package pl.betoncraft.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.api.JobsPaymentEvent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

@SuppressWarnings("PMD.CommentRequired")
public class ObjectivePaymentEvent extends Objective implements Listener {
    private final double nAmount;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public ObjectivePaymentEvent(final Instruction instructions) throws InstructionParseException {
        super(instructions);
        template = PaymentData.class;
        if (instructions.size() < 2) {
            throw new InstructionParseException("Not enough arguments");
        }
        try {
            nAmount = Double.parseDouble(instructions.getPart(1));
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse amount", e);
        }
        if (nAmount <= 0) {
            throw new InstructionParseException("Amount needs to be one or more");
        }
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @EventHandler(ignoreCancelled = true)
    public void onJobsPaymentEvent(final JobsPaymentEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer().getPlayer().getPlayer());
        if (containsPlayer(playerID) && checkConditions(playerID)) {
            final PaymentData playerData = (PaymentData) dataMap.get(playerID);
            Bukkit.getServer().broadcastMessage("Amount: " + playerData.getAmount());
            playerData.subtract(event.getAmount());

            if (playerData.isZero()) {
                completeObjective(playerID);
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
    public String getProperty(final String name, final String playerID) {
        if ("left".equalsIgnoreCase(name)) {
            return Double.toString(nAmount - ((PaymentData) dataMap.get(playerID)).getAmount());
        } else if ("amount".equalsIgnoreCase(name)) {
            return Double.toString(((PaymentData) dataMap.get(playerID)).getAmount());
        }
        return "";
    }

    public static class PaymentData extends ObjectiveData {

        private Double amount;

        public PaymentData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Double.parseDouble(instruction);
        }

        private Double getAmount() {
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
