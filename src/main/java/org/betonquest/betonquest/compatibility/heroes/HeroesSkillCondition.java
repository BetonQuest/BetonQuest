package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Checks if the player has access to specified Heroes skill.
 */
@SuppressWarnings("PMD.CommentRequired")
public class HeroesSkillCondition extends Condition {

    private final String skillName;

    public HeroesSkillCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        skillName = instruction.next();
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        if (profile.getPlayer().isEmpty()) {
            throw new QuestRuntimeException("Player is offline");
        }
        final Hero hero = Heroes.getInstance().getCharacterManager().getHero(profile.getPlayer().get());
        if (hero == null) {
            return false;
        }
        return hero.canUseSkill(skillName);
    }

}
