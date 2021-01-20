package pl.betoncraft.betonquest.events;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Gives the player specified potion effect
 */
@SuppressWarnings("PMD.CommentRequired")
public class EffectEvent extends QuestEvent {

    private final PotionEffectType effect;
    private final VariableNumber duration;
    private final VariableNumber amplifier;
    private final boolean ambient;
    private final boolean hidden;
    private final boolean icon;

    public EffectEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String type = instruction.next();
        effect = PotionEffectType.getByName(type);
        if (effect == null) {
            throw new InstructionParseException("Effect type '" + type + "' does not exist");
        }
        try {
            duration = instruction.getVarNum();
            amplifier = instruction.getVarNum();
        } catch (final NumberFormatException e) {
            throw new InstructionParseException("Could not parse number arguments", e);
        }

        ambient = instruction.hasArgument("ambient");
        hidden = instruction.hasArgument("hidden");
        icon = !instruction.hasArgument("noicon");
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        PlayerConverter.getPlayer(playerID).addPotionEffect(
                new PotionEffect(effect, duration.getInt(playerID) * 20, amplifier.getInt(playerID) - 1, ambient, !hidden, icon));
        return null;
    }

}
