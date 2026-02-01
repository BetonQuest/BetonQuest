package org.betonquest.betonquest.quest.condition.eval;

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
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.kernel.processor.adapter.ConditionAdapter;
import org.betonquest.betonquest.kernel.registry.QuestTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;

/**
 * A condition which evaluates to another condition.
 */
public class EvalCondition implements NullableCondition {

    /**
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * The condition type registry providing factories to parse the evaluated instruction.
     */
    private final ConditionTypeRegistry conditionTypeRegistry;

    /**
     * The quest package to relate the condition to.
     */
    private final QuestPackage pack;

    /**
     * The evaluation input.
     */
    private final Argument<String> evaluation;

    /**
     * The {@link BetonQuestApi}.
     */
    private final BetonQuestApi betonQuestApi;

    /**
     * The scheduler to use for synchronous execution.
     */
    private final BukkitScheduler scheduler;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Creates a new Eval condition.
     *
     * @param betonQuestApi         the {@link BetonQuestApi} to use
     * @param placeholders          the {@link Placeholders} to create and resolve placeholders
     * @param packManager           the quest package manager to get quest packages from
     * @param conditionTypeRegistry the condition type registry providing factories to parse the evaluated instruction
     * @param pack                  the quest package to relate the condition to
     * @param evaluation            the evaluation input
     * @param scheduler             the scheduler to use for synchronous execution
     * @param plugin                the plugin instance
     */
    public EvalCondition(final BetonQuestApi betonQuestApi, final Placeholders placeholders, final QuestPackageManager packManager, final ConditionTypeRegistry conditionTypeRegistry,
                         final QuestPackage pack, final Argument<String> evaluation, final BukkitScheduler scheduler, final Plugin plugin) {
        this.placeholders = placeholders;
        this.packManager = packManager;
        this.betonQuestApi = betonQuestApi;
        this.conditionTypeRegistry = conditionTypeRegistry;
        this.pack = pack;
        this.evaluation = evaluation;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    /**
     * Constructs a condition with a given instruction and returns it.
     *
     * @param parsers               the {@link ArgumentParsers} to use to parse arguments
     * @param placeholders          the {@link Placeholders} to create and resolve placeholders
     * @param packManager           the quest package manager to get quest packages from
     * @param instruction           the instruction string to parse
     * @param conditionTypeRegistry the condition type registry providing factories to parse the evaluated instruction
     * @param pack                  the quest package to relate the condition to
     * @return the condition
     * @throws QuestException if the condition could not be created
     */
    public static ConditionAdapter createCondition(final ArgumentParsers parsers, final Placeholders placeholders, final QuestPackageManager packManager,
                                                   final QuestTypeRegistry<PlayerCondition, PlayerlessCondition, ConditionAdapter> conditionTypeRegistry,
                                                   final QuestPackage pack, final String instruction) throws QuestException {
        final Instruction conditionInstruction = new DefaultInstruction(placeholders, packManager, pack, null, parsers, instruction);
        final TypeFactory<ConditionAdapter> conditionFactory = conditionTypeRegistry.getFactory(conditionInstruction.getPart(0));
        return conditionFactory.parseInstruction(conditionInstruction);
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final ConditionAdapter condition = createCondition(betonQuestApi.getArgumentParsers(), placeholders, packManager, conditionTypeRegistry, pack, evaluation.getValue(profile));
        if (condition.isPrimaryThreadEnforced() && !Bukkit.isPrimaryThread()) {
            try {
                return scheduler.callSyncMethod(plugin, () -> condition.check(profile)).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new QuestException("Failed to check condition in primary thread", e);
            }
        }
        return condition.check(profile);
    }
}
