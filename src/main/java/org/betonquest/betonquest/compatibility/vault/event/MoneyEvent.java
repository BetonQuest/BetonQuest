package org.betonquest.betonquest.compatibility.vault.event;

import net.milkbowl.vault.economy.Economy;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

/**
 * Modifies player's balance.
 */
public class MoneyEvent implements Event {
    /**
     * Economy where the balance will be modified.
     */
    private final Economy economy;

    /**
     * Amount to modify the balance.
     */
    private final VariableNumber amount;

    /**
     * If the current balance should be multiplied with the amount.
     */
    private final boolean multi;

    /**
     * Notification wrapper if the player should get a message when getting money.
     */
    @Nullable
    private final IngameNotificationSender givenSender;

    /**
     * Notification wrapper if the player should get a message when loosing money.
     */
    @Nullable
    private final IngameNotificationSender takenSender;

    /**
     * Create a new vault money event.
     *
     * @param economy     the economy where the balance will be modified
     * @param amount      the amount to modify the balance
     * @param multi       if the current balance should be multiplied with the amount
     * @param givenSender the notification wrapper if the player should get a message when getting money
     * @param takenSender the notification wrapper if the player should get a message when loosing money
     */
    public MoneyEvent(final Economy economy, final VariableNumber amount, final boolean multi,
                      @Nullable final IngameNotificationSender givenSender, @Nullable final IngameNotificationSender takenSender) {
        this.economy = economy;
        this.amount = amount;
        this.multi = multi;
        this.givenSender = givenSender;
        this.takenSender = takenSender;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final OfflinePlayer player = profile.getPlayer();
        final double current = economy.getBalance(player);
        final double target;
        if (multi) {
            target = current * amount.getValue(profile).doubleValue();
        } else {
            target = current + amount.getValue(profile).doubleValue();
        }

        final double difference = target - current;
        final DecimalFormat decimalFormat = new DecimalFormat("#.00");
        final String currencyName = economy.currencyNamePlural();

        if (difference > 0) {
            economy.depositPlayer(player, difference);
            if (givenSender != null) {
                givenSender.sendNotification(profile, decimalFormat.format(difference), currencyName);
            }
        } else if (difference < 0) {
            economy.withdrawPlayer(player, -difference);
            if (takenSender != null) {
                takenSender.sendNotification(profile, decimalFormat.format(difference), currencyName);
            }
        }
    }
}
