package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Requires the player to use the specified BetonQuest language.
 */
public class LanguageCondition extends Condition {

    /**
     * The player must have selected one of the languages in this set for the condition to become true.
     */
    private final Set<String> expectedLanguages;

    /**
     * Create a language condition.
     *
     * @param instruction instruction to parse
     * @throws InstructionParseException if the instruction is invalid
     */
    public LanguageCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        final String[] languages = instruction.getArray();
        for (final String language : languages) {
            if (!Config.getLanguages().contains(language)) {
                throw new InstructionParseException("Language " + language + " does not exist.");
            }
        }
        expectedLanguages = new HashSet<>(Arrays.asList(languages));
    }

    @Override
    protected Boolean execute(final Profile profile) {
        final String playerLanguage = BetonQuest.getInstance().getPlayerData(profile).getLanguage();
        return expectedLanguages.contains(playerLanguage);
    }

}
