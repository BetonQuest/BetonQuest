package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.exceptions.QuestException;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Adapter for {@link PlayerVariable} and {@link PlayerlessVariable} to fit the old convention of
 * {@link org.betonquest.betonquest.api.Variable Legacy Variable}.
 */
public class LegacyVariableAdapter extends org.betonquest.betonquest.api.Variable {
    /**
     * The player variable to be adapted.
     */
    @Nullable
    private final PlayerVariable playerVariable;

    /**
     * The playerless variable to be adapted.
     */
    @Nullable
    private final PlayerlessVariable playerlessVariable;

    /**
     * The custom logger for logging exceptions from the new throwing Variables.
     */
    private final BetonQuestLogger log;

    /**
     * Create a legacy variable from an {@link PlayerVariable} and a {@link PlayerlessVariable}.
     * If the variable does not support "static"/playerless execution ({@code staticness = false}) then no
     * {@link PlayerVariable} instance must be provided.
     * <p>
     * When no player variable is given the playerless variable is required.
     *
     * @param instruction        instruction used to create the variables
     * @param playerVariable     variable to use
     * @param playerlessVariable static variable to use or null if no static execution is supported
     * @param log                the custom logger for logging exceptions from the new throwing Variables
     */
    public LegacyVariableAdapter(final Instruction instruction, @Nullable final PlayerVariable playerVariable,
                                 @Nullable final PlayerlessVariable playerlessVariable, final BetonQuestLogger log) {
        super(instruction);
        this.log = log;
        if (playerVariable == null && playerlessVariable == null) {
            throw new IllegalArgumentException("Either the normal or static factory must be present!");
        }
        this.playerVariable = playerVariable;
        this.playerlessVariable = playerlessVariable;
        staticness = playerlessVariable != null;
    }

    @Override
    public String getValue(@Nullable final Profile profile) {
        try {

            if (playerVariable == null || profile == null) {
                Objects.requireNonNull(playerlessVariable);
                return playerlessVariable.getValue();
            } else {
                return playerVariable.getValue(profile);
            }
        } catch (final QuestException e) {
            log.warn(instruction.getPackage(), "Unable to get variable value: " + e.getMessage(), e);
            return "";
        }
    }
}
