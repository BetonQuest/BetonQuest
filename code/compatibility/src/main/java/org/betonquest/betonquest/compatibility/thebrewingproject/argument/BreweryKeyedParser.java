package org.betonquest.betonquest.compatibility.thebrewingproject.argument;

import dev.jsinco.brewery.api.util.BreweryKey;
import dev.jsinco.brewery.api.util.BreweryKeyed;
import dev.jsinco.brewery.api.util.BreweryRegistry;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;

/**
 * Parse string representations of {@link BreweryKeyed} types that are backed with a {@link BreweryRegistry}.
 *
 * @param registry the {@link BreweryKeyed} item backing
 * @param <T>      the type of {@link BreweryKeyed} to parse
 */
public record BreweryKeyedParser<T extends BreweryKeyed>(
        BreweryRegistry<T> registry) implements SimpleArgumentParser<T> {

    @Override
    public T apply(final String string) throws QuestException {
        final T element = registry.get(BreweryKey.parse(string));
        if (element == null) {
            throw new QuestException("Invalid element key '%s', unknown key".formatted(string));
        }
        return element;
    }
}
