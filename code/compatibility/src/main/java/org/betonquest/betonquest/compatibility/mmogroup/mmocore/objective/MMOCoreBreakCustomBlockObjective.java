package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import net.Indyuce.mmocore.api.block.BlockType;
import net.Indyuce.mmocore.api.block.SkullBlockType;
import net.Indyuce.mmocore.api.block.VanillaBlockType;
import net.Indyuce.mmocore.api.event.CustomBlockMineEvent;
import net.Indyuce.mmoitems.comp.mmocore.load.MMOItemsBlockType;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

/**
 * An objective that listens for the player breaking a custom block.
 */
public class MMOCoreBreakCustomBlockObjective extends CountingObjective implements Listener {

    /**
     * The ID of the block to be broken.
     */
    private final Variable<String> desiredBlockId;

    /**
     * Constructor for the MMOCoreBreakCustomBlockObjective.
     *
     * @param instruction    the instruction object representing the objective
     * @param targetAmount   the target amount of blocks to break
     * @param desiredBlockId the ID of the block to be broken
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public MMOCoreBreakCustomBlockObjective(final Instruction instruction, final Variable<Number> targetAmount, final Variable<String> desiredBlockId) throws QuestException {
        super(instruction, targetAmount, "blocks_to_break");
        this.desiredBlockId = desiredBlockId;
    }

    /**
     * Listens for the player breaking a custom block.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(final CustomBlockMineEvent event) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            final String blockId = getBlockId(event.getBlockInfo().getBlock());
            qeHandler.handle(() -> {
                if (desiredBlockId.getValue(onlineProfile).equals(blockId)) {
                    getCountingData(onlineProfile).progress();
                    completeIfDoneOrNotify(onlineProfile);
                }
            });
        }
    }

    @Nullable
    private String getBlockId(final BlockType blockType) {
        String actualBlockId = null;

        if (blockType instanceof final VanillaBlockType vanillaBlock) {
            actualBlockId = vanillaBlock.getType().toString();
        } else if (blockType instanceof final MMOItemsBlockType mmoItemsBlock) {
            actualBlockId = String.valueOf(mmoItemsBlock.getBlockId());
        } else if (blockType instanceof final SkullBlockType skullBlock) {
            actualBlockId = skullBlock.getValue();
        }
        return actualBlockId;
    }
}
