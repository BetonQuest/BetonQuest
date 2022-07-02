package org.betonquest.betonquest.database;

import java.util.List;

public interface TagData {
    List<String> getTags();

    boolean hasTag(String tag);

    void addTag(String tag);

    void removeTag(String tag);
}
