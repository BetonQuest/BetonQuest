package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem.Existence;

@SuppressWarnings("PMD.CommentRequired")
public class NameHandler {

    private String name;
    private Existence existence = Existence.WHATEVER;

    public NameHandler() {
    }

    public void set(final String name) throws InstructionParseException {
        if (name == null || name.isEmpty()) {
            throw new InstructionParseException("Name cannot be empty");
        }
        if ("none".equalsIgnoreCase(name)) {
            existence = Existence.FORBIDDEN;
        } else {
            this.name = name.replace('_', ' ').replace('&', '§');
            existence = Existence.REQUIRED;
        }
    }

    public String get() {
        return name;
    }

    public boolean check(final String name) {
        switch (existence) {
            case WHATEVER:
                return true;
            case REQUIRED:
                return this.name.equals(name);
            case FORBIDDEN:
                return name == null;
            default:
                return false;
        }
    }

}
