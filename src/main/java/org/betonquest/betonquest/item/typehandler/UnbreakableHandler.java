package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.item.QuestItem.Existence;

@SuppressWarnings("PMD.CommentRequired")
public class UnbreakableHandler {

    private Existence unbreakable = Existence.WHATEVER;

    public UnbreakableHandler() {
    }

    public void set(final String string) {
        if ("true".equalsIgnoreCase(string)) {
            unbreakable = Existence.REQUIRED;
        } else {
            unbreakable = Existence.FORBIDDEN;
        }
    }

    public boolean isUnbreakable() {
        return unbreakable == Existence.REQUIRED;
    }

    public boolean check(final boolean bool) {
        switch (unbreakable) {
            case WHATEVER:
                return true;
            case REQUIRED:
                return bool;
            case FORBIDDEN:
                return !bool;
            default:
                return false;
        }
    }

}
