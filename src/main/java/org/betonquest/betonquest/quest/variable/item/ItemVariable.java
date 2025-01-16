package org.betonquest.betonquest.quest.variable.item;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariable;
import org.betonquest.betonquest.exceptions.QuestException;
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
     * The QuestItem.
     */
    private final QuestItem questItem;

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
     * @param questItem the QuestItem
     * @param type      the type how the item should be displayed
     * @param raw       if the output should be raw
     * @param amount    the amount of the item
     */
    public ItemVariable(final QuestItem questItem, final ItemDisplayType type, final boolean raw, final int amount) {
        this.questItem = questItem;
        this.type = type;
        this.raw = raw;
        this.amount = amount;
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "NullAway"})
    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        if (profile == null && (type == ItemDisplayType.AMOUNT || type == ItemDisplayType.LEFT)) {
            throw new QuestException("ItemVariable with type " + type + " can't be used without a profile.");
        }
        return switch (type) {
            case AMOUNT -> Integer.toString(itemAmount(profile));
            case LEFT -> Integer.toString(amount - itemAmount(profile));
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

    private int itemAmount(final Profile profile) {
        final OnlineProfile onlineProfile = profile.getOnlineProfile().get();
        final Player player = onlineProfile.getPlayer();
        int itemAmount = 0;
        for (final ItemStack item : player.getInventory().getContents()) {
            if (item == null || !questItem.compare(item)) {
                continue;
            }
            itemAmount += item.getAmount();
        }
        final List<ItemStack> backpackItems = BetonQuest.getInstance().getPlayerDataStorage()
                .get(onlineProfile).getBackpack();
        for (final ItemStack item : backpackItems) {
            if (item == null || !questItem.compare(item)) {
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
