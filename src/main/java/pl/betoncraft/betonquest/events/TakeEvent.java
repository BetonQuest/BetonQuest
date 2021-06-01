package pl.betoncraft.betonquest.events;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.Instruction.Item;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.item.QuestItem;
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
public class TakeEvent extends QuestEvent {

    private final Item[] questItems;
    private final List<CheckType> checkOrder = new ArrayList<>();
    private final boolean notify;

    private final Map<UUID, Pair<QuestItem, Integer>> neededDeletions = new ConcurrentHashMap<>();

    public TakeEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        questItems = instruction.getItemList();
        notify = instruction.hasArgument("notify");

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
    }

    @SuppressWarnings("PMD.PreserveStackTrace")
    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        final UUID uuid = UUID.fromString(playerID);

        for (final Item item : questItems) {
            final QuestItem questItem = item.getItem();
            final int deleteAmount = item.getAmount().getInt(playerID);
            neededDeletions.put(uuid, Pair.of(questItem, deleteAmount));

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
            notifyPlayer(playerID, questItem, deleteAmount);
        }
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


    private void notifyPlayer(final String playerID, final QuestItem questItem,final  int amount) {
        if (notify) {
            try {
                Config.sendNotify(instruction.getPackage().getName(), playerID, "items_taken",
                        new String[]{
                                questItem.getName() == null ? questItem.getMaterial().toString().toLowerCase(Locale.ROOT).replace("_", " ") : questItem.getName(),
                                String.valueOf(amount)},
                        "items_taken,info");
            } catch (final QuestRuntimeException exception) {
                LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'items_taken' category in '" + getFullId() + "'. Error was: '" + exception.getMessage() + "'");
                LogUtils.logThrowable(exception);
            }
        }
    }

    private ItemStack[] removeDesiredAmount(final Player player, final List<ItemStack> items) {
        final QuestItem questItem = neededDeletions.get(player.getUniqueId()).getLeft();
        int desiredDeletions = neededDeletions.get(player.getUniqueId()).getRight();

        int index = 0;
        while (index < items.size()) {
            final ItemStack item = items.get(index);
            if (item != null && questItem.compare(item)) {
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
        neededDeletions.put(player.getUniqueId(), Pair.of(questItem, desiredDeletions));
        return items.toArray(new ItemStack[0]);
    }

    private enum CheckType {
        INVENTORY,
        ARMOR,
        BACKPACK
    }
}
