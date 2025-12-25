package org.betonquest.betonquest.compatibility.heroes.condition;

import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;

/**
 * Checks if the player has access to specified Heroes skill.
 */
public class HeroesSkillCondition implements OnlineCondition {

    /**
     * The {@link CharacterManager} of the Heroes plugin.
     */
    private final CharacterManager characterManager;

    /**
     * The skill name.
     */
    private final Argument<String> skillNameVar;

    /**
     * Create a new Heroes Skill Condition.
     *
     * @param characterManager The {@link CharacterManager} of the Heroes plugin.
     * @param skillNameVar     The skill name.
     */
    public HeroesSkillCondition(final CharacterManager characterManager, final Argument<String> skillNameVar) {
        this.characterManager = characterManager;
        this.skillNameVar = skillNameVar;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final Hero hero = characterManager.getHero(profile.getPlayer());
        return hero != null && hero.canUseSkill(skillNameVar.getValue(profile));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
