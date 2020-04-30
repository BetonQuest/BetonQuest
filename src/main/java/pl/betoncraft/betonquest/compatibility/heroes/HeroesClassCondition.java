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
package pl.betoncraft.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;

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
    private VariableNumber level;

    public HeroesClassCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        String string = instruction.next();
        primary = string.equalsIgnoreCase("primary");
        mastered = string.equals("mastered");
        string = instruction.next();
        if (string.equalsIgnoreCase("any")) {
            any = true;
        } else {
            heroClass = Heroes.getInstance().getClassManager().getClass(string);
            if (heroClass == null) {
                throw new InstructionParseException("Class '" + string + "' does not exist");
            }
        }
        level = instruction.getVarNum(instruction.getOptional("level"));
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
        int l = (level != null) ? level.getInt(playerID) : -1;
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
