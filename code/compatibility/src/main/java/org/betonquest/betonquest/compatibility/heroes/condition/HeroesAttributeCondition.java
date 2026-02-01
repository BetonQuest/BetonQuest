package org.betonquest.betonquest.compatibility.heroes.condition;

import com.herocraftonline.heroes.attributes.AttributeType;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.OnlineCondition;

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
    private final Argument<String> attribute;

    /**
     * The level.
     */
    private final Argument<Number> level;

    /**
     * Create a new Heroes Attribute Condition.
     *
     * @param characterManager The {@link CharacterManager} of the Heroes plugin.
     * @param attribute        The attribute name.
     * @param level            The level.
     */
    public HeroesAttributeCondition(final CharacterManager characterManager, final Argument<String> attribute, final Argument<Number> level) {
        this.characterManager = characterManager;
        this.attribute = attribute;
        this.level = level;
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
        final String name = attribute.getValue(profile);
        final Hero hero = characterManager.getHero(profile.getPlayer());
        return hero.getAttributeValue(findAttribute(name)) >= level.getValue(profile).intValue();
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
