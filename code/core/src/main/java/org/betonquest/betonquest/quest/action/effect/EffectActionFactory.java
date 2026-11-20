package org.betonquest.betonquest.quest.action.effect;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.bukkit.potion.PotionEffectType;

/**
 * Factory to create effect actions from {@link Instruction}s.
 */
public class EffectActionFactory implements PlayerActionFactory {

    /**
     * Create the effect action factory.
     */
    public EffectActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final PotionEffectType effect = PotionEffectType.getByName(instruction.string().get().getValue(null));
        if (effect == null) {
            throw new QuestException("Effect type '%s' does not exist".formatted(instruction.current()));
        }
        try {
            final Argument<Number> duration = instruction.number().get();
            final Argument<Number> level = instruction.number().get();
            final FlagArgument<Boolean> ambient = instruction.bool().getFlag("ambient", true);
            final FlagArgument<Boolean> hidden = instruction.bool().getFlag("hidden", true);
            final FlagArgument<Boolean> noicon = instruction.bool().getFlag("noicon", true);
            return new OnlineActionAdapter(new EffectAction(effect, duration, level, ambient, hidden, noicon));
        } catch (final QuestException e) {
            throw new QuestException("Could not parse effect duration and amplifier", e);
        }
    }
}
