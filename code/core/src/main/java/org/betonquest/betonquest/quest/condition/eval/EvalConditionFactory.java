package org.betonquest.betonquest.quest.condition.eval;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * A factory for creating Eval conditions.
 */
public class EvalConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

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
     * Creates a new Eval condition factory.
     *
     * @param betonQuestApi         the BetonQuest API
     * @param placeholders          the {@link Placeholders} to create and resolve placeholders
     * @param packManager           the quest package manager to get quest packages from
     * @param conditionTypeRegistry the condition type registry providing factories to parse the evaluated instruction
     * @param scheduler             the scheduler to use for synchronous execution
     * @param plugin                the plugin instance
     */
    public EvalConditionFactory(final BetonQuestApi betonQuestApi, final Placeholders placeholders, final QuestPackageManager packManager,
                                final ConditionTypeRegistry conditionTypeRegistry, final BukkitScheduler scheduler, final Plugin plugin) {
        this.placeholders = placeholders;
        this.packManager = packManager;
        this.betonQuestApi = betonQuestApi;
        this.conditionTypeRegistry = conditionTypeRegistry;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return parseEvalCondition(instruction);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return parseEvalCondition(instruction);
    }

    private NullableConditionAdapter parseEvalCondition(final Instruction instruction) throws QuestException {
        final String rawInstruction = String.join(" ", instruction.getValueParts());
        return new NullableConditionAdapter(new EvalCondition(betonQuestApi, placeholders, packManager, conditionTypeRegistry,
                instruction.getPackage(), instruction.chainForArgument(rawInstruction).string().get(), scheduler, plugin));
    }
}
