package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;

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
    protected Boolean execute(final String playerID) {
        final Hero hero = Heroes.getInstance().getCharacterManager().getHero(PlayerConverter.getPlayer(playerID));
        if (hero == null) {
            return false;
        }
        return hero.canUseSkill(skillName);
    }

}
