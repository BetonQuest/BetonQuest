package org.betonquest.betonquest.item.typehandler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.text.TextParser;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Handles de-/serialization of Item Lore.
 */
public class LoreHandler implements ItemMetaHandler<ItemMeta> {

    /**
     * The text parser used to parse text.
     */
    private final TextParser textParser;

    /**
     * The lore.
     */
    private final List<Component> lore = new LinkedList<>();

    /**
     * The required existence.
     */
    private Existence existence = Existence.WHATEVER;

    /**
     * If the lore need to be exact the same or just contain all specified lines.
     */
    private boolean exact = true;

    /**
     * Creates an empty LoreHandler.
     *
     * @param textParser the text parser used to parse text
     */
    public LoreHandler(final TextParser textParser) {
        this.textParser = textParser;
    }

    @Override
    public Class<ItemMeta> metaClass() {
        return ItemMeta.class;
    }

    @Override
    public Set<String> keys() {
        return Set.of("lore", "lore-containing");
    }

    @Override
    @Nullable
    public String serializeToString(final ItemMeta meta) {
        if (meta.hasLore()) {
            final StringBuilder string = new StringBuilder();
            for (final Component line : meta.lore()) {
                string.append(MiniMessage.miniMessage().serialize(line)).append(';');
            }
            return "\"lore:@[minimessage]" + string.substring(0, string.length() - 1) + "\"";
        }
        return null;
    }

    @SuppressWarnings("PMD.AssignmentInOperand")
    @Override
    public void set(final String key, final String data) throws QuestException {
        switch (key) {
            case "lore" -> {
                if (Existence.NONE_KEY.equalsIgnoreCase(data)) {
                    existence = Existence.FORBIDDEN;
                } else {
                    existence = Existence.REQUIRED;
                    for (final String line : data.split(";")) {
                        this.lore.add(textParser.parse(line));
                    }
                }
            }
            case "lore-containing" -> exact = false;
            default -> throw new QuestException("Unknown lore key: " + key);
        }
    }

    @Override
    public void populate(final ItemMeta meta) {
        meta.lore(get());
    }

    @Override
    public boolean check(final ItemMeta meta) {
        final List<Component> lore = meta.lore();
        return switch (existence) {
            case WHATEVER -> true;
            case REQUIRED -> checkRequired(lore);
            case FORBIDDEN -> lore == null || lore.isEmpty();
        };
    }

    /**
     * Gets the lore.
     *
     * @return the list of lore lines, can be empty
     */
    public List<Component> get() {
        return lore;
    }

    private boolean checkRequired(@Nullable final List<Component> lore) {
        if (lore == null) {
            return false;
        }
        if (exact) {
            if (this.lore.size() != lore.size()) {
                return false;
            }
            for (int i = 0; i < lore.size(); i++) {
                if (!this.lore.get(i).equals(lore.get(i))) {
                    return false;
                }
            }
        } else {
            return !checkNonExact(lore);
        }
        return true;
    }

    private boolean checkNonExact(final List<Component> lore) {
        for (final Component line : this.lore) {
            boolean has = false;
            for (final Component itemLine : lore) {
                if (itemLine.equals(line)) {
                    has = true;
                    break;
                }
            }
            if (!has) {
                return true;
            }
        }
        return false;
    }
}
