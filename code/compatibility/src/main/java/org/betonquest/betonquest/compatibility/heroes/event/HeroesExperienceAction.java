package org.betonquest.betonquest.compatibility.heroes.event;

import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;
import org.betonquest.betonquest.compatibility.heroes.HeroesClassType;

/**
 * Adds the experience to a class.
 */
public class HeroesExperienceAction implements OnlineAction {

    /**
     * The {@link CharacterManager} of the Heroes plugin.
     */
    private final CharacterManager characterManager;

    /**
     * The {@link HeroesClassType} of the class to add experience to.
     */
    private final Argument<HeroesClassType> classType;

    /**
     * The amount of experience to add.
     */
    private final Argument<Number> amount;

    /**
     * Create a new Heroes Experience Event.
     *
     * @param characterManager The {@link CharacterManager} of the Heroes plugin.
     * @param classType        The {@link HeroesClassType} of the class to add experience to.
     * @param amount           The amount of experience to add.
     */
    public HeroesExperienceAction(final CharacterManager characterManager,
                                  final Argument<HeroesClassType> classType, final Argument<Number> amount) {
        this.characterManager = characterManager;
        this.classType = classType;
        this.amount = amount;
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
        hero.addExp(amount.getValue(profile).intValue(), heroClass, hero.getPlayer().getLocation());
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
