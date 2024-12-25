package org.betonquest.betonquest.item.typehandler;

import org.bukkit.inventory.meta.ItemMeta;

/**
 * Handles de/-serialization of ItemMeta from/into QuestItem string format.
 * <p>
 * The implementation needs to provide a static method that converts the item meta into QuestItem format.
 * It accepts the meta to serialize and returns the parsed values with a leading space or an empty string.
 * <pre>{@code
 * /// Converts the meta into QuestItem format.
 * ///
 * /// @param bookMeta the meta to serialize
 * /// @return parsed values with leading space or empty string
 * public static String serializeToString(M meta);
 * }</pre>
 *
 * @param <M> handled meta
 */
public interface ItemMetaHandler<M extends ItemMeta> {

    /**
     * Sets the data into the Handler.
     * <p>
     * The data may be empty if the key is just a keyword.
     *
     * @param key  the lower case key
     * @param data the associated data
     */
    void set(String key, String data);

    /**
     * Reconstitute this Handler data into the specified meta.
     *
     * @param meta the meta to populate
     */
    void populate(M meta);

    /**
     * Check to see if the specified ItemMeta matches the Handler.
     *
     * @param meta the ItemMeta to check
     * @return if the meta satisfies the requirement defined via {@link #set(String, String)}
     */
    boolean check(M meta);
}
