package org.betonquest.betonquest.compatibility.mcmmo;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;

/**
 * Factory to create {@link McMMOSkillLevelCondition}s from {@link Instruction}s.
 */
public class McMMOSkillLevelConditionFactory implements PlayerConditionFactory {

    /**
     * Logger Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new factory for mc mmo level conditions.
     *
     * @param loggerFactory the logger factory to create new class specific logger
     */
    public McMMOSkillLevelConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<PrimarySkillType> skillType = instruction.get(instruction.getParsers().forEnum(PrimarySkillType.class));
        final Variable<Number> level = instruction.get(instruction.getParsers().number());
        final BetonQuestLogger log = loggerFactory.create(McMMOSkillLevelCondition.class);
        return new OnlineConditionAdapter(new McMMOSkillLevelCondition(skillType, level), log, instruction.getPackage());
    }
}
