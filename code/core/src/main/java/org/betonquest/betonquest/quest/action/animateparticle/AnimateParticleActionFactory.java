package org.betonquest.betonquest.quest.action.animateparticle;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionDefinition;
import org.betonquest.betonquest.api.function.FunctionExpression;
import org.betonquest.betonquest.api.function.FunctionProvider;
import org.betonquest.betonquest.api.function.MathFunction;
import org.betonquest.betonquest.api.identifier.FunctionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.chain.DecoratableChainRetriever;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.service.function.Functions;
import org.betonquest.betonquest.lib.argument.type.TimeUnit;
import org.betonquest.betonquest.lib.function.assignment.NumberSourceAssignment;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Factory to create {@link AnimateParticleAction}s from {@link Instruction}s.
 */
public class AnimateParticleActionFactory implements PlayerlessActionFactory, PlayerActionFactory {

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The scheduler instance to use.
     */
    private final BukkitScheduler scheduler;

    /**
     * Function provider to evaluate function arguments.
     */
    private final Functions functions;

    /**
     * Creates the animate particle action factory.
     *
     * @param plugin    the plugin instance
     * @param scheduler the scheduler to use for delayed actions
     * @param functions the functions to provide access to {@link org.betonquest.betonquest.api.function.MathFunction}s
     */
    public AnimateParticleActionFactory(final Plugin plugin, final BukkitScheduler scheduler, final Functions functions) {
        this.plugin = plugin;
        this.scheduler = scheduler;
        this.functions = functions;
    }

    private NullableActionAdapter parse(final Instruction instruction) throws QuestException {
        final Argument<Particle> particle = instruction.enumeration(Particle.class).get();
        final Argument<Location> origin = instruction.location().get();
        final Argument<Number> start = instruction.number().get();
        final Argument<Number> end = instruction.number().get();
        final Argument<Number> samples = instruction.number().get();
        final DecoratableChainRetriever<MathFunction> functionRetriever = instruction.identifier(FunctionIdentifier.class)
                .map(functions::getFunction).prefilter("linear", new IdentityFunction());
        final Argument<MathFunction> functionX = functionRetriever.get("x", new ZeroFunction());
        final Argument<MathFunction> functionY = functionRetriever.get("y", new ZeroFunction());
        final Argument<MathFunction> functionZ = functionRetriever.get("z", new ZeroFunction());
        final Argument<Number> range = instruction.number().get("range", 32);
        final Argument<Number> extra = instruction.number().get("extra", 0);
        final Argument<Number> duration = instruction.number().get("duration", 0);
        final Argument<TimeUnit> unit = instruction.enumeration(TimeUnit.class).get("unit", TimeUnit.TICKS);
        final Argument<Number> batch = instruction.number().atLeast(1).get("batch", 1);
        final Argument<Vector> offset = instruction.vector().get("offset", new Vector(0, 0, 0));
        final Argument<Number> count = instruction.number().get("count", 1);
        final Optional<Argument<Vector>> color = instruction.vector().get("color");
        final Optional<Argument<ItemWrapper>> item = instruction.item().get("item");
        final Optional<Argument<BlockData>> blockdata = instruction.string().map(data -> Bukkit.getServer().createBlockData(data)).get("blockdata");
        final ParticleFunction animation = new ParticleFunction(functionX, functionY, functionZ, functions.getFunctionProvider(instruction.getPackage()));
        return new NullableActionAdapter(new AnimateParticleAction(plugin, scheduler, particle, origin, animation, start,
                end, samples, range, extra, duration, unit, batch, offset, count, color.orElse(null), item.orElse(null), blockdata.orElse(null)));
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return parse(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return parse(instruction);
    }

    /**
     * Simple function that always returns 0.
     */
    private static class ZeroFunction implements MathFunction {

        /**
         * Creates a new simple value function.
         */
        public ZeroFunction() {
        }

        @Override
        public FunctionDefinition definition() {
            return assignments -> Map.of();
        }

        @Override
        public FunctionExpression expression() {
            return (functions, assignments) -> new NumberSourceAssignment(0);
        }

        @Override
        public FunctionAssignment evaluate(final FunctionProvider functions, final List<FunctionAssignment> assignments) {
            return new NumberSourceAssignment(0);
        }
    }

    /**
     * Simple function that always returns its input.
     */
    private static class IdentityFunction implements MathFunction {

        /**
         * Creates a new simple value function.
         */
        public IdentityFunction() {
        }

        @Override
        public FunctionDefinition definition() {
            return assignments -> Map.of();
        }

        @Override
        public FunctionExpression expression() {
            return (functions, assignments) -> assignments.values().iterator().next();
        }

        @Override
        public FunctionAssignment evaluate(final FunctionProvider functions, final List<FunctionAssignment> assignments) {
            return assignments.get(0);
        }
    }
}
