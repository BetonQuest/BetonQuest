package org.betonquest.betonquest.quest.action.eval;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.betonquest.betonquest.kernel.registry.quest.ActionTypeRegistry;

/**
 * A factory for creating Eval events.
 */
public class EvalActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * The event type registry providing factories to parse the evaluated instruction.
     */
    private final ActionTypeRegistry actionTypeRegistry;

    /**
     * Create a new Eval event factory.
     *
     * @param placeholders       the {@link Placeholders} to create and resolve placeholders
     * @param packManager        the quest package manager to get quest packages from
     * @param actionTypeRegistry the event type registry providing factories to parse the evaluated instruction
     */
    public EvalActionFactory(final Placeholders placeholders, final QuestPackageManager packManager, final ActionTypeRegistry actionTypeRegistry) {
        this.placeholders = placeholders;
        this.packManager = packManager;
        this.actionTypeRegistry = actionTypeRegistry;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return parseEvalEvent(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return parseEvalEvent(instruction);
    }

    private NullableActionAdapter parseEvalEvent(final Instruction instruction) throws QuestException {
        final String rawInstruction = String.join(" ", instruction.getValueParts());
        return new NullableActionAdapter(new EvalAction(placeholders, packManager, actionTypeRegistry, instruction.getPackage(),
                instruction.chainForArgument(rawInstruction).string().get()));
    }
}
