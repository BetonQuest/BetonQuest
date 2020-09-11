package pl.betoncraft.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks if the player has access to specified Heroes skill.
 */
public class HeroesSkillCondition extends Condition {

    private String skillName;

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
