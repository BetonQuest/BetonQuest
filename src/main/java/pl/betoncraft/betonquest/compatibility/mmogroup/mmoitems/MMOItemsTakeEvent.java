package pl.betoncraft.betonquest.compatibility.mmogroup.mmoitems;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Removes items from player's inventory and/or backpack
 */
@SuppressWarnings("PMD.CommentRequired")
public class MMOItemsTakeEvent extends QuestEvent {

    private final Type itemType;
    private final String itemID;

    private VariableNumber deleteAmountVar = new VariableNumber(1);
    private final List<CheckType> checkOrder = new ArrayList<>();
    private final boolean notify;

    private final Map<UUID, Integer> neededDeletions = new ConcurrentHashMap<>();

    public MMOItemsTakeEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        itemType = MMOItems.plugin.getTypes().get(instruction.next());
        itemID = instruction.next();

        final String amount = instruction.getOptional("amount");
        if (amount != null) {
            deleteAmountVar = instruction.getVarNum(amount);
        }

        final String order = instruction.getOptional("invOrder");
        if (order == null) {
            checkOrder.add(CheckType.INVENTORY);
            checkOrder.add(CheckType.ARMOR);
            checkOrder.add(CheckType.BACKPACK);
        } else {
            final String[] enumNames = order.split(",");
            for (final String s : enumNames) {
                try {
                    checkOrder.add(Enum.valueOf(CheckType.class, s.toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException e) {
                    throw new InstructionParseException("There is no such check type: " + s, e);
                }
            }
        }

        notify = instruction.hasArgument("notify");
    }

    @SuppressWarnings("PMD.PreserveStackTrace")
    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        final UUID uuid = UUID.fromString(playerID);

        final int deleteAmount = deleteAmountVar.getInt(playerID);
        neededDeletions.put(uuid, deleteAmount);

        for (final CheckType type : checkOrder) {
            switch (type) {
                case INVENTORY:
                    checkInventory(player); break;
                case ARMOR:
                    checkArmor(player); break;
                case BACKPACK:
                    checkBackpack(playerID); break;
            }
        }
        notifyPlayer(playerID, deleteAmount);
        return null;
    }

    private void checkInventory(final Player player) {
        final List<ItemStack> inv = new ArrayList<>(Arrays.asList(player.getInventory().getContents()));
        final ItemStack[] newInv = removeDesiredAmount(player, inv);
        player.getInventory().setContents(newInv);
    }

    private void checkArmor(final Player player) {
        final List<ItemStack> armor = Arrays.asList(player.getInventory().getArmorContents());
        final ItemStack[] newArmor = removeDesiredAmount(player, armor);
        player.getInventory().setArmorContents(newArmor);
    }

    private void checkBackpack(final String playerID) {
        final List<ItemStack> backpack = BetonQuest.getInstance().getPlayerData(playerID).getBackpack();
        final ItemStack[] newBackpack = removeDesiredAmount(PlayerConverter.getPlayer(playerID), backpack);
        BetonQuest.getInstance().getPlayerData(playerID).setBackpack(Arrays.asList(newBackpack));
    }


    private void notifyPlayer(final String playerID,final int amount) {
        if (notify) {
            try {
                Config.sendNotify(instruction.getPackage().getName(), playerID, "items_taken",
                        new String[]{itemID, String.valueOf(amount)}, "items_taken,info");
            } catch (final QuestRuntimeException exception) {
                LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'items_taken' category in '" + getFullId() + "'. Error was: '" + exception.getMessage() + "'");
                LogUtils.logThrowable(exception);
            }
        }
    }

    private ItemStack[] removeDesiredAmount(final Player player, final List<ItemStack> items) {
        int desiredDeletions = neededDeletions.get(player.getUniqueId());

        int index = 0;
        while (index < items.size()) {
            final ItemStack item = items.get(index);
            if (MMOItemsUtils.equalsMMOItem(item, itemType, itemID)) {
                if (item.getAmount() - desiredDeletions <= 0) {
                    desiredDeletions = desiredDeletions - item.getAmount();
                    items.remove(index);
                    if (desiredDeletions == 0) {
                        break;
                    }
                } else {
                    item.setAmount(item.getAmount() - desiredDeletions);
                    desiredDeletions = 0;
                    break;
                }
            } else {
                index++;
            }
        }
        neededDeletions.put(player.getUniqueId(), desiredDeletions);
        return items.toArray(new ItemStack[0]);
    }

    private enum CheckType {
        INVENTORY,
        ARMOR,
        BACKPACK
    }
}
