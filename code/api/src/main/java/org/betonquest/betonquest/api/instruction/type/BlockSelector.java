package org.betonquest.betonquest.api.instruction.type;

import org.betonquest.betonquest.api.QuestException;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;

import java.util.List;

/**
 * A BlockSelector represents a defined group of materials of which all of a random material may be fetched.
 */
public interface BlockSelector {

    /**
     * Get a random {@link Material}. If only one Material is represented by this {@link BlockSelector} this will be returned.
     *
     * @return A {@link Material}
     */
    Material getRandomMaterial();

    /**
     * Get a list of all {@link Material}s that match this {@link BlockSelector}.
     *
     * @return A copy of the list of matching {@link Material}s
     */
    List<Material> getMaterials();

    /**
     * Get a BlockData.
     * The Material is randomly selected from {@link BlockSelector#getRandomMaterial()}.
     * If the states contain regex {@link IllegalArgumentException} is thrown, if you apply this with
     * {@link BlockState#setBlockData(BlockData)}.
     *
     * @return A {@link BlockData}
     */
    BlockData getBlockData();

    /**
     * Set the {@link BlockData} returned from {@link BlockSelector#getBlockData()} to a block.
     *
     * @param block        The block that is changed by the {@link BlockData}
     * @param applyPhysics If physics should be active for that block
     * @throws QuestException when the block data could not be set
     */
    void setToBlock(Block block, boolean applyPhysics) throws QuestException;

    /**
     * Checks if a {@link Material} matched this {@link BlockSelector}. The {@link BlockState} is ignored.
     *
     * @param material The {@link Material} that should be compared
     * @return True if the {@link Material} is represented by this {@link BlockSelector}
     */
    boolean match(Material material);

    /**
     * Checks if a {@link Block} matched this {@link BlockSelector}.
     *
     * @param block      The {@link Block} that should be compared
     * @param exactMatch If false, the target block may have more {@link BlockState}s than this {@link BlockSelector}.
     *                   If true, the target block is not allowed to have more {@link BlockState}s than this
     *                   {@link BlockSelector}.
     * @return True if the {@link Material} is represented by this {@link BlockSelector} and the {@link BlockState} matches.
     */
    boolean match(Block block, boolean exactMatch);
}
