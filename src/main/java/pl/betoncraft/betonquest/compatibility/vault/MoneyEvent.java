/**
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
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import java.text.DecimalFormat;

/**
 * Modifies player's balance
 * 
 * @author Jakub Sapalski
 */
public class MoneyEvent extends QuestEvent {

	private final VariableNumber amount;
	private boolean multi;
	private final boolean notify;

	public MoneyEvent(Instruction instruction) throws InstructionParseException {
		super(instruction);
		String string = instruction.next();
		if (string.startsWith("*")) {
			multi = true;
			string = string.replace("*", "");
		}
		try {
			amount = new VariableNumber(instruction.getPackage().getName(), string);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse money amount");
		}
		notify = instruction.hasArgument("notify");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run(String playerID) throws QuestRuntimeException {
		Player player = PlayerConverter.getPlayer(playerID);
		// get the difference between target money and current money
		double current = VaultIntegrator.getEconomy().getBalance(player);
		double target;
		if (multi)
			target = current * amount.getDouble(playerID);
		else
			target = current + amount.getDouble(playerID);

		double difference = target - current;
		DecimalFormat df = new DecimalFormat("#.00");
		String currencyName = VaultIntegrator.getEconomy().currencyNamePlural();

		if (difference > 0) {
		    VaultIntegrator.getEconomy().depositPlayer(player.getName(), difference);
			if (notify) {
				Config.sendMessage(playerID, "money_given",
						new String[] {df.format(difference), currencyName});
			}
		} else if (difference < 0) {
		    VaultIntegrator.getEconomy().withdrawPlayer(player.getName(), -difference);
			if (notify) {
				Config.sendMessage(playerID, "money_taken",
						new String[] {df.format(difference), currencyName});
			}
		}
	}
}
