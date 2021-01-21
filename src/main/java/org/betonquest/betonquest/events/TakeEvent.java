package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.LogUtils;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
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
                                    questItem.getName() == null ? questItem.getMaterial().toString().toLowerCase(Locale.ROOT).replace("_", " ") : questItem.getName(),
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
