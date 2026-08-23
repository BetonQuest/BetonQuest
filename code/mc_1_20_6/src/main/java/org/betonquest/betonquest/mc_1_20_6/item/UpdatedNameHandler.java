package org.betonquest.betonquest.mc_1_20_6.item;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.typehandler.Existence;
import org.betonquest.betonquest.item.typehandler.ExistenceArgument;
import org.betonquest.betonquest.item.typehandler.HandlerUtil;
import org.betonquest.betonquest.item.typehandler.NameHandler;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Handles de-/serialization of (Display) Names.
 */
public class UpdatedNameHandler extends NameHandler {

    /**
     * The 'item-name' string.
     */
    private static final String ITEM_NAME = "item-name";

    /**
     * The Item Name.
     */
    private ExistenceArgument<@Nullable Component> itemName = ExistenceArgument.whateverNullValue();

    /**
     * Creates an empty NameHandler with also an 'itemName'.
     */
    public UpdatedNameHandler() {
        super();
    }

    @Override
    public Set<String> keys() {
        return Set.of("name", ITEM_NAME);
    }

    @Override
    @Nullable
    public String serializeToString(final ItemMeta meta) {
        final String name = super.serializeToString(meta);
        if (meta.hasItemName()) {
            return (name == null ? "" : name + " ") + HandlerUtil.toKeyValue("item-name", meta.itemName());
        }
        return name;
    }

    @Override
    public void set(final Instruction instruction) throws QuestException {
        // TODO is empty check?
        this.itemName = ExistenceArgument.apply(ITEM_NAME, instruction.component().map(Component::compact));
        super.set(instruction);
    }

    @Override
    public ResolvedName resolve(final @Nullable Profile profile) throws QuestException {
        final ResolvedName displayName = super.resolve(profile);
        return new UpdatedResolved(displayName, itemName.getValue(profile));
    }

    /**
     * The resolved handler.
     *
     * @param itemName Item Display Name's required existence and value.
     */
    public record UpdatedResolved(ResolvedName displayName,
                                  Pair<Existence, @Nullable Component> itemName) implements ResolvedName {

        @Override
        public void populate(final ItemMeta meta) {
            displayName.populate(meta);
            meta.itemName(itemName.getRight());
        }

        @Override
        public boolean check(final ItemMeta meta) {
            if (!displayName.check(meta)) {
                return false;
            }
            final Component itemName = meta.hasItemName() ? meta.itemName() : null;
            return switch (this.itemName.getLeft()) {
                case WHATEVER -> true;
                case REQUIRED -> itemName != null && itemName.compact().equals(this.itemName.getRight());
                case FORBIDDEN -> itemName == null;
            };
        }

        @Override
        @Nullable
        public Component get() {
            final Component display = displayName.get();
            return display == null ? itemName.getRight() : display;
        }
    }
}
