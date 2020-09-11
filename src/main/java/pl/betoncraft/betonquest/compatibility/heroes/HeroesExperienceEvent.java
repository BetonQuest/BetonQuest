package pl.betoncraft.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Adds the experience the a class.
 */
public class HeroesExperienceEvent extends QuestEvent {

    private boolean primary;
    private VariableNumber amount;

    public HeroesExperienceEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        primary = instruction.next().equalsIgnoreCase("primary");
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
