package org.betonquest.betonquest.compatibility.vault;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.LogUtils;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

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
