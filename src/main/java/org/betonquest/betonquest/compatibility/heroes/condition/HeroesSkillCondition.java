package org.betonquest.betonquest.compatibility.heroes.condition;

import com.herocraftonline.heroes.Heroes;
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
     * The {@link VariableString} of the skill name.
     */
    private final VariableString skillNameVar;

    /**
     * Create a new Heroes Skill Condition.
     *
     * @param skillNameVar The {@link VariableString} of the skill name.
     */
    public HeroesSkillCondition(final VariableString skillNameVar) {
        this.skillNameVar = skillNameVar;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final Hero hero = Heroes.getInstance().getCharacterManager().getHero(profile.getPlayer());
        return hero != null && hero.canUseSkill(skillNameVar.getValue(profile));
    }
}
