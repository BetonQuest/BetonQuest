package org.betonquest.betonquest.compatibility.heroes.condition;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.attributes.AttributeType;
import com.herocraftonline.heroes.characters.Hero;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;

/**
 * Checks an attribute of a player and if greater than or equal to a level.
 */
public class HeroesAttributeCondition implements OnlineCondition {
    /**
     * The {@link VariableString} of the attribute name.
     */
    private final VariableString attributeVar;

    /**
     * The {@link VariableNumber} of the level.
     */
    private final VariableNumber levelVar;

    /**
     * Create a new Heroes Attribute Condition.
     *
     * @param attributeVar The {@link VariableString} of the attribute name.
     * @param levelVar     The {@link VariableNumber} of the level.
     */
    public HeroesAttributeCondition(final VariableString attributeVar, final VariableNumber levelVar) {
        this.attributeVar = attributeVar;
        this.levelVar = levelVar;
    }

    private AttributeType findAttribute(final String name) throws QuestException {
        final AttributeType attributeType = AttributeType.getAttribute(name);
        if (attributeType == null) {
            throw new QuestException("Attribute '" + name + "' does not exist!");
        }
        return attributeType;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final String name = attributeVar.getValue(profile);
        final Hero hero = Heroes.getInstance().getCharacterManager().getHero(profile.getPlayer());
        return hero.getAttributeValue(findAttribute(name)) >= levelVar.getValue(profile).intValue();
    }
}
