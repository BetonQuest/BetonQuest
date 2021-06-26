package org.betonquest.betonquest.events;

import lombok.CustomLog;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Removes items from player's inventory and/or backpack
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class TakeEvent extends AbstractTakeEvent {

    protected final Map<UUID, Pair<QuestItem, Integer>> neededDeletions = new ConcurrentHashMap<>();
    protected Item[] questItems;

    public TakeEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        questItems = instruction.getItemList();
    }


    @SuppressWarnings("PMD.PreserveStackTrace")
    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        final UUID uuid = UUID.fromString(playerID);

        for (final Item item : questItems) {
            final QuestItem questItem = item.getItem();
            final int deleteAmount = item.getAmount().getInt(playerID);
            neededDeletions.put(uuid, Pair.of(questItem, deleteAmount));

            checkSelectedTypes(player);
            final String itemName = questItem.getName() == null
                    ? new ItemStack(questItem.getMaterial()).getI18NDisplayName()
                    : questItem.getName();
            notifyPlayer(playerID, itemName, deleteAmount - neededDeletions.get(uuid).getRight());
        }
        return null;
    }


    @Override
    protected ItemStack[] takeDesiredAmount(final Player player, final ItemStack... items) {
        final QuestItem questItem = neededDeletions.get(player.getUniqueId()).getLeft();
        int desiredDeletions = neededDeletions.get(player.getUniqueId()).getRight();

        for (int i = 0; i < items.length && desiredDeletions > 0; i++) {
            final ItemStack item = items[i];
            if (item != null && questItem.compare(item)) {
                if (item.getAmount() <= desiredDeletions) {
                    items[i] = null;
                    desiredDeletions = desiredDeletions - item.getAmount();
                } else {
                    item.setAmount(item.getAmount() - desiredDeletions);
                    desiredDeletions = 0;
                }
            }
        }

        neededDeletions.put(player.getUniqueId(), Pair.of(questItem, desiredDeletions));
        return items;
    }
}
