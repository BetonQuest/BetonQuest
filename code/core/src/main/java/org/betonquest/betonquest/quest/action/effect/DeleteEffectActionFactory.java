package org.betonquest.betonquest.quest.action.effect;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;

/**
 * Factory to create delete effect actions from {@link Instruction}s.
 */
public class DeleteEffectActionFactory implements PlayerActionFactory {

    /**
     * Create the delete effect action factory.
     */
    public DeleteEffectActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<List<PotionEffectType>> effects;
        final boolean any = instruction.bool().getFlag("any", true)
                .getValue(null).orElse(false);
        if (!any && instruction.size() > 1 && !instruction.nextElement().startsWith("conditions:")) {
            effects = instruction.chainForArgument(instruction.current()).parse(type -> {
                final PotionEffectType effect = PotionEffectType.getByName(type);
                if (effect == null) {
                    throw new QuestException("Unknown effect type: " + type);
                }
                return effect;
            }).list().get();
        } else {
            effects = new DefaultArgument<>(Collections.emptyList());
        }
        return new OnlineActionAdapter(new DeleteEffectAction(effects));
    }
}
