package org.betonquest.betonquest.item.typehandler;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.text.TextParser;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Handles de-/serialization of Display Names.
 */
public class NameHandler implements ItemMetaHandler<ItemMeta> {

    /**
     * The text parser used to parse text.
     */
    protected final TextParser textParser;

    /**
     * The Item Display Name.
     */
    @Nullable
    private Component name;

    /**
     * The required existence.
     */
    private Existence existence = Existence.WHATEVER;

    /**
     * Creates an empty NameHandler.
     *
     * @param textParser the text parser used to parse text
     */
    public NameHandler(final TextParser textParser) {
        this.textParser = textParser;
    }

    @Override
    public Class<ItemMeta> metaClass() {
        return ItemMeta.class;
    }

    @Override
    public Set<String> keys() {
        return Set.of("name");
    }

    @Override
    @Nullable
    public String serializeToString(final ItemMeta meta) {
        if (meta.hasDisplayName()) {
            return HandlerUtil.toKeyValue("name", meta.displayName());
        }
        return null;
    }

    @Override
    public void set(final String key, final String data) throws QuestException {
        if (!"name".equals(key)) {
            throw new QuestException("Invalid name: " + key);
        }
        if (data.isEmpty()) {
            throw new QuestException("Name cannot be empty");
        }
        if (Existence.NONE_KEY.equalsIgnoreCase(data)) {
            existence = Existence.FORBIDDEN;
        } else {
            this.name = textParser.parse(data).compact();
            existence = Existence.REQUIRED;
        }
    }

    @Override
    public void populate(final ItemMeta meta) {
        meta.displayName(get());
    }

    @Override
    public boolean check(final ItemMeta meta) {
        final Component displayName = meta.hasDisplayName() ? meta.displayName() : null;
        return switch (existence) {
            case WHATEVER -> true;
            case REQUIRED -> displayName != null && displayName.compact().equals(this.name);
            case FORBIDDEN -> displayName == null;
        };
    }

    /**
     * Get the name.
     *
     * @return the name
     */
    @Nullable
    public Component get() {
        return name;
    }
}
