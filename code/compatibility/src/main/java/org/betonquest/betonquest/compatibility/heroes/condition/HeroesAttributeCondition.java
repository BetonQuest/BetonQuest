package org.betonquest.betonquest.compatibility.heroes.condition;

import com.herocraftonline.heroes.attributes.AttributeType;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;

/**
 * Checks an attribute of a player and if greater than or equal to a level.
 */
public class HeroesAttributeCondition implements OnlineCondition {

    /**
     * The {@link CharacterManager} of the Heroes plugin.
     */
    private final CharacterManager characterManager;

    /**
     * The attribute name.
     */
    private final Variable<String> attributeVar;

    /**
     * The level.
     */
    private final Variable<Number> levelVar;

    /**
     * Create a new Heroes Attribute Condition.
     *
     * @param characterManager The {@link CharacterManager} of the Heroes plugin.
     * @param attributeVar     The attribute name.
     * @param levelVar         The level.
     */
    public HeroesAttributeCondition(final CharacterManager characterManager, final Variable<String> attributeVar, final Variable<Number> levelVar) {
        this.characterManager = characterManager;
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
        final Hero hero = characterManager.getHero(profile.getPlayer());
        return hero.getAttributeValue(findAttribute(name)) >= levelVar.getValue(profile).intValue();
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
