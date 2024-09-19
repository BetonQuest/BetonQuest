package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.item.QuestItem.Existence;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("PMD.CommentRequired")
public class NameHandler {
    @Nullable
    private String name;

    private Existence existence = Existence.WHATEVER;

    public NameHandler() {
    }

    /**
     * Replaces all underscores with spaces, except for those that are escaped with a backslash.
     *
     * @param input The input string.
     * @return The input string with all underscores replaced with spaces, except for those that are escaped with a backslash.
     */
    protected static String replaceUnderscore(final String input) {
        return input.replaceAll("(?<!\\\\)_", " ").replaceAll("\\\\_", "_");
    }

    public void set(final String name) throws InstructionParseException {
        if (name.isEmpty()) {
            throw new InstructionParseException("Name cannot be empty");
        }
        if (QuestItem.NONE_KEY.equalsIgnoreCase(name)) {
            existence = Existence.FORBIDDEN;
        } else {
            this.name = replaceUnderscore(name).replace('&', '§');
            existence = Existence.REQUIRED;
        }
    }

    @Nullable
    public String get() {
        return name;
    }

    public boolean check(@Nullable final String name) {
        return switch (existence) {
            case WHATEVER -> true;
            case REQUIRED -> name != null && name.equals(this.name);
            case FORBIDDEN -> name == null;
        };
    }
}
