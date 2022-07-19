package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.attributes.AttributeType;
import com.herocraftonline.heroes.characters.Hero;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.utils.PlayerConverter;

/**
 * Checks an attribute of a player and if greater than or equal to a level
 * Formatted as such "heroesattribute {attribute} {level}
 */
@SuppressWarnings("PMD.CommentRequired")
public class HeroesAttributeCondition extends Condition {
    private AttributeType attribute;
    private VariableNumber level;

    /**
     * Creates new instance of the condition. The condition should parse
     * instruction string at this point and extract all the data from it. If
     * anything goes wrong, throw {@link InstructionParseException} with an
     * error message describing the problem.
     *
     * @param instruction the Instruction object; you can get one from ID instance with
     *                    {@link ID#generateInstruction()
     *                    ID.generateInstruction()} or create it from an instruction
     *                    string
     */
    public HeroesAttributeCondition(Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        String string = instruction.next();
        for(AttributeType t : AttributeType.values()) {
            if(t.name().equalsIgnoreCase(string)) {
                attribute = t;
                break;
            }
        }
        if(attribute == null) {
            throw new InstructionParseException("Attribute '" + string + "' does not exist!");
        }

        level = instruction.getVarNum(instruction.next());
    }

    @Override
    protected Boolean execute(String playerID) throws QuestRuntimeException {
        final Hero hero = Heroes.getInstance().getCharacterManager().getHero(PlayerConverter.getPlayer(playerID));

        return hero.getAttributeValue(attribute) >= level.getInt(playerID);
    }
}
