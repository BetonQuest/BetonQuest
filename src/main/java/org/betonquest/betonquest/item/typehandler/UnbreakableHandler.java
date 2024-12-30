package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem.Existence;
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
    public void set(final String key, final String data) throws InstructionParseException {
        if (!UNBREAKABLE.equals(key)) {
            throw new InstructionParseException("Unknown unbreakable key: " + key);
        }
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
