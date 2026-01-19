package org.betonquest.betonquest.quest.action.eval;

import org.betonquest.betonquest.BetonQuest;
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
import org.bukkit.scheduler.BukkitScheduler;

/**
 * A factory for creating Eval actions.
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
     * The action type registry providing factories to parse the evaluated instruction.
     */
    private final ActionTypeRegistry actionTypeRegistry;

    /**
     * The scheduler to use for synchronous execution.
     */
    private final BukkitScheduler scheduler;

    /**
     * The plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * Create a new Eval action factory.
     *
     * @param placeholders       the {@link Placeholders} to create and resolve placeholders
     * @param packManager        the quest package manager to get quest packages from
     * @param actionTypeRegistry the action type registry providing factories to parse the evaluated instruction
     * @param scheduler          the scheduler to use for synchronous execution
     * @param plugin             the plugin instance
     */
    public EvalActionFactory(final Placeholders placeholders, final QuestPackageManager packManager,
                             final ActionTypeRegistry actionTypeRegistry, final BukkitScheduler scheduler, final BetonQuest plugin) {
        this.placeholders = placeholders;
        this.packManager = packManager;
        this.actionTypeRegistry = actionTypeRegistry;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return parseEvalAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return parseEvalAction(instruction);
    }

    private NullableActionAdapter parseEvalAction(final Instruction instruction) throws QuestException {
        final String rawInstruction = String.join(" ", instruction.getValueParts());
        return new NullableActionAdapter(new EvalAction(plugin, placeholders, packManager, actionTypeRegistry, instruction.getPackage(),
                instruction.chainForArgument(rawInstruction).string().get(), scheduler, plugin));
    }
}
