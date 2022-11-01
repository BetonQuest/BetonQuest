package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.util.ArrayList;

/**
 * Checks the class of the player and the level.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.CyclomaticComplexity"})
public class HeroesClassCondition extends Condition {

    private final boolean primary;
    private final boolean mastered;
    private final VariableNumber level;
    private final HeroClass heroClass;
    private final boolean any;

    public HeroesClassCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        String string = instruction.next();
        primary = "primary".equalsIgnoreCase(string);
        mastered = "mastered".equals(string);
        string = instruction.next();
        if ("any".equalsIgnoreCase(string)) {
            any = true;
            heroClass = null;
        } else {
            any = false;
            heroClass = Heroes.getInstance().getClassManager().getClass(string);
            if (heroClass == null) {
                throw new InstructionParseException("Class '" + string + "' does not exist");
            }
        }
        level = instruction.getVarNum(instruction.getOptional("level"));
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final Hero hero = Heroes.getInstance().getCharacterManager().getHero(profile.getOnlineProfile().getOnlinePlayer());
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
            heroClasses.add(hero.getSecondaryClass());
        }
        if (heroClasses.isEmpty()) {
            return false;
        }
        boolean matchingClass = true;
        boolean matchingLevel = true;
        final int playerLevel = level == null ? -1 : level.getInt(profile);
        if (any) {
            matchingLevel = hero.getHeroLevel() >= playerLevel;
        } else {
            matchingClass = heroClasses.contains(heroClass);
            if (playerLevel > 0) {
                matchingLevel = hero.getHeroLevel(heroClass) >= playerLevel;
            }
        }
        return matchingClass && matchingLevel;
    }

}
