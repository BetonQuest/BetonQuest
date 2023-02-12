package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.item.QuestItem;

import java.util.Arrays;
import java.util.Base64;

@SuppressWarnings("PMD.CommentRequired")
public class PersistentDataContainerHandler {
    private byte[] data;
    private QuestItem.Existence dataE = QuestItem.Existence.WHATEVER;

    public PersistentDataContainerHandler() {
    }

    public void set(final String data) {
        this.data = Base64.getDecoder().decode(data);
        dataE = QuestItem.Existence.REQUIRED;
    }

    public byte[] get() {
        return data;
    }

    public boolean check(final byte[] pdc) {
        switch (dataE) {
            case WHATEVER:
                return true;
            case REQUIRED:
                return pdc != null && Arrays.equals(pdc, data);
            case FORBIDDEN:
                return pdc == null;
            default:
                return false;
        }
    }
}
