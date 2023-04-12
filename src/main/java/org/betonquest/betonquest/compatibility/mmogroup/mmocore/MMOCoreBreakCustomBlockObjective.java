package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.block.BlockType;
import net.Indyuce.mmocore.api.block.SkullBlockType;
import net.Indyuce.mmocore.api.block.VanillaBlockType;
import net.Indyuce.mmocore.api.event.CustomBlockMineEvent;
import net.Indyuce.mmoitems.comp.mmocore.load.MMOItemsBlockType;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreBreakCustomBlockObjective extends CountingObjective implements Listener {

    private final String desiredBlockId;

    public MMOCoreBreakCustomBlockObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "blocks_to_break");
        desiredBlockId = instruction.getOptional("block");
        targetAmount = instruction.getInt();
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(final CustomBlockMineEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            final String blockId = getBlockId(event.getBlockInfo().getBlock());
            if (blockId.equals(desiredBlockId)) {
                getCountingData(onlineProfile).progress();
                completeIfDoneOrNotify(onlineProfile);
            }
        }
    }

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

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }
}
