package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Removes potion effects from the player
 */
@SuppressWarnings("PMD.CommentRequired")
public class DelEffectEvent extends QuestEvent {

    private final List<PotionEffectType> effects;
    private final boolean any;

    public DelEffectEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String next = instruction.next();

        if ("any".equalsIgnoreCase(next)) {
            any = true;
            effects = Collections.emptyList();
        } else {
            any = false;
            effects = instruction.getList(next, type -> {
                final PotionEffectType effect = PotionEffectType.getByName(type.toUpperCase(Locale.ROOT));
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
