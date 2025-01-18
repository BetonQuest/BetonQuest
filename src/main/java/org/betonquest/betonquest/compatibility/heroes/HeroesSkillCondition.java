package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Checks if the player has access to specified Heroes skill.
 */
@SuppressWarnings("PMD.CommentRequired")
public class HeroesSkillCondition extends Condition {

    private final String skillName;

    public HeroesSkillCondition(final Instruction instruction) throws QuestException {
        super(instruction, true);
        skillName = instruction.next();
    }

    @Override
    protected Boolean execute(final Profile profile) {
        final Hero hero = Heroes.getInstance().getCharacterManager().getHero(profile.getOnlineProfile().get().getPlayer());
        return hero != null && hero.canUseSkill(skillName);
    }
}
