package org.betonquest.betonquest.quest.condition.language;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Factory for {@link LanguageCondition}s.
 */
public class LanguageConditionFactory implements PlayerConditionFactory {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Create a language condition factory.
     *
     * @param betonQuest the BetonQuest instance
     */
    public LanguageConditionFactory(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final String[] languages = instruction.getArray();
        for (final String language : languages) {
            if (!Config.getLanguages().contains(language)) {
                throw new InstructionParseException("Language " + language + " does not exist.");
            }
        }
        final Set<String> expectedLanguages = new HashSet<>(Arrays.asList(languages));
        return new LanguageCondition(betonQuest, expectedLanguages);
    }
}
