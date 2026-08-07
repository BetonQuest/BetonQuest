package org.betonquest.betonquest.lib.instruction.reader;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.FlagState;
import org.betonquest.betonquest.api.instruction.InstructionParts;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The default implementation of the {@link InstructionReaderStrategy}.
 */
public class DefaultInstructionReader implements InstructionReaderStrategy<String> {

    /**
     * The parts of the instruction.
     */
    private final InstructionParts parts;

    /**
     * Constructs a new instruction reader.
     *
     * @param parts the parts of the instruction
     */
    public DefaultInstructionReader(final InstructionParts parts) {
        this.parts = parts;
    }

    @Override
    public String getNext() throws QuestException {
        return parts.nextElement();
    }

    @Override
    public @Nullable String getOptional(final String prefix) {
        return parts.getParts().stream()
                .filter(part -> part.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT) + ":"))
                .findFirst()
                .map(part -> part.substring(prefix.length() + 1)).orElse(null);
    }

    @Override
    public Map.Entry<FlagState, String> getFlag(final String prefix) {
        return parts.getParts().stream()
                .filter(part -> part.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT) + ":")
                        || part.equalsIgnoreCase(prefix))
                .findFirst()
                .map(part -> part.substring(prefix.length()))
                .map(part -> part.startsWith(":") ? Map.entry(FlagState.DEFINED, part.substring(1))
                        : Map.entry(FlagState.UNDEFINED, part))
                .orElse(Map.entry(FlagState.ABSENT, ""));
    }

    @Override
    public Map<String, String> getNamed(final Predicate<String> keyFilter) {
        return parts.getParts().stream()
                .filter(part -> part.startsWith("+"))
                .map(part -> part.substring(1))
                .filter(part -> part.indexOf(':') > 0)
                .map(part -> {
                    final int colonIndex = part.indexOf(':');
                    return Map.entry(part.substring(0, colonIndex), part.substring(colonIndex + 1));
                })
                .filter(argument -> keyFilter.test(argument.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
