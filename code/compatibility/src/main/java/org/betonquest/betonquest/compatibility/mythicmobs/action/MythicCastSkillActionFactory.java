package org.betonquest.betonquest.compatibility.mythicmobs.action;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;

/**
 * Factory to create {@link MythicCastSkillAction}s from {@link Instruction}s.
 */
public class MythicCastSkillActionFactory implements PlayerActionFactory {

    /**
     * Factory to create new class specific loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The BukkitAPIHelper used to cast the skill.
     */
    private final BukkitAPIHelper apiHelper;

    /**
     * Create a new Factory.
     *
     * @param loggerFactory the logger factory to create class specific logger
     * @param apiHelper     the BukkitAPIHelper to cast the skill
     */
    public MythicCastSkillActionFactory(final BetonQuestLoggerFactory loggerFactory, final BukkitAPIHelper apiHelper) {
        this.loggerFactory = loggerFactory;
        this.apiHelper = apiHelper;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> skillName = instruction.string().get();
        final BetonQuestLogger log = loggerFactory.create(MythicCastSkillAction.class);
        return new OnlineActionAdapter(
                new MythicCastSkillAction(log, instruction.getPackage(), apiHelper, skillName),
                log,
                instruction.getPackage()
        );
    }
}
