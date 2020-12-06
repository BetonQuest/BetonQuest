package pl.betoncraft.betonquest.conditions;

import org.bukkit.potion.PotionEffectType;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

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
