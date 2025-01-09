package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.QuestException;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Exposes the presence of tags as a variable.
 * Originally implemented for use with the PAPI integration.
 */
public class TagVariable extends Variable {
    /**
     * The tag to check for.
     */
    private final String tagName;

    /**
     * The quest package to check for the tag.
     */
    private final QuestPackage questPackage;

    /**
     * Whether to return true/false or the configured messages.
     */
    private final boolean papiMode;

    /**
     * Constructs a new GlobalTagVariable.
     *
     * @param instruction the instruction to parse
     * @throws QuestException if the instruction is malformed
     */
    public TagVariable(final Instruction instruction) throws QuestException {
        super(instruction);
        tagName = instruction.next();
        questPackage = instruction.getPackage();
        papiMode = instruction.hasArgument("papiMode");
    }

    /**
     * Returns the value of the variable.
     *
     * @param profile profile of the player
     * @return the value of the variable
     */
    @Override
    public String getValue(@Nullable final Profile profile) {
        if (profile == null) {
            return "";
        }
        final List<String> tags = BetonQuest.getInstance().getPlayerDataStorage().get(profile).getTags();
        return getValue(tags);
    }

    /**
     * Finds out whether the player has the tag and returns this as a true/false string.
     * If papiMode is enabled, it returns the configured messages instead.
     *
     * @param tags the tags to check
     * @return whether the player has the variable
     */
    protected String getValue(final List<String> tags) {
        final String lang = Config.getLanguage();

        if (tags.contains(questPackage.getQuestPath() + "." + tagName)) {
            return papiMode ? Config.getMessage(lang, "condition_variable_met") : "true";
        }
        return papiMode ? Config.getMessage(lang, "condition_variable_not_met") : "false";
    }
}
