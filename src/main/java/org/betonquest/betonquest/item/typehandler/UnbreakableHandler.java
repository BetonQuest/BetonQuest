package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.QuestException;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@SuppressWarnings("PMD.CommentRequired")
public class UnbreakableHandler implements ItemMetaHandler<ItemMeta> {

    /**
     * The unbreakable string.
     */
    private static final String UNBREAKABLE = "unbreakable";

    private Existence unbreakable = Existence.WHATEVER;

    public UnbreakableHandler() {
    }

    @Override
    public Class<ItemMeta> metaClass() {
        return ItemMeta.class;
    }

    @Override
    public Set<String> keys() {
        return Set.of(UNBREAKABLE);
    }

    @Override
    @Nullable
    public String serializeToString(final ItemMeta meta) {
        if (meta.isUnbreakable()) {
            return UNBREAKABLE;
        }
        return null;
    }

    @Override
    public void set(final String key, final String data) throws QuestException {
        if (!UNBREAKABLE.equals(key)) {
            throw new QuestException("Unknown unbreakable key: " + key);
        }
        if (UNBREAKABLE.equals(data) || Boolean.parseBoolean(data)) {
            unbreakable = Existence.REQUIRED;
        } else {
            unbreakable = Existence.FORBIDDEN;
        }
    }

    @Override
    public void populate(final ItemMeta meta) {
        meta.setUnbreakable(unbreakable == Existence.REQUIRED);
    }

    @Override
    public boolean check(final ItemMeta meta) {
        return switch (unbreakable) {
            case WHATEVER -> true;
            case REQUIRED -> meta.isUnbreakable();
            case FORBIDDEN -> !meta.isUnbreakable();
        };
    }
}
