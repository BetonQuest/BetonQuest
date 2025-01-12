package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("PMD.CommentRequired")
public class LoreHandler implements ItemMetaHandler<ItemMeta> {
    private final List<String> lore = new LinkedList<>();

    private Existence existence = Existence.WHATEVER;

    /**
     * If the lore need to be exact the same or just contain all specified lines.
     */
    private boolean exact = true;

    public LoreHandler() {
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
            for (final String line : meta.getLore()) {
                string.append(line).append(';');
            }
            return "lore:" + string.substring(0, string.length() - 1).replace(" ", "_").replace("§", "&");
        }
        return null;
    }

    @Override
    public void set(final String key, final String data) throws InstructionParseException {
        switch (key) {
            case "lore" -> {
                if (Existence.NONE_KEY.equalsIgnoreCase(data)) {
                    existence = Existence.FORBIDDEN;
                } else {
                    existence = Existence.REQUIRED;
                    for (final String line : data.split(";")) {
                        this.lore.add(NameHandler.replaceUnderscore(line).replaceAll("&", "§"));
                    }
                }
            }
            case "lore-containing" -> exact = false;
            default -> throw new InstructionParseException("Unknown lore key: " + key);
        }
    }

    @Override
    public void populate(final ItemMeta meta) {
        meta.setLore(get());
    }

    @Override
    public boolean check(final ItemMeta meta) {
        final List<String> lore = meta.getLore();
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
    public List<String> get() {
        return lore;
    }

    private boolean checkRequired(@Nullable final List<String> lore) {
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

    private boolean checkNonExact(final List<String> lore) {
        for (final String line : this.lore) {
            boolean has = false;
            for (final String itemLine : lore) {
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
