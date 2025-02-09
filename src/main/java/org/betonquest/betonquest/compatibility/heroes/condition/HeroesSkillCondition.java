package org.betonquest.betonquest.compatibility.heroes.condition;

import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.instruction.variable.VariableString;

/**
 * Checks if the player has access to specified Heroes skill.
 */
public class HeroesSkillCondition implements OnlineCondition {
    /**
     * The {@link CharacterManager} of the Heroes plugin.
     */
    private final CharacterManager characterManager;

    /**
     * The {@link VariableString} of the skill name.
     */
    private final VariableString skillNameVar;

    /**
     * Create a new Heroes Skill Condition.
     *
     * @param characterManager The {@link CharacterManager} of the Heroes plugin.
     * @param skillNameVar     The {@link VariableString} of the skill name.
     */
    public HeroesSkillCondition(final CharacterManager characterManager, final VariableString skillNameVar) {
        this.characterManager = characterManager;
        this.skillNameVar = skillNameVar;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final Hero hero = characterManager.getHero(profile.getPlayer());
        return hero != null && hero.canUseSkill(skillNameVar.getValue(profile));
    }
}
