package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import lombok.CustomLog;
import net.Indyuce.mmocore.api.block.BlockType;
import net.Indyuce.mmocore.api.block.SkullBlockType;
import net.Indyuce.mmocore.api.block.VanillaBlockType;
import net.Indyuce.mmocore.api.event.CustomBlockMineEvent;
import net.Indyuce.mmoitems.comp.mmocore.load.MMOItemsBlockType;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.LogUtils;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.logging.Level;

@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class MMOCoreBreakCustomBlockObjective extends Objective implements Listener {

    private final String desiredBlockId;
    private final int neededAmount;

    private final boolean notify;
    private final int notifyInterval;

    public MMOCoreBreakCustomBlockObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = MMOBlockData.class;

        desiredBlockId = instruction.getOptional("block");
        neededAmount = instruction.getInt();

        notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        notify = instruction.hasArgument("notify") || notifyInterval > 1;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(final CustomBlockMineEvent event) {
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(playerID) || !checkConditions(playerID)) {
            return;
        }
        if (!event.canBreak()) {
            return;
        }
        final String blockId = getBlockId(event.getBlockInfo().getBlock());
        if (!blockId.equals(desiredBlockId)) {
            return;
        }

        final MMOBlockData playerData = (MMOBlockData) dataMap.get(playerID);
        playerData.addPlacedBlock();

        if (playerData.getPlacedBlocks() == neededAmount) {
            completeObjective(playerID);
        } else if (notify && playerData.getPlacedBlocks() % notifyInterval == 0) {
            handleNotifications(playerID, playerData);
        }

    }

    private void handleNotifications(final String playerID, final MMOBlockData playerData) {
        try {
            Config.sendNotify(instruction.getPackage().getName(), playerID, "blocks_to_break",
                    new String[]{String.valueOf(neededAmount - playerData.getPlacedBlocks())},
                    "blocks_to_break,info");
        } catch (final QuestRuntimeException exception) {
            try {
                LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to send a notification for the 'blocks_to_break' category in '" + instruction.getObjective().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                LogUtils.logThrowableIgnore(exception);
            } catch (final InstructionParseException e) {
                LOG.reportException(e);
            }
        }
    }

    private String getBlockId(final BlockType blockType) {
        String actualBlockId = null;

        if (blockType instanceof VanillaBlockType) {
            final VanillaBlockType vanillaBlock = (VanillaBlockType) blockType;
            actualBlockId = vanillaBlock.getType().toString();

        } else if (blockType instanceof MMOItemsBlockType) {
            final MMOItemsBlockType mmoItemsBlock = (MMOItemsBlockType) blockType;
            actualBlockId = String.valueOf(mmoItemsBlock.getBlockId());

        } else if (blockType instanceof SkullBlockType) {
            final SkullBlockType skullBlock = (SkullBlockType) blockType;
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

    @Override
    public String getDefaultDataInstruction() {
        return "0";
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        if ("left".equalsIgnoreCase(name)) {
            return Integer.toString(neededAmount - ((MMOBlockData) dataMap.get(playerID)).getPlacedBlocks());
        } else if ("amount".equalsIgnoreCase(name)) {
            return Integer.toString(((MMOBlockData) dataMap.get(playerID)).getPlacedBlocks());
        }
        return "";
    }

    public static class MMOBlockData extends ObjectiveData {

        private int amount;

        public MMOBlockData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        private void addPlacedBlock() {
            amount++;
            update();
        }

        private int getPlacedBlocks() {
            return amount;
        }

        @Override
        public String toString() {
            return String.valueOf(amount);
        }
    }
}
