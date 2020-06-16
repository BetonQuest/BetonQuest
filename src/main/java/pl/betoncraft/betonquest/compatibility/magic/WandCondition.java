/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016 Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.compatibility.magic;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.api.wand.LostWand;
import com.elmakers.mine.bukkit.api.wand.Wand;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;


/**
 * Checks if the player is holding a wand.
 *
 * @author Jakub Sapalski
 */
public class WandCondition extends Condition {

	private final MagicAPI							api;
	private CheckType								type;
	private final HashMap<String, VariableNumber>	spells	= new HashMap<>();
	private final String							name;
	private final VariableNumber					amount;

	public WandCondition(final Instruction instruction) throws InstructionParseException {
		super(instruction, true);
		final String string = instruction.next();
		switch(string) {
			case "hand":
				type = CheckType.IN_HAND;
				break;
			case "inventory":
				type = CheckType.IN_INVENTORY;
				break;
			case "lost":
				type = CheckType.IS_LOST;
				break;
			default:
				throw new InstructionParseException("Unknown check type '" + string + "'");
		}
		final String[] array = instruction.getArray(instruction.getOptional("spells"));
		if(array != null) {
			for(String spell : array) {
				VariableNumber level = new VariableNumber(1);
				if(spell.contains(":")) {
					final String[] spellParts = spell.split(":");
					spell = spellParts[0];
					try {
						level = new VariableNumber(instruction.getPackage().getName(), spellParts[1]);
					} catch(final NumberFormatException e) {
						throw new InstructionParseException("Could not parse spell level", e);
					}
					this.spells.put(spell, level);
				} else {
					throw new InstructionParseException("Incorrect spell format");
				}
			}
		}
		name = instruction.getOptional("name");
		api = (MagicAPI) Bukkit.getPluginManager().getPlugin("Magic");
		amount = instruction.getVarNum(instruction.getOptional("amount"));
	}

	@Override
	protected Boolean execute(final String playerID) throws QuestRuntimeException {
		final Player player = PlayerConverter.getPlayer(playerID);
		int heldAmount;

		switch(type) {
			case IS_LOST:
				for(final LostWand lost : api.getLostWands()) {
					final Player owner = Bukkit.getPlayer(UUID.fromString(lost.getOwnerId()));
					if(owner == null)
						continue;
					if(owner.equals(player)) {
						return true;
					}
				}
				return false;
			case IN_HAND:
				ItemStack wandItem = null;
				wandItem = player.getInventory().getItemInMainHand();
				if(!api.isWand(wandItem)) {
					return false;
				}
				final Wand wand1 = api.getWand(wandItem);
				return checkWand(wand1, playerID);
			case IN_INVENTORY:
				heldAmount = 0;
				for(final ItemStack item : player.getInventory().getContents()) {
					if(item == null)
						continue;
					if(api.isWand(item)) {
						final Wand wand2 = api.getWand(item);
						if(checkWand(wand2, playerID)) {
							heldAmount += item.getAmount();
							if(amount == null || heldAmount >= amount.getInt(playerID)) {
								return true;
							}
						}
					}
				}
				return false;
			default:
				return false;
		}
	}

	/**
	 * Checks if the given wand meets specified name and spells conditions.
	 *
	 * @param wand
	 *            wand to check
	 * @return true if the wand meets the conditions, false otherwise
	 * @throws QuestRuntimeException
	 */
	private boolean checkWand(final Wand wand, final String playerID) throws QuestRuntimeException {
		if(name != null && !wand.getTemplateKey().equalsIgnoreCase(name)) {
			return false;
		}
		if(!spells.isEmpty()) {
			spell : for(final String spell : spells.keySet()) {
				final int level = spells.get(spell).getInt(playerID);
				for(final String wandSpell : wand.getSpells()) {
					if(wandSpell.toLowerCase().startsWith(spell.toLowerCase()) && wand.getSpellLevel(spell) >= level) {
						continue spell;
					}
				}
				return false;
			}
		}
		return true;
	}

	private enum CheckType {
		IS_LOST,
		IN_HAND,
		IN_INVENTORY
	}

}
