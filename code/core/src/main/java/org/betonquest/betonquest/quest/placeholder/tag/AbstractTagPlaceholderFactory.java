package org.betonquest.betonquest.quest.placeholder.tag;

/**
 * An abstract class for creating Tag placeholders.
 *
 * @param <T> the data holder type
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractTagPlaceholderFactory<T> {

    /**
     * The data holder.
     */
    protected final T dataHolder;

    /**
     * Create a new Tag placeholder factory.
     *
     * @param dataHolder the data holder
     */
    public AbstractTagPlaceholderFactory(final T dataHolder) {
        this.dataHolder = dataHolder;
    }
}
