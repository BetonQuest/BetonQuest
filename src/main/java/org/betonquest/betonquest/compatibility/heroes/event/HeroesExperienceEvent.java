package org.betonquest.betonquest.compatibility.heroes.event;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Adds the experience to a class.
 */
public class HeroesExperienceEvent implements OnlineEvent {
    /**
     * Whether the experience should be added to the primary class.
     */
    private final boolean primary;

    /**
     * The amount of experience to add.
     */
    private final VariableNumber amountVar;

    /**
     * Create a new Heroes Experience Event.
     *
     * @param primary   Whether the experience should be added to the primary class.
     * @param amountVar The {@link VariableNumber} of the amount of experience to add.
     */
    public HeroesExperienceEvent(final boolean primary, final VariableNumber amountVar) {
        this.primary = primary;
        this.amountVar = amountVar;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Hero hero = Heroes.getInstance().getCharacterManager().getHero(profile.getPlayer());
        if (hero == null) {
            return;
        }

        final HeroClass heroClass;
        if (primary) {
            heroClass = hero.getHeroClass();
        } else {
            heroClass = hero.getSecondaryClass();
        }

        if (heroClass == null) {
            throw new QuestException("The specified player does not have a class of the type: " + (primary ? "primary" : "secondary"));
        }
        hero.addExp(amountVar.getValue(profile).intValue(), heroClass, hero.getPlayer().getLocation());
    }
}
