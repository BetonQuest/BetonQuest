package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.potion.PotionEffectType;

/**
 * Requires the player to have active potion effect
 */
@SuppressWarnings("PMD.CommentRequired")
public class EffectCondition extends Condition {

    private final PotionEffectType type;

    public EffectCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String string = instruction.next();
        type = PotionEffectType.getByName(string);
        if (type == null) {
            throw new InstructionParseException("Effect " + string + " does not exist");
        }
    }

    @Override
    protected Boolean execute(final String playerID) {
        return PlayerConverter.getPlayer(playerID).hasPotionEffect(type);
    }

}
