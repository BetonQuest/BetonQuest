package org.betonquest.betonquest.compatibility.vault;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;

/**
 * Modifies player's balance
 */
@SuppressWarnings("PMD.CommentRequired")
public class MoneyEvent extends QuestEvent {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(MoneyEvent.class);

    private final VariableNumber amount;
    private final boolean notify;
    private final boolean multi;

    public MoneyEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        String string = instruction.next();
        if (!string.isEmpty() && string.charAt(0) == '*') {
            multi = true;
            string = string.replace("*", "");
        } else {
            multi = false;
        }
        try {
            amount = new VariableNumber(instruction.getPackage(), string);
        } catch (final InstructionParseException e) {
            throw new InstructionParseException("Could not parse money amount", e);
        }
        notify = instruction.hasArgument("notify");
    }

    @SuppressWarnings({"PMD.PreserveStackTrace", "PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final OfflinePlayer player = profile.getPlayer();
        // get the difference between target money and current money
        final double current = VaultIntegrator.getEconomy().getBalance(player);
        final double target;
        if (multi) {
            target = current * amount.getDouble(profile);
        } else {
            target = current + amount.getDouble(profile);
        }

        final double difference = target - current;
        final DecimalFormat decimalFormat = new DecimalFormat("#.00");
        final String currencyName = VaultIntegrator.getEconomy().currencyNamePlural();

        if (difference > 0) {
            VaultIntegrator.getEconomy().depositPlayer(player, difference);
            if (notify && profile.getOnlineProfile().isPresent()) {
                try {
                    Config.sendNotify(instruction.getPackage().getQuestPath(), profile.getOnlineProfile().get(), "money_given",
                            new String[]{decimalFormat.format(difference), currencyName}, "money_given,info");
                } catch (final QuestRuntimeException e) {
                    LOG.warn(instruction.getPackage(), "The notify system was unable to play a sound for the 'money_given' category in '" + getFullId() + "'. Error was: '" + e.getMessage() + "'", e);
                }
            }
        } else if (difference < 0) {
            VaultIntegrator.getEconomy().withdrawPlayer(player, -difference);
            if (notify && profile.getOnlineProfile().isPresent()) {
                try {
                    Config.sendNotify(instruction.getPackage().getQuestPath(), profile.getOnlineProfile().get(), "money_taken",
                            new String[]{decimalFormat.format(difference), currencyName}, "money_taken,info");
                } catch (final QuestRuntimeException e) {
                    LOG.warn(instruction.getPackage(), "The notify system was unable to play a sound for the 'money_taken' category in '" + getFullId() + "'. Error was: '" + e.getMessage() + "'", e);
                }
            }
        }
        return null;
    }
}
