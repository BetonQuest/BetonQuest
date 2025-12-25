package org.betonquest.betonquest.lib.instruction.argument;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.chain.ChainableInstruction;
import org.betonquest.betonquest.api.instruction.chain.DecoratableChainRetriever;
import org.betonquest.betonquest.api.instruction.chain.InstructionChainParser;
import org.betonquest.betonquest.api.instruction.chain.NumberChainRetriever;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.lib.instruction.chain.DefaultDecoratableChainRetriever;
import org.betonquest.betonquest.lib.instruction.chain.DefaultNumberChainRetriever;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * The default implementation for {@link InstructionChainParser}.
 */
public class DefaultInstructionChainParser implements InstructionChainParser {

    /**
     * The chainable instruction.
     */
    private final ChainableInstruction instruction;

    /**
     * The provider for default argument parsers.
     */
    private final ArgumentParsers argumentParsers;

    /**
     * Sole constructor.
     *
     * @param instruction     the chainable instruction base
     * @param argumentParsers the provider for parsers
     */
    public DefaultInstructionChainParser(final ChainableInstruction instruction, final ArgumentParsers argumentParsers) {
        this.instruction = instruction;
        this.argumentParsers = argumentParsers;
    }

    @Override
    public <T> DecoratableChainRetriever<T> parse(final InstructionArgumentParser<T> argument) {
        return new DefaultDecoratableChainRetriever<>(instruction, argument);
    }

    @Override
    public DecoratableChainRetriever<String> string() {
        return new DefaultDecoratableChainRetriever<>(instruction, argumentParsers.string());
    }

    @Override
    public DecoratableChainRetriever<Boolean> bool() {
        return new DefaultDecoratableChainRetriever<>(instruction, argumentParsers.bool());
    }

    @Override
    public DecoratableChainRetriever<Vector> vector() {
        return new DefaultDecoratableChainRetriever<>(instruction, argumentParsers.vector());
    }

    @Override
    public DecoratableChainRetriever<World> world() {
        return new DefaultDecoratableChainRetriever<>(instruction, argumentParsers.world());
    }

    @Override
    public DecoratableChainRetriever<Location> location() {
        return new DefaultDecoratableChainRetriever<>(instruction, argumentParsers.location());
    }

    @Override
    public DecoratableChainRetriever<ItemWrapper> item() {
        return new DefaultDecoratableChainRetriever<>(instruction, argumentParsers.item());
    }

    @Override
    public DecoratableChainRetriever<BlockSelector> blockSelector() {
        return new DefaultDecoratableChainRetriever<>(instruction, argumentParsers.blockSelector());
    }

    @Override
    public DecoratableChainRetriever<String> packageIdentifier() {
        return new DefaultDecoratableChainRetriever<>(instruction, argumentParsers.packageIdentifier());
    }

    @Override
    public DecoratableChainRetriever<Component> component() {
        return new DefaultDecoratableChainRetriever<>(instruction, argumentParsers.component());
    }

    @Override
    public DecoratableChainRetriever<UUID> uuid() {
        return new DefaultDecoratableChainRetriever<>(instruction, argumentParsers.uuid());
    }

    @Override
    public NumberChainRetriever number() {
        return new DefaultNumberChainRetriever(instruction, argumentParsers.number());
    }

    @Override
    public <E extends Enum<E>> DecoratableChainRetriever<E> enumeration(final Class<E> enumType) {
        return new DefaultDecoratableChainRetriever<>(instruction, argumentParsers.forEnum(enumType));
    }
}
