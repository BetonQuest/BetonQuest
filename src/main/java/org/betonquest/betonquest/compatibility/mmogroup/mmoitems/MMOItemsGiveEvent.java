package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@SuppressWarnings("PMD.CommentRequired")
public class MMOItemsGiveEvent extends QuestEvent {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    private final MMOItems mmoPlugin = MMOItems.plugin;

    private final Type itemType;

    private final String itemID;

    private boolean scale;

    private boolean notify;

    private boolean singleStack;

    private VariableNumber amountVar = new VariableNumber(1);

    private ItemStack mmoItem;

    public MMOItemsGiveEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());

        itemType = mmoPlugin.getTypes().get(instruction.next());
        itemID = instruction.next();

        while (instruction.hasNext()) {
            final String next = instruction.next();
            switch (next) {
                case "scale" -> this.scale = true;
                case "singleStack" -> this.singleStack = true;
                case "notify" -> this.notify = true;
                default -> this.amountVar = instruction.getVarNum(next);
            }
        }

        mmoItem = mmoPlugin.getItem(itemType, itemID);

        if (mmoItem == null) {
            throw new InstructionParseException("Item with type '" + itemType + "' and ID '" + itemID + "' does not exist.");
        }
    }

    @SuppressWarnings("PMD.PreserveStackTrace")
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        int amount = amountVar.getInt(profile);

        if (scale) {
            mmoItem = mmoPlugin.getItem(itemType, itemID, PlayerData.get(profile.getPlayerUUID()));
        }

        if (notify) {
            try {
                Config.sendNotify(instruction.getPackage().getQuestPath(), profile.getOnlineProfile().get(), "items_given",
                        new String[]{mmoItem.getItemMeta().getDisplayName(), String.valueOf(amount)},
                        "items_given,info");
            } catch (final QuestRuntimeException e) {
                log.warn(instruction.getPackage(), "The notify system was unable to play a sound for the 'items_given' category in '" + getFullId() + "'. Error was: '" + e.getMessage() + "'", e);
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
