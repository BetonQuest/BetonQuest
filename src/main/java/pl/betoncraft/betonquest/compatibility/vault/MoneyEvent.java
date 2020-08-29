/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.compatibility.vault;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.text.DecimalFormat;

/**
 * Modifies player's balance
 *
 * @author Jakub Sapalski
 */
public class MoneyEvent extends QuestEvent {

    private final VariableNumber amount;
    private final boolean notify;
    private boolean multi;

    public MoneyEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        String string = instruction.next();
        if (string.startsWith("*")) {
            multi = true;
            string = string.replace("*", "");
        }
        try {
            amount = new VariableNumber(instruction.getPackage().getName(), string);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse money amount", e);
        }
        notify = instruction.hasArgument("notify");
    }

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
                Config.sendNotify(playerID, "money_given",
                        new String[]{decimalFormat.format(difference), currencyName}, "money_given,info");
            }
        } else if (difference < 0) {
            VaultIntegrator.getEconomy().withdrawPlayer(player, -difference);
            if (notify) {
                Config.sendNotify(playerID, "money_taken",
                        new String[]{decimalFormat.format(difference), currencyName}, "money_taken,info");
            }
        }
        return null;
    }
}
