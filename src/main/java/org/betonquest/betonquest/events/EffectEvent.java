package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
