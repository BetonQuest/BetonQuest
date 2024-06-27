package org.betonquest.betonquest.compatibility.vault;

import net.milkbowl.vault.economy.Economy;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
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
    private final BetonQuestLogger log;

    private final VariableNumber amount;

    private final boolean notify;

    private final boolean multi;

    public MoneyEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
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

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        // get the difference between target money and current money
        final Economy economy = VaultIntegrator.getInstance().getEconomy();
        if (economy == null) {
            throw new QuestRuntimeException("Can't execute the event because the Vault instance is null!");
        }
        final OfflinePlayer player = profile.getPlayer();
        final double current = economy.getBalance(player);
        final double target;
        if (multi) {
            target = current * amount.getDouble(profile);
        } else {
            target = current + amount.getDouble(profile);
        }

        final double difference = target - current;
        final DecimalFormat decimalFormat = new DecimalFormat("#.00");
        final String currencyName = economy.currencyNamePlural();

        if (difference > 0) {
            economy.depositPlayer(player, difference);
            if (notify && profile.getOnlineProfile().isPresent()) {
                try {
                    Config.sendNotify(instruction.getPackage(), profile.getOnlineProfile().get(), "money_given",
                            new String[]{decimalFormat.format(difference), currencyName}, "money_given,info");
                } catch (final QuestRuntimeException e) {
                    log.warn(instruction.getPackage(), "The notify system was unable to play a sound for the 'money_given' category in '" + getFullId() + "'. Error was: '" + e.getMessage() + "'", e);
                }
            }
        } else if (difference < 0) {
            economy.withdrawPlayer(player, -difference);
            if (notify && profile.getOnlineProfile().isPresent()) {
                try {
                    Config.sendNotify(instruction.getPackage(), profile.getOnlineProfile().get(), "money_taken",
                            new String[]{decimalFormat.format(difference), currencyName}, "money_taken,info");
                } catch (final QuestRuntimeException e) {
                    log.warn(instruction.getPackage(), "The notify system was unable to play a sound for the 'money_taken' category in '" + getFullId() + "'. Error was: '" + e.getMessage() + "'", e);
                }
            }
        }
        return null;
    }
}
