package org.betonquest.betonquest.mc_1_20_6.item;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.item.typehandler.Existence;
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
    @Nullable
    private Component itemName;

    /**
     * The required item name existence.
     */
    private Existence itemNameE = Existence.WHATEVER;

    /**
     * Creates an empty NameHandler with also an 'itemName'.
     *
     * @param textParser the text parser used to parse text
     */
    public UpdatedNameHandler(final TextParser textParser) {
        super(textParser);
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
    public void set(final String key, final String data) throws QuestException {
        if (data.isEmpty()) {
            throw new QuestException("Item-/Name cannot be empty");
        }
        if (ITEM_NAME.equals(key)) {
            if (Existence.NONE_KEY.equalsIgnoreCase(data)) {
                itemNameE = Existence.FORBIDDEN;
            } else {
                this.itemName = textParser.parse(data);
                itemNameE = Existence.REQUIRED;
            }
        } else {
            super.set(key, data);
        }
    }

    @Override
    public void populate(final ItemMeta meta) {
        super.populate(meta);
        meta.itemName(itemName);
    }

    @Override
    public boolean check(final ItemMeta meta) {
        if (!super.check(meta)) {
            return false;
        }
        final Component itemName = meta.hasItemName() ? meta.itemName() : null;
        return switch (itemNameE) {
            case WHATEVER -> true;
            case REQUIRED -> itemName != null && itemName.equals(this.itemName);
            case FORBIDDEN -> itemName == null;
        };
    }

    @Override
    @Nullable
    public Component get() {
        final Component display = super.get();
        return display == null ? itemName : display;
    }
}
