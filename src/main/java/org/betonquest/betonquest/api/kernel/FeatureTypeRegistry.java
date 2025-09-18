package org.betonquest.betonquest.api.kernel;

/**
 * Stores the implementation factories for Quest Types.
 *
 * @param <F> the type to be produced from the stored factory
 */
public interface FeatureTypeRegistry<F> extends FeatureRegistry<TypeFactory<F>> {

}
