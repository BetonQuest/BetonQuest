package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.QuestException;
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

    @Override
    public void set(final String key, final String data) throws QuestException {
        if (!"flags".equals(key)) {
            throw new QuestException("Invalid flag key: " + key);
        }
        final Set<ItemFlag> flags = Arrays.stream(data.split(",")).map(ItemFlag::valueOf).collect(Collectors.toSet());
        if (flags.isEmpty()) {
            this.itemFlags = Set.of();
            this.existence = Existence.FORBIDDEN;
        } else {
            this.itemFlags = Set.copyOf(flags);
            this.existence = Existence.REQUIRED;
        }
    }

    @Override
    public void populate(final ItemMeta meta) {
        itemFlags.forEach(meta::addItemFlags);
    }

    @Override
    public boolean check(final ItemMeta data) {
        return existence == Existence.WHATEVER
                || existence == Existence.FORBIDDEN && data.getItemFlags().isEmpty()
                || existence == Existence.REQUIRED && !data.getItemFlags().isEmpty() && itemFlags.equals(data.getItemFlags());
    }
}
