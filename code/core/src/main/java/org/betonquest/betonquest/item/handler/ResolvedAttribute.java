package org.betonquest.betonquest.item.handler;

import org.bukkit.inventory.meta.ItemMeta;

/**
 * An {@link Attribute} with definit values.
 *
 * @param <M> applicable meta
 */
public interface ResolvedAttribute<M extends ItemMeta> {

    /**
     * Gets the class of meta this attribute is for.
     *
     * @return the ItemMeta class for the Handler
     */
    Class<M> metaClass();

    /**
     * Reconstitute this Attribute data into the specified meta.
     *
     * @param meta the meta to populate
     */
    void populate(M meta);

    /**
     * Reconstitute this Attribute data into the specified meta if it is applicable to {@link #metaClass()}.
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
     * Check to see if the specified ItemMeta matches the Attribute.
     *
     * @param meta the ItemMeta to check
     * @return if the meta satisfies the requirement
     */
    boolean check(M meta);

    /**
     * Check to see if the specified ItemMeta matches the Attribute if it is applicable to {@link #metaClass()}.
     * <p>
     * When the meta is not applicable it will return {@code true}.
     *
     * @param meta the ItemMeta to check
     * @return if the meta satisfies the requirement
     */
    @SuppressWarnings("unchecked")
    default boolean rawCheck(final ItemMeta meta) {
        return !metaClass().isInstance(meta) || check((M) meta);
    }

    /**
     * Resolved Attribute for the standard item meta.
     */
    interface Standard extends ResolvedAttribute<ItemMeta> {

        @Override
        default Class<ItemMeta> metaClass() {
            return ItemMeta.class;
        }
    }
}
