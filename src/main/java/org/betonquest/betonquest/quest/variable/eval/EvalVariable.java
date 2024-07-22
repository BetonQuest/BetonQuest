package org.betonquest.betonquest.quest.variable.eval;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariable;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.jetbrains.annotations.Nullable;

/**
 * A variable which evaluates to another variable.
 */
public class EvalVariable implements NullableVariable {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The variable processor used to create the evaluated variable.
     */
    private final VariableProcessor variableProcessor;

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
     * @param variableProcessor the variable processor
     * @param pack              the package
     * @param evaluation        the evaluation input
     */
    public EvalVariable(final BetonQuestLogger log, final VariableProcessor variableProcessor, final QuestPackage pack, final VariableString evaluation) {
        this.log = log;
        this.variableProcessor = variableProcessor;
        this.pack = pack;
        this.evaluation = evaluation;
    }

    @Override
    public String getValue(@Nullable final Profile profile) {
        try {
            return new VariableString(variableProcessor, pack, "%" + evaluation.getValue(profile) + "%").getValue(profile);
        } catch (final InstructionParseException | QuestRuntimeException e) {
            log.warn(pack, "Could not resolve eval variable: " + e.getMessage(), e);
            return "";
        }
    }
}
