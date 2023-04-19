package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.attributes.AttributeType;
import com.herocraftonline.heroes.characters.Hero;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Checks an attribute of a player and if greater than or equal to a level
 */
@SuppressWarnings("PMD.CommentRequired")
public class HeroesAttributeCondition extends Condition {
    private final AttributeType attribute;

    private final VariableNumber level;

    public HeroesAttributeCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        attribute = findAttribute(instruction.next());
        level = instruction.getVarNum(instruction.next());
    }

    private AttributeType findAttribute(final String string) throws InstructionParseException {
        for (final AttributeType t : AttributeType.values()) {
            if (t.name().equalsIgnoreCase(string)) {
                return t;
            }
        }
        throw new InstructionParseException("Attribute '" + string + "' does not exist!");
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final Hero hero = Heroes.getInstance().getCharacterManager().getHero(profile.getOnlineProfile().get().getPlayer());
        return hero.getAttributeValue(attribute) >= level.getInt(profile);
    }
}
