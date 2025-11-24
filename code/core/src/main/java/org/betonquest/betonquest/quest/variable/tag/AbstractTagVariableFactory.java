package org.betonquest.betonquest.quest.variable.tag;

/**
 * An abstract class for creating Tag variables.
 *
 * @param <T> the data holder type
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractTagVariableFactory<T> {

    /**
     * The data holder.
     */
    protected final T dataHolder;

    /**
     * Create a new Tag variable factory.
     *
     * @param dataHolder the data holder
     */
    public AbstractTagVariableFactory(final T dataHolder) {
        this.dataHolder = dataHolder;
    }
}
