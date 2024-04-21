package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem.Existence;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("PMD.CommentRequired")
public class NameHandler {
    @Nullable
    private String name;

    private Existence existence = Existence.WHATEVER;

    public NameHandler() {
    }

    public void set(final String name) throws InstructionParseException {
        if (name.isEmpty()) {
            throw new InstructionParseException("Name cannot be empty");
        }
        if ("none".equalsIgnoreCase(name)) {
            existence = Existence.FORBIDDEN;
        } else {
            this.name = name.replace('_', ' ').replace('&', '§');
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
            case REQUIRED -> {
                assert this.name != null;
                yield this.name.equals(name);
            }
            case FORBIDDEN -> name == null;
        };
    }

}
