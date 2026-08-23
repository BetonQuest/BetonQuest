package org.betonquest.betonquest.item.handler;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
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
     * The keys this handler allows in {@link #parse(Instruction)} which are used for data identification.
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
     * Reads the data from the instruction into an {@link Attribute}.
     * <p>
     * The data may be the same as the key if it is just a keyword.
     * <p>
     * When there is no applicable part in the instruction {@code null} should be returned.
     *
     * @param instruction the instruction to read from associated data
     * @return the read attributes if present, null otherwise
     * @throws QuestException if the data is malformed
     */
    @Nullable
    Attribute<M> parse(Instruction instruction) throws QuestException;

    /**
     * Handler for the standard item meta.
     */
    interface Standard extends ItemMetaHandler<ItemMeta> {

        @Override
        default Class<ItemMeta> metaClass() {
            return ItemMeta.class;
        }
    }
}
