package org.betonquest.betonquest;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a string that can contain variables.
 * Makes handling instructions with variables easier.
 */
public class VariableString {

    /**
     * The string that may contain variables.
     */
    private final String string;

    /**
     * The list of variables in the string.
     */
    private final List<String> variables = new ArrayList<>();

    /**
     * The package in which the string is defined.
     */
    private final QuestPackage questPackage;


    /**
     * Creates a new VariableString.
     *
     * @param questPackage the package in which the string is used
     * @param string       the string that may contain variables
     * @throws InstructionParseException if the variables could not be created
     */
    public VariableString(final QuestPackage questPackage, final String string) throws InstructionParseException {
        this.string = string;
        this.questPackage = questPackage;

        for (final String variable : BetonQuest.resolveVariables(string)) {
            try {
                BetonQuest.createVariable(questPackage, variable);
            } catch (final InstructionParseException exception) {
                throw new InstructionParseException("Could not create '" + variable + "' variable: "
                        + exception.getMessage(), exception);
            }
            if (!variables.contains(variable)) {
                variables.add(variable);
            }
        }
    }


    /**
     * Resolves all variables in the string and returns the result.
     *
     * @param profile the profile of the player to resolve the variables for
     * @return the string with all variables resolved
     */
    public String getString(final Profile profile) {
        String resolvedString = string;
        for (final String variable : variables) {
            final String resolvedVariable = BetonQuest.getInstance().getVariableValue(questPackage.getQuestPath(), variable, profile);
            resolvedString = resolvedString.replace(variable, resolvedVariable);
        }
        return resolvedString;
    }
}
