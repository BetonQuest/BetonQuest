package pl.betoncraft.betonquest.item.typehandler;

import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem.Existence;

import java.util.LinkedList;
import java.util.List;

public class LoreHandler {

    private final List<String> lore = new LinkedList<>();
    private Existence existence = Existence.WHATEVER;
    private boolean exact = true;

    public LoreHandler() {
    }

    public void set(final String lore) throws InstructionParseException {
        if ("none".equals(lore)) {
            existence = Existence.FORBIDDEN;
            return;
        }
        existence = Existence.REQUIRED;
        for (final String line : lore.split(";")) {
            this.lore.add(line.replaceAll("_", " ").replaceAll("&", "§"));
        }
    }

    public void setNotExact() {
        exact = false;
    }

    public List<String> get() {
        return lore;
    }

    public boolean check(final List<String> lore) {
        switch (existence) {
            case WHATEVER:
                return true;
            case REQUIRED:
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
                    for (final String line : this.lore) {
                        boolean has = false;
                        for (final String itemLine : lore) {
                            if (itemLine.equals(line)) {
                                has = true;
                                break;
                            }
                        }
                        if (!has) {
                            return false;
                        }
                    }
                }
                return true;
            case FORBIDDEN:
                return lore == null || lore.isEmpty();
            default:
                return false;
        }
    }

}
