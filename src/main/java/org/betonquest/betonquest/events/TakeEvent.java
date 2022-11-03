package org.betonquest.betonquest.events;

import lombok.CustomLog;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.item.QuestItem;
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
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        final UUID uuid = player.getUniqueId();

        for (final Item item : questItems) {
            final QuestItem questItem = item.getItem();
            final int deleteAmount = item.getAmount().getInt(profile);
            neededDeletions.put(uuid, Pair.of(questItem, deleteAmount));

            checkSelectedTypes(player);
            final String itemName = questItem.getName() == null
                    ? new ItemStack(questItem.getMaterial()).getItemMeta().getDisplayName()
                    : questItem.getName();
            notifyPlayer(profile.getOnlineProfile().get(), itemName, deleteAmount - neededDeletions.get(uuid).getRight());
        }
        return null;
    }


    @Override
    protected ItemStack[] takeDesiredAmount(final Profile profile, final ItemStack... items) {
        final QuestItem questItem = neededDeletions.get(profile.getPlayer().getUniqueId()).getLeft();
        int desiredDeletions = neededDeletions.get(profile.getPlayer().getUniqueId()).getRight();

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

        neededDeletions.put(profile.getPlayer().getUniqueId(), Pair.of(questItem, desiredDeletions));
        return items;
    }
}
