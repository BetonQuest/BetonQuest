package pl.betoncraft.betonquest.compatibility.vault;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.text.DecimalFormat;
import java.util.logging.Level;

/**
 * Modifies player's balance
 */
@SuppressWarnings("PMD.CommentRequired")
public class MoneyEvent extends QuestEvent {

    private final VariableNumber amount;
    private final boolean notify;
    private boolean multi;

    public MoneyEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        String string = instruction.next();
        if (!string.isEmpty() && string.charAt(0) == '*') {
            multi = true;
            string = string.replace("*", "");
        }
        try {
            amount = new VariableNumber(instruction.getPackage().getName(), string);
        } catch (InstructionParseException e) {
            throw new InstructionParseException("Could not parse money amount", e);
        }
        notify = instruction.hasArgument("notify");
    }

    @SuppressWarnings({"PMD.PreserveStackTrace", "PMD.CyclomaticComplexity"})
    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        // get the difference between target money and current money
        final double current = VaultIntegrator.getEconomy().getBalance(player);
        final double target;
        if (multi) {
            target = current * amount.getDouble(playerID);
        } else {
            target = current + amount.getDouble(playerID);
        }

        final double difference = target - current;
        final DecimalFormat decimalFormat = new DecimalFormat("#.00");
        final String currencyName = VaultIntegrator.getEconomy().currencyNamePlural();

        if (difference > 0) {
            VaultIntegrator.getEconomy().depositPlayer(player, difference);
            if (notify) {
                try {
                    Config.sendNotify(instruction.getPackage().getName(), playerID, "money_given",
                            new String[]{decimalFormat.format(difference), currencyName}, "money_given,info");
                } catch (final QuestRuntimeException exception) {
                    LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'money_given' category in '" + getFullId() + "'. Error was: '" + exception.getMessage() + "'");
                    LogUtils.logThrowable(exception);
                }
            }
        } else if (difference < 0) {
            VaultIntegrator.getEconomy().withdrawPlayer(player, -difference);
            if (notify) {
                try {
                    Config.sendNotify(instruction.getPackage().getName(), playerID, "money_taken",
                            new String[]{decimalFormat.format(difference), currencyName}, "money_taken,info");
                } catch (final QuestRuntimeException exception) {
                    LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'money_taken' category in '" + getFullId() + "'. Error was: '" + exception.getMessage() + "'");
                    LogUtils.logThrowable(exception);
                }
            }
        }
        return null;
    }
}
