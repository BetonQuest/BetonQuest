package pl.betoncraft.betonquest.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.Instruction.Item;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * Removes items from player's inventory and/or backpack
 */
@SuppressWarnings("PMD.CommentRequired")
public class TakeEvent extends QuestEvent {

    private final Item[] questItems;
    private final boolean notify;

    private int counter;

    public TakeEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        questItems = instruction.getItemList();
        notify = instruction.hasArgument("notify");
    }

    @SuppressWarnings("PMD.PreserveStackTrace")
    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        for (final Item item : questItems) {
            final QuestItem questItem = item.getItem();
            final VariableNumber amount = item.getAmount();

            // cache the amount
            counter = amount.getInt(playerID);

            // notify the player
            if (notify) {
                try {
                    Config.sendNotify(instruction.getPackage().getName(), playerID, "items_taken",
                            new String[]{
                                    questItem.getName() == null ? questItem.getMaterial().toString().toLowerCase().replace("_", " ") : questItem.getName(),
                                    String.valueOf(counter)},
                            "items_taken,info");
                } catch (final QuestRuntimeException exception) {
                    LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'items_taken' category in '" + getFullId() + "'. Error was: '" + exception.getMessage() + "'");
                    LogUtils.logThrowable(exception);
                }
            }

            // Remove Quest items from player's inventory
            player.getInventory().setContents(removeItems(player.getInventory().getContents(), questItem));

            // Remove Quest items from player's armor slots
            if (counter > 0) {
                player.getInventory()
                        .setArmorContents(removeItems(player.getInventory().getArmorContents(), questItem));
            }

            // Remove Quest items from player's backpack
            if (counter > 0) {
                final List<ItemStack> backpack = BetonQuest.getInstance().getPlayerData(playerID).getBackpack();
                ItemStack[] array = {};
                array = backpack.toArray(array);
                final LinkedList<ItemStack> list = new LinkedList<>(Arrays.asList(removeItems(array, questItem)));
                list.removeAll(Collections.singleton(null));
                BetonQuest.getInstance().getPlayerData(playerID).setBackpack(list);
            }
        }
        return null;
    }

    private ItemStack[] removeItems(final ItemStack[] items, final QuestItem questItem) {
        for (int i = 0; i < items.length; i++) {
            final ItemStack item = items[i];
            if (questItem.compare(item)) {
                if (item.getAmount() - counter <= 0) {
                    counter -= item.getAmount();
                    items[i] = null;
                } else {
                    item.setAmount(item.getAmount() - counter);
                    counter = 0;
                }
                if (counter <= 0) {
                    break;
                }
            }
        }
        return items;
    }
}
