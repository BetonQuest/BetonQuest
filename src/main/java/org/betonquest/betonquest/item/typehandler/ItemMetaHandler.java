package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

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
     * Gets the class of meta this Handler works on.
     *
     * @return the ItemMeta class for the Handler
     */
    Class<M> metaClass();

    /**
     * Sets the data into the Handler.
     * <p>
     * The data may be empty if the key is just a keyword.
     *
     * @param key  the lower case key
     * @param data the associated data
     * @throws InstructionParseException if the data is malformed or key not valid for handler
     */
    void set(String key, String data) throws InstructionParseException;

    /**
     * Reconstitute this Handler data into the specified meta.
     *
     * @param meta the meta to populate
     */
    void populate(M meta);

    /**
     * Reconstitute this Handler data into the specified meta.
     * <p>
     * Defaults to {@link #populate(ItemMeta)}.
     *
     * @param meta    the meta to populate
     * @param profile the profile for customized population
     */
    default void populate(final M meta, @Nullable final Profile profile) {
        populate(meta);
    }

    /**
     * Reconstitute this Handler data into the specified meta if it is applicable to {@link #metaClass()}.
     * <p>
     * When the meta is not applicable nothing changes.
     *
     * @param meta    the meta to populate
     * @param profile the profile for customized population
     */
    @SuppressWarnings("unchecked")
    default void rawPopulate(final ItemMeta meta, @Nullable final Profile profile) {
        if (metaClass().isInstance(meta)) {
            populate((M) meta, profile);
        }
    }

    /**
     * Check to see if the specified ItemMeta matches the Handler.
     *
     * @param meta the ItemMeta to check
     * @return if the meta satisfies the requirement defined via {@link #set(String, String)}
     */
    boolean check(M meta);
}
