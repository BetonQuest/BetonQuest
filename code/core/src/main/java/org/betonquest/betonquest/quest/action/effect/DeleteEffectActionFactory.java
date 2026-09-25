package org.betonquest.betonquest.quest.action.effect;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.PotionEffectTypeParser;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
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
        final Argument<List<PotionEffectType>> effects = instruction.parse(PotionEffectTypeParser.POTION_EFFECT_TYPE)
                .list().prefilter("any", Collections.emptyList()).get();
        return new OnlineActionAdapter(new DeleteEffectAction(effects));
    }
}
