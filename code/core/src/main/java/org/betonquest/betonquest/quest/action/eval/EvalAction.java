package org.betonquest.betonquest.quest.action.eval;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.TypeFactory;
import org.betonquest.betonquest.api.quest.action.NullableAction;
import org.betonquest.betonquest.kernel.processor.adapter.ActionAdapter;
import org.betonquest.betonquest.kernel.registry.quest.ActionTypeRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;

/**
 * An action which evaluates to another action.
 */
public class EvalAction implements NullableAction {

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
     * The quest package to relate the action to.
     */
    private final QuestPackage pack;

    /**
     * The evaluation input.
     */
    private final Argument<String> evaluation;

    /**
     * The scheduler to use for synchronous execution.
     */
    private final BukkitScheduler scheduler;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The {@link BetonQuestApi}.
     */
    private final BetonQuestApi betonQuestApi;

    /**
     * Created a new Eval action.
     *
     * @param betonQuestApi      the BetonQuest API
     * @param placeholders       the {@link Placeholders} to create and resolve placeholders
     * @param packManager        the quest package manager to get quest packages from
     * @param actionTypeRegistry the action type registry providing factories to parse the evaluated instruction
     * @param pack               the quest package to relate the action to
     * @param evaluation         the evaluation input
     * @param scheduler          the scheduler to use for synchronous execution
     * @param plugin             the plugin instance
     */
    public EvalAction(final BetonQuestApi betonQuestApi, final Placeholders placeholders, final QuestPackageManager packManager,
                      final ActionTypeRegistry actionTypeRegistry, final QuestPackage pack,
                      final Argument<String> evaluation, final BukkitScheduler scheduler, final Plugin plugin) {
        this.placeholders = placeholders;
        this.packManager = packManager;
        this.betonQuestApi = betonQuestApi;
        this.actionTypeRegistry = actionTypeRegistry;
        this.pack = pack;
        this.evaluation = evaluation;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    /**
     * Constructs an action with a given instruction and returns it.
     *
     * @param parsers            the {@link ArgumentParsers} to use to parse arguments
     * @param placeholders       the {@link Placeholders} to create and resolve placeholders
     * @param packManager        the quest package manager to get quest packages from
     * @param instruction        the instruction string to parse
     * @param actionTypeRegistry the action type registry providing factories to parse the evaluated instruction
     * @param pack               the quest package to relate the action to
     * @return the action
     * @throws QuestException if the action could not be created
     */
    public static ActionAdapter createAction(final ArgumentParsers parsers, final Placeholders placeholders,
                                             final QuestPackageManager packManager, final ActionTypeRegistry actionTypeRegistry,
                                             final QuestPackage pack, final String instruction) throws QuestException {
        final Instruction actionInstruction = new DefaultInstruction(placeholders, packManager, pack, null, parsers, instruction);
        final TypeFactory<ActionAdapter> actionFactory = actionTypeRegistry.getFactory(actionInstruction.getPart(0));
        return actionFactory.parseInstruction(actionInstruction);
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final ActionAdapter action = createAction(betonQuestApi.getArgumentParsers(), placeholders, packManager, actionTypeRegistry, pack, evaluation.getValue(profile));
        if (action.isPrimaryThreadEnforced() && !Bukkit.isPrimaryThread()) {
            try {
                scheduler.callSyncMethod(plugin, () -> action.fire(profile)).get();
                return;
            } catch (InterruptedException | ExecutionException e) {
                throw new QuestException("Failed to execute action in primary thread", e);
            }
        }
        action.fire(profile);
    }
}
