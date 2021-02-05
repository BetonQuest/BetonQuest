package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.item.QuestItem.Existence;

@SuppressWarnings("PMD.CommentRequired")
public class HeadOwnerHandler {

    private String owner;
    private Existence ownerE = Existence.WHATEVER;

    public HeadOwnerHandler() {
    }

    public void set(final String string) {
        if ("none".equalsIgnoreCase(string)) {
            ownerE = Existence.FORBIDDEN;
        } else {
            owner = string;
            ownerE = Existence.REQUIRED;
        }
    }

    public String get() {
        return owner;
    }

    public boolean check(final String string) {
        switch (ownerE) {
            case WHATEVER:
                return true;
            case REQUIRED:
                return string != null && string.equals(owner);
            case FORBIDDEN:
                return string == null;
            default:
                return false;
        }
    }

}
