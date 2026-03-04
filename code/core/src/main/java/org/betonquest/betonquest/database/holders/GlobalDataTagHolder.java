package org.betonquest.betonquest.database.holders;

import org.betonquest.betonquest.api.data.TagHolder;
import org.betonquest.betonquest.database.GlobalData;

import java.util.Set;

/**
 * An implementation of {@link TagHolder} for {@link GlobalData}.
 */
public class GlobalDataTagHolder implements TagHolder {

    /**
     * The global data to access tags from.
     */
    private final GlobalData globalData;

    /**
     * Creates a new instance of GlobalDataTagHolder.
     *
     * @param globalData the global data
     */
    public GlobalDataTagHolder(final GlobalData globalData) {
        this.globalData = globalData;
    }

    @Override
    public Set<String> get() {
        return globalData.getTags();
    }

    @Override
    public boolean has(final String tag) {
        return globalData.hasTag(tag);
    }

    @Override
    public void add(final String tag) {
        globalData.addTag(tag);
    }

    @Override
    public void remove(final String tag) {
        globalData.removeTag(tag);
    }
}
