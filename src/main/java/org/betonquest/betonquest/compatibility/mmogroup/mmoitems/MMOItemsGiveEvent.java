package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.LogUtils;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.logging.Level;

@SuppressWarnings("PMD.CommentRequired")
public class MMOItemsGiveEvent extends QuestEvent {

    private final MMOItems mmoPlugin = MMOItems.plugin;

    private final Type itemType;
    private final String itemID;
    private VariableNumber amountVar = new VariableNumber(1);
    private final boolean scale;
    private final boolean notify;
    private final boolean singleStack;

    public MMOItemsGiveEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        itemType = mmoPlugin.getTypes().get(instruction.next());
        itemID = instruction.next();

        if (instruction.getInstruction().contains("%") || !instruction.getAllNumbers().isEmpty()) {
            amountVar = instruction.getVarNum();
        }

        scale = instruction.hasArgument("scale");
        singleStack = instruction.hasArgument("singleStack");
        notify = instruction.hasArgument("notify");
    }

    @SuppressWarnings("PMD.PreserveStackTrace")
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        int amount = amountVar.getInt(playerID);

        final ItemStack mmoItem;
        if (scale) {
            mmoItem = mmoPlugin.getItem(itemType, itemID, PlayerData.get(player.getUniqueId()));
        } else {
            mmoItem = mmoPlugin.getItem(itemType, itemID);
        }

        if (notify) {
            try {
                Config.sendNotify(instruction.getPackage().getName(), playerID, "items_given",
                        new String[]{mmoItem.getItemMeta().getDisplayName(), String.valueOf(amount)},
                        "items_given,info");
            } catch (final QuestRuntimeException exception) {
                LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'items_given' category in '" + getFullId() + "'. Error was: '" + exception.getMessage() + "'");
                LogUtils.logThrowable(exception);
            }
        }

        while (amount > 0) {

            final int stackSize;
            if (singleStack) {
                stackSize = Math.min(amount, 64);
            } else {
                stackSize = 1;
            }

            mmoItem.setAmount(stackSize);
            final HashMap<Integer, ItemStack> left = player.getInventory().addItem(mmoItem);
            for (final ItemStack itemStack : left.values()) {
                player.getWorld().dropItem(player.getLocation(), itemStack);
            }
            amount -= stackSize;
        }
        return null;
    }
}
