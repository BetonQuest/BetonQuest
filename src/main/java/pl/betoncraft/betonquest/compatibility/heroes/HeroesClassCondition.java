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
package pl.betoncraft.betonquest.compatibility.heroes;

import java.util.ArrayList;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks the class of the player and the level.
 * 
 * @author Jakub Sapalski
 */
public class HeroesClassCondition extends Condition {

	private HeroClass heroClass;
	private boolean any;
	private boolean primary;
	private boolean mastered;
	private VariableNumber level = new VariableNumber(-1);

	public HeroesClassCondition(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		String[] parts = instructions.split(" ");
		if (parts.length < 3) {
			throw new InstructionParseException("Not enough arguments");
		}
		primary = parts[1].equalsIgnoreCase("primary");
		mastered = parts[1].equals("mastered");
		if (parts[2].equalsIgnoreCase("any")) {
			any = true;
		} else {
			heroClass = Heroes.getInstance().getClassManager().getClass(parts[2]);
			if (heroClass == null) {
				throw new InstructionParseException("Class '" + parts[2] + "' does not exist");
			}
		}
		for (String part : parts) {
			if (part.startsWith("level:")) {
				try {
					level = new VariableNumber(packName, part.substring(6));
				} catch (NumberFormatException e) {
					throw new InstructionParseException("Could not parse level");
				}
			}
		}
	}

	@Override
	public boolean check(String playerID) throws QuestRuntimeException {
		Hero hero = Heroes.getInstance().getCharacterManager().getHero(PlayerConverter.getPlayer(playerID));
		if (hero == null)
			return false;
		ArrayList<HeroClass> heroClasses = new ArrayList<>();
		if (mastered) {
			for (String heroClass : hero.getMasteredClasses()) {
				heroClasses.add(Heroes.getInstance().getClassManager().getClass(heroClass));
			}
		} else if (primary) {
			heroClasses.add(hero.getHeroClass());
		} else {
			heroClasses.add(hero.getSecondClass());
		}
		if (heroClasses.isEmpty())
			return false;
		boolean matchingClass = true, matchingLevel = true;
		int l = level.getInt(playerID);
		if (!any) {
			matchingClass = heroClasses.contains(heroClass);
			if (l > 0) {
				matchingLevel = hero.getLevel(heroClass) >= l;
			}
		} else {
			matchingLevel = hero.getLevel() >= l;
		}
		return matchingClass && matchingLevel;
	}

}
