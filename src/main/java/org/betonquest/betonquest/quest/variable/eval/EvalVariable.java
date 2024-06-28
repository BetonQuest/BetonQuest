package org.betonquest.betonquest.quest.variable.eval;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.Variable;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.jetbrains.annotations.Nullable;

/**
 * A variable which evaluates to another variable.
 */
public class EvalVariable implements Variable {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The package.
     */
    private final QuestPackage pack;

    /**
     * The evaluation input.
     */
    private final VariableString evaluation;

    /**
     * Create a new Eval variable.
     *
     * @param log        the logger
     * @param pack       the package
     * @param evaluation the evaluation input
     */
    public EvalVariable(final BetonQuestLogger log, final QuestPackage pack, final VariableString evaluation) {
        this.log = log;
        this.pack = pack;
        this.evaluation = evaluation;
    }

    @Override
    public String getValue(@Nullable final Profile profile) {
        try {
            return new VariableString(pack, "%" + evaluation.getString(profile) + "%").getValue(profile);
        } catch (final InstructionParseException | QuestRuntimeException e) {
            log.warn(pack, "Could not resolve eval variable: " + e.getMessage(), e);
            return "";
        }
    }
}
