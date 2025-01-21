package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Adds the experience the a class.
 */
@SuppressWarnings("PMD.CommentRequired")
public class HeroesExperienceEvent extends QuestEvent {
    private final boolean primary;

    private final VariableNumber amount;

    public HeroesExperienceEvent(final Instruction instruction) throws QuestException {
        super(instruction, true);
        primary = "primary".equalsIgnoreCase(instruction.next());
        amount = instruction.get(VariableNumber::new);
    }

    @Override
    protected Void execute(final Profile profile) throws QuestException {
        final Hero hero = Heroes.getInstance().getCharacterManager().getHero(profile.getOnlineProfile().get().getPlayer());
        if (hero == null) {
            return null;
        }
        if (primary) {
            if (hero.getHeroClass() == null) {
                return null;
            }
            hero.addExp(amount.getInt(profile), hero.getHeroClass(), hero.getPlayer().getLocation());
        } else {
            if (hero.getSecondClass() == null) {
                return null;
            }
            hero.addExp(amount.getInt(profile), hero.getSecondClass(), hero.getPlayer().getLocation());
        }
        return null;
    }
}
