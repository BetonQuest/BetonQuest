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
package pl.betoncraft.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;


/**
 * Adds the experience the a class.
 *
 * @author Jakub Sapalski
 */
public class HeroesExperienceEvent extends QuestEvent {

	private final boolean			primary;
	private final VariableNumber	amount;

	public HeroesExperienceEvent(final Instruction instruction) throws InstructionParseException {
		super(instruction, true);
		primary = instruction.next().equalsIgnoreCase("primary");
		amount = instruction.getVarNum();
	}

	@Override
	protected Void execute(final String playerID) throws QuestRuntimeException {
		final Hero hero = Heroes.getInstance().getCharacterManager().getHero(PlayerConverter.getPlayer(playerID));
		if(hero == null)
			return null;
		if(primary) {
			if(hero.getHeroClass() == null)
				return null;
			hero.addExp(amount.getInt(playerID), hero.getHeroClass(), hero.getPlayer().getLocation());
		} else {
			if(hero.getSecondClass() == null)
				return null;
			hero.addExp(amount.getInt(playerID), hero.getSecondClass(), hero.getPlayer().getLocation());
		}
		return null;
	}

}
