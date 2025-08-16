package org.betonquest.betonquest.compatibility.mcmmo;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link McMMOSkillLevelCondition}s from {@link Instruction}s.
 */
public class McMMOSkillLevelConditionFactory implements PlayerConditionFactory {

    /**
     * Logger Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for mc mmo level conditions.
     *
     * @param loggerFactory the logger factory to create new class specific logger
     * @param data          the data for primary server thread access
     */
    public McMMOSkillLevelConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<PrimarySkillType> skillType = instruction.get(Argument.ENUM(PrimarySkillType.class));
        final Variable<Number> level = instruction.get(Argument.NUMBER);
        final BetonQuestLogger log = loggerFactory.create(McMMOSkillLevelCondition.class);
        return new PrimaryServerThreadPlayerCondition(new OnlineConditionAdapter(
                new McMMOSkillLevelCondition(skillType, level),
                log, instruction.getPackage()), data);
    }
}
