package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks the class of the player and the level.
 */
@SuppressWarnings("PMD.CommentRequired")
public class HeroesClassCondition extends Condition {
    /**
     * The string to match any class.
     */
    private static final String ANY_CLASS = "any";

    private final boolean primary;

    private final boolean mastered;

    @Nullable
    private final VariableNumber level;

    @Nullable
    private final HeroClass heroClass;

    private final boolean any;

    public HeroesClassCondition(final Instruction instruction) throws QuestException {
        super(instruction, true);
        String string = instruction.next();
        primary = "primary".equalsIgnoreCase(string);
        mastered = "mastered".equals(string);
        string = instruction.next();
        if (ANY_CLASS.equalsIgnoreCase(string)) {
            any = true;
            heroClass = null;
        } else {
            any = false;
            heroClass = Heroes.getInstance().getClassManager().getClass(string);
            if (heroClass == null) {
                throw new QuestException("Class '" + string + "' does not exist");
            }
        }
        level = instruction.get(instruction.getOptional("level"), VariableNumber::new);
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestException {
        final Hero hero = Heroes.getInstance().getCharacterManager().getHero(profile.getOnlineProfile().get().getPlayer());
        if (hero == null) {
            return false;
        }
        final List<HeroClass> heroClasses = new ArrayList<>();
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
