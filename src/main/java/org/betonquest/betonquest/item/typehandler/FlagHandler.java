package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles metadata about item flags.
 */
public class FlagHandler {
    /**
     * Set of ItemFlags on the ItemStack.
     */
    private Set<ItemFlag> itemFlags;

    /**
     * Existence of the flags.
     */
    private QuestItem.Existence existence = QuestItem.Existence.WHATEVER;

    /**
     * Construct a new FlagHandler.
     */
    public FlagHandler() {
        itemFlags = Set.of();
    }

    /**
     * Parse a String into the Set of ItemFlags.
     *
     * @param data The Set of ItemFlags.
     * @throws InstructionParseException If there is an error parsing.
     */
    public void parse(final String data) throws InstructionParseException {
        set(Arrays.stream(data.split(",")).map(ItemFlag::valueOf).collect(Collectors.toSet()));
    }

    /**
     * Set the Set of ItemFlags in this handler.
     *
     * @param itemFlags The ItemFlags, or null if not set.
     * @throws InstructionParseException If there is an error setting the flags.
     */
    public void set(@Nullable final Set<ItemFlag> itemFlags) throws InstructionParseException {
        if (itemFlags == null || itemFlags.isEmpty()) {
            this.itemFlags = Set.of();
            this.existence = QuestItem.Existence.FORBIDDEN;
        } else {
            this.itemFlags = Set.copyOf(itemFlags);
            this.existence = QuestItem.Existence.REQUIRED;
        }
    }

    /**
     * Get the Set of ItemFlags.
     *
     * @return The Set of ItemFlags.
     */
    public Set<ItemFlag> get() {
        return itemFlags;
    }

    /**
     * Check to see if the specified ItemMeta matches this FlagHandler.
     *
     * @param data The ItemMeta to check.
     * @return True if this metadata is required or matches, false otherwise.
     */
    public boolean check(final ItemMeta data) {
        return existence == QuestItem.Existence.WHATEVER
                || existence == QuestItem.Existence.FORBIDDEN && data.getItemFlags().isEmpty()
                || existence == QuestItem.Existence.REQUIRED && !data.getItemFlags().isEmpty() && itemFlags.equals(data.getItemFlags());
    }

}
