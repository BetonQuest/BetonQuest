package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles metadata about item flags.
 */
public class FlagHandler implements ItemMetaHandler<ItemMeta> {
    /**
     * Set of ItemFlags on the ItemStack.
     */
    private Set<ItemFlag> itemFlags;

    /**
     * Existence of the flags.
     */
    private Existence existence = Existence.WHATEVER;

    /**
     * Construct a new FlagHandler.
     */
    public FlagHandler() {
        itemFlags = Set.of();
    }

    @Override
    public Class<ItemMeta> metaClass() {
        return ItemMeta.class;
    }

    @Override
    public Set<String> keys() {
        return Set.of("flags");
    }

    @Override
    @Nullable
    public String serializeToString(final ItemMeta meta) {
        if (meta.getItemFlags().isEmpty()) {
            return null;
        }
        return "flags:" + String.join(",", meta.getItemFlags().stream().map(ItemFlag::name).sorted().toList());
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
            this.existence = Existence.FORBIDDEN;
        } else {
            this.itemFlags = Set.copyOf(itemFlags);
            this.existence = Existence.REQUIRED;
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

    @Override
    public void set(final String key, final String data) throws InstructionParseException {
        if (!"flags".equals(key)) {
            throw new InstructionParseException("Invalid flag key: " + key);
        }
        parse(data);
    }

    @Override
    public void populate(final ItemMeta meta) {
        get().forEach(meta::addItemFlags);
    }

    @Override
    public boolean check(final ItemMeta data) {
        return existence == Existence.WHATEVER
                || existence == Existence.FORBIDDEN && data.getItemFlags().isEmpty()
                || existence == Existence.REQUIRED && !data.getItemFlags().isEmpty() && itemFlags.equals(data.getItemFlags());
    }
}
