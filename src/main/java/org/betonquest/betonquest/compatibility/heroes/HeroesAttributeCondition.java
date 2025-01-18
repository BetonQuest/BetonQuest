package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.attributes.AttributeType;
import com.herocraftonline.heroes.characters.Hero;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Checks an attribute of a player and if greater than or equal to a level
 */
@SuppressWarnings("PMD.CommentRequired")
public class HeroesAttributeCondition extends Condition {
    private final AttributeType attribute;

    private final VariableNumber level;

    public HeroesAttributeCondition(final Instruction instruction) throws QuestException {
        super(instruction, true);
        attribute = findAttribute(instruction.next());
        level = instruction.get(VariableNumber::new);
    }

    private AttributeType findAttribute(final String string) throws QuestException {
        for (final AttributeType t : AttributeType.values()) {
            if (t.name().equalsIgnoreCase(string)) {
                return t;
            }
        }
        throw new QuestException("Attribute '" + string + "' does not exist!");
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestException {
        final Hero hero = Heroes.getInstance().getCharacterManager().getHero(profile.getOnlineProfile().get().getPlayer());
        return hero.getAttributeValue(attribute) >= level.getInt(profile);
    }
}
