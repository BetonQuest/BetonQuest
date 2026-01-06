package org.betonquest.betonquest.quest.action.effect;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;
import org.betonquest.betonquest.lib.instruction.argument.DefaultListArgument;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;

/**
 * Factory to create delete effect actions from {@link Instruction}s.
 */
public class DeleteEffectActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the delete effect action factory.
     *
     * @param loggerFactory the logger factory to create a logger for the actions
     */
    public DeleteEffectActionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<List<PotionEffectType>> effects;
        final boolean any = instruction.bool().getFlag("any", true)
                .getValue(null).orElse(false);
        if (!any && instruction.size() > 1) {
            effects = instruction.parse(type -> {
                final PotionEffectType effect = PotionEffectType.getByName(type);
                if (effect == null) {
                    throw new QuestException("Unknown effect type: " + type);
                }
                return effect;
            }).list().get();
        } else {
            effects = new DefaultListArgument<>(Collections.emptyList());
        }
        return new OnlineActionAdapter(new DeleteEffectAction(effects),
                loggerFactory.create(DeleteEffectAction.class), instruction.getPackage());
    }
}
