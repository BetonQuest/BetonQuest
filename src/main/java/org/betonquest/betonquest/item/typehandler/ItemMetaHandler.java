package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

/**
 * Handles de/-serialization of ItemMeta from/into QuestItem string format.
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
     * Converts the meta into QuestItem format.
     *
     * @param meta the meta to serialize
     * @return parsed values with leading space or empty string
     */
    String serializeToString(M meta);

    /**
     * Converts the meta into QuestItem format if it is applicable to {@link #metaClass()}.
     * When the meta is not applicable it will return an empty string.
     *
     * @param meta the meta to serialize
     * @return parsed values with leading space or empty string
     */
    @SuppressWarnings("unchecked")
    default String rawSerializeToString(final ItemMeta meta) {
        if (metaClass().isInstance(meta)) {
            return serializeToString((M) meta);
        }
        return "";
    }

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

    /**
     * Check to see if the specified ItemMeta matches the Handler if it is applicable to {@link #metaClass()}.
     * <p>
     * When the meta is not applicable it will return {@code true}.
     *
     * @param meta the ItemMeta to check
     * @return if the meta satisfies the requirement defined via {@link #set(String, String)}
     */
    @SuppressWarnings("unchecked")
    default boolean rawCheck(final ItemMeta meta) {
        if (metaClass().isInstance(meta)) {
            return check((M) meta);
        }
        return true;
    }
}
