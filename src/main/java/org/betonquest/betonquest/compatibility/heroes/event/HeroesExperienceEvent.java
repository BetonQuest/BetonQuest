package org.betonquest.betonquest.compatibility.heroes.event;

import com.herocraftonline.heroes.characters.CharacterManager;
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
     * The {@link CharacterManager} of the Heroes plugin.
     */
    private final CharacterManager characterManager;

    /**
     * If the type is the primary class or the secondary class.
     */
    private final boolean isPrimary;

    /**
     * The amount of experience to add.
     */
    private final VariableNumber amountVar;

    /**
     * Create a new Heroes Experience Event.
     *
     * @param characterManager The {@link CharacterManager} of the Heroes plugin.
     * @param isPrimary        If the type is the primary class or the secondary class.
     * @param amountVar        The {@link VariableNumber} of the amount of experience to add.
     */
    public HeroesExperienceEvent(final CharacterManager characterManager, final boolean isPrimary, final VariableNumber amountVar) {
        this.characterManager = characterManager;
        this.isPrimary = isPrimary;
        this.amountVar = amountVar;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Hero hero = characterManager.getHero(profile.getPlayer());
        if (hero == null) {
            return;
        }

        final HeroClass heroClass;
        if (isPrimary) {
            heroClass = hero.getHeroClass();
        } else {
            heroClass = hero.getSecondaryClass();
        }

        if (heroClass == null) {
            throw new QuestException("The specified player does not have a class of the type: " + (isPrimary ? "primary" : "secondary"));
        }
        hero.addExp(amountVar.getValue(profile).intValue(), heroClass, hero.getPlayer().getLocation());
    }
}
