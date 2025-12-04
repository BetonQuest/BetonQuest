package org.betonquest.betonquest.compatibility.heroes.event;

import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.compatibility.heroes.HeroesClassType;

/**
 * Adds the experience to a class.
 */
public class HeroesExperienceEvent implements OnlineEvent {
    /**
     * The {@link CharacterManager} of the Heroes plugin.
     */
    private final CharacterManager characterManager;

    /**
     * The {@link HeroesClassType} of the class to add experience to.
     */
    private final Variable<HeroesClassType> classType;

    /**
     * The amount of experience to add.
     */
    private final Variable<Number> amountVar;

    /**
     * Create a new Heroes Experience Event.
     *
     * @param characterManager The {@link CharacterManager} of the Heroes plugin.
     * @param classType        The {@link HeroesClassType} of the class to add experience to.
     * @param amountVar        The amount of experience to add.
     */
    public HeroesExperienceEvent(final CharacterManager characterManager,
                                 final Variable<HeroesClassType> classType, final Variable<Number> amountVar) {
        this.characterManager = characterManager;
        this.classType = classType;
        this.amountVar = amountVar;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Hero hero = characterManager.getHero(profile.getPlayer());
        if (hero == null) {
            return;
        }

        final boolean isPrimary = classType.getValue(profile) == HeroesClassType.PRIMARY;
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
