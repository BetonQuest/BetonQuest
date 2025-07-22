package org.betonquest.betonquest.quest.variable.item;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariable;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Allows you to display properties of QuestItems like the name
 * or the amount in player's inventory.
 */
public class ItemVariable implements NullableVariable {

    /**
     * The Item.
     */
    private final Variable<Item> item;

    /**
     * The type how the item should be displayed.
     */
    private final ItemDisplayType type;

    /**
     * If the output should be raw.
     */
    private final boolean raw;

    /**
     * The amount of the item.
     */
    private final int amount;

    /**
     * Creates a new ItemVariable.
     *
     * @param item   the QuestItem
     * @param type   the type how the item should be displayed
     * @param raw    if the output should be raw
     * @param amount the amount of the item
     */
    public ItemVariable(final Variable<Item> item, final ItemDisplayType type, final boolean raw, final int amount) {
        this.item = item;
        this.type = type;
        this.raw = raw;
        this.amount = amount;
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        final QuestItem questItem = this.item.getValue(profile).getItem(profile);
        return switch (type) {
            case AMOUNT -> Integer.toString(itemAmount(questItem, profile));
            case LEFT -> Integer.toString(amount - itemAmount(questItem, profile));
            case NAME -> conditionalRaw(questItem.getName());
            case LORE -> {
                try {
                    yield conditionalRaw(questItem.getLore().get(amount));
                } catch (final IndexOutOfBoundsException e) {
                    yield "";
                }
            }
        };
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private int itemAmount(final QuestItem questItem, @Nullable final Profile profile) throws QuestException {
        if (profile == null || profile.getOnlineProfile().isEmpty()) {
            throw new QuestException("ItemVariable with type " + type + " can't be used without an online profile.");
        }
        final OnlineProfile onlineProfile = profile.getOnlineProfile().get();
        final Player player = onlineProfile.getPlayer();
        int itemAmount = 0;
        for (final ItemStack item : player.getInventory().getContents()) {
            if (item == null || !questItem.matches(item)) {
                continue;
            }
            itemAmount += item.getAmount();
        }
        final List<ItemStack> backpackItems = BetonQuest.getInstance().getPlayerDataStorage()
                .get(onlineProfile).getBackpack();
        for (final ItemStack item : backpackItems) {
            if (item == null || !questItem.matches(item)) {
                continue;
            }
            itemAmount += item.getAmount();
        }
        return itemAmount;
    }

    private String conditionalRaw(@Nullable final String string) {
        if (string == null) {
            return "";
        }
        if (raw) {
            return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', string));
        }
        return string;
    }
}
