package org.betonquest.betonquest.particle.datafunction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.particle.deltafunction.DeltaFunctionParser;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

/**
 * A block data provider usable as an argument.
 */
public class BlockDataProvider implements ParticleDataFunctionProvider {

    /**
     * The block data argument.
     */
    @Nullable
    private Argument<BlockData> blockData;

    /**
     * Creates a new block data provider.
     */
    public BlockDataProvider() {
    }

    @Override
    public ParticleDataFunction<BlockData> getValue(@Nullable final Profile profile) throws QuestException {
        if (blockData == null) {
            throw new QuestException("BlockData particles require 'block' argument");
        }
        final BlockData value = blockData.getValue(profile);
        return (d1, d2) -> value;
    }

    @Override
    public boolean match(final Particle particle) {
        return particle.getDataType().equals(BlockData.class);
    }

    @Override
    public void read(final SectionInstruction instruction, final DeltaFunctionParser deltaFunctionParser) throws QuestException {
        this.blockData = instruction.read().value("block").parse(input -> Bukkit.getServer().createBlockData(input)).get();
    }
}
