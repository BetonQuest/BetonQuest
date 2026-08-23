package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

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
     * The keys this handler allows in {@link #set(Instruction)} which are used for data identification.
     *
     * @return keys in lower case
     */
    Set<String> keys();

    /**
     * Converts the meta into QuestItem format.
     *
     * @param meta the meta to serialize
     * @return parsed values or null
     */
    @Nullable
    String serializeToString(M meta);

    /**
     * Converts the meta into QuestItem format if it is applicable to {@link #metaClass()}.
     * When the meta is not applicable it will return null.
     *
     * @param meta the meta to serialize
     * @return parsed values or null
     */
    @SuppressWarnings("unchecked")
    @Nullable
    default String rawSerializeToString(final ItemMeta meta) {
        if (metaClass().isInstance(meta)) {
            return serializeToString((M) meta);
        }
        return null;
    }

    /**
     * Sets the data into the Handler.
     * <p>
     * The data may be the same as the key if it is just a keyword.
     *
     * @param instruction the instruction to read from associated data
     * @throws QuestException if the data is malformed or key not valid for handler
     */
    void set(Instruction instruction) throws QuestException;

    Resolved<M> resolve(@Nullable Profile profile) throws QuestException;

    interface Resolved<M extends ItemMeta> {

        /**
         * Gets the class of meta this Handler works on.
         *
         * @return the ItemMeta class for the Handler
         */
        Class<M> metaClass();

        /**
         * Reconstitute this Handler data into the specified meta.
         *
         * @param meta the meta to populate
         */
        void populate(final M meta);

        /**
         * Reconstitute this Handler data into the specified meta if it is applicable to {@link #metaClass()}.
         * <p>
         * When the meta is not applicable nothing changes.
         *
         * @param meta the meta to populate
         */
        @SuppressWarnings("unchecked")
        default void rawPopulate(final ItemMeta meta) {
            if (metaClass().isInstance(meta)) {
                populate((M) meta);
            }
        }

        /**
         * Check to see if the specified ItemMeta matches the Handler.
         *
         * @param meta the ItemMeta to check
         * @return if the meta satisfies the requirement defined via {@link #set(Instruction)}
         */
        boolean check(M meta);

        /**
         * Check to see if the specified ItemMeta matches the Handler if it is applicable to {@link #metaClass()}.
         * <p>
         * When the meta is not applicable it will return {@code true}.
         *
         * @param meta the ItemMeta to check
         * @return if the meta satisfies the requirement defined via {@link #set(Instruction)}
         */
        @SuppressWarnings("unchecked")
        default boolean rawCheck(final ItemMeta meta) {
            return !metaClass().isInstance(meta) || check((M) meta);
        }
    }

    interface ItemMetaResolved extends Resolved<ItemMeta> {

        @Override
        default Class<ItemMeta> metaClass() {
            return ItemMeta.class;
        }
    }
}
