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
 */
public class HeroesClassCondition extends Condition {

    private HeroClass heroClass;
    private boolean any;
    private boolean primary;
    private boolean mastered;
    private VariableNumber level;

    public HeroesClassCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
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
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Hero hero = Heroes.getInstance().getCharacterManager().getHero(PlayerConverter.getPlayer(playerID));
        if (hero == null) {
            return false;
        }
        final ArrayList<HeroClass> heroClasses = new ArrayList<>();
        if (mastered) {
            for (final String heroClass : hero.getMasteredClasses()) {
                heroClasses.add(Heroes.getInstance().getClassManager().getClass(heroClass));
            }
        } else if (primary) {
            heroClasses.add(hero.getHeroClass());
        } else {
            heroClasses.add(hero.getSecondClass());
        }
        if (heroClasses.isEmpty()) {
            return false;
        }
        boolean matchingClass = true, matchingLevel = true;
        final int level = (this.level == null) ? -1 : this.level.getInt(playerID);
        if (any) {
            matchingLevel = hero.getLevel() >= level;
        } else {
            matchingClass = heroClasses.contains(heroClass);
            if (level > 0) {
                matchingLevel = hero.getLevel(heroClass) >= level;
            }
        }
        return matchingClass && matchingLevel;
    }

}
