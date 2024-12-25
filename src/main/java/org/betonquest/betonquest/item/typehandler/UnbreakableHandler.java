package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.item.QuestItem.Existence;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("PMD.CommentRequired")
public class UnbreakableHandler implements ItemMetaHandler<ItemMeta> {

    /**
     * The unbreakable string.
     */
    private static final String UNBREAKABLE = "unbreakable";

    private Existence unbreakable = Existence.WHATEVER;

    public UnbreakableHandler() {
    }

    /**
     * Converts the item meta into QuestItem format.
     *
     * @param meta the meta to serialize
     * @return parsed values with leading space or empty string
     */
    public static String serializeToString(final ItemMeta meta) {
        if (meta.isUnbreakable()) {
            return " unbreakable";
        }
        return "";
    }

    @Override
    public void set(final String key, final String data) {
        if (UNBREAKABLE.equals(data)) {
            unbreakable = Existence.REQUIRED;
        } else {
            set(data);
        }
    }

    @Override
    public void populate(final ItemMeta meta) {
        meta.setUnbreakable(isUnbreakable());
    }

    @Override
    public boolean check(final ItemMeta meta) {
        return check(meta.isUnbreakable());
    }

    public void set(final String string) {
        if (Boolean.parseBoolean(string)) {
            unbreakable = Existence.REQUIRED;
        } else {
            unbreakable = Existence.FORBIDDEN;
        }
    }

    public boolean isUnbreakable() {
        return unbreakable == Existence.REQUIRED;
    }

    public boolean check(final boolean bool) {
        return switch (unbreakable) {
            case WHATEVER -> true;
            case REQUIRED -> bool;
            case FORBIDDEN -> !bool;
        };
    }
}
