package pl.betoncraft.betonquest.item.typehandler;

import pl.betoncraft.betonquest.item.QuestItem.Existence;

public class UnbreakableHandler {

    private Existence unbreakable = Existence.WHATEVER;

    public UnbreakableHandler() {
    }

    public void set(final String string) {
        if (string.equalsIgnoreCase("true")) {
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
