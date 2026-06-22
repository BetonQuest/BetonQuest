package org.betonquest.betonquest.api.quest;

/**
 * Stores the implementation factories for Quest Types.
 *
 * @param <F> the type to be produced from the stored factory
 * @since 3.0.0
 */
public interface FeatureTypeRegistry<F> extends FeatureRegistry<TypeFactory<F>> {

}
