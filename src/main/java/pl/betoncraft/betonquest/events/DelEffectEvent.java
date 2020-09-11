package pl.betoncraft.betonquest.events;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Collections;
import java.util.List;

/**
 * Removes potion effects from the player
 */
public class DelEffectEvent extends QuestEvent {

    private final List<PotionEffectType> effects;
    private final boolean any;

    public DelEffectEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String next = instruction.next();

        if (next.equalsIgnoreCase("any")) {
            any = true;
            effects = Collections.emptyList();
        } else {
            any = false;
            effects = instruction.getList(next, type -> {
                final PotionEffectType effect = PotionEffectType.getByName(type.toUpperCase());
                if (effect == null) {
                    throw new InstructionParseException("Effect type '" + type + "' does not exist");
                } else {
                    return effect;
                }
            });
        }
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        if (any) {
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        } else {
            effects.forEach(player::removePotionEffect);
        }
        return null;
    }

}
