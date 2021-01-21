package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;

/**
 * Adds the experience the a class.
 */
@SuppressWarnings("PMD.CommentRequired")
public class HeroesExperienceEvent extends QuestEvent {

    private final boolean primary;
    private final VariableNumber amount;

    public HeroesExperienceEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        primary = "primary".equalsIgnoreCase(instruction.next());
        amount = instruction.getVarNum();
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Hero hero = Heroes.getInstance().getCharacterManager().getHero(PlayerConverter.getPlayer(playerID));
        if (hero == null) {
            return null;
        }
        if (primary) {
            if (hero.getHeroClass() == null) {
                return null;
            }
            hero.addExp(amount.getInt(playerID), hero.getHeroClass(), hero.getPlayer().getLocation());
        } else {
            if (hero.getSecondClass() == null) {
                return null;
            }
            hero.addExp(amount.getInt(playerID), hero.getSecondClass(), hero.getPlayer().getLocation());
        }
        return null;
    }

}
