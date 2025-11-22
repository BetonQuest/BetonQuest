package org.betonquest.betonquest.compatibility.mythicmobs.event;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;

/**
 * Factory to create {@link MythicCastSkillEvent}s from {@link Instruction}s.
 */
public class MythicCastSkillEventFactory implements PlayerEventFactory {

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
    public MythicCastSkillEventFactory(final BetonQuestLoggerFactory loggerFactory, final BukkitAPIHelper apiHelper) {
        this.loggerFactory = loggerFactory;
        this.apiHelper = apiHelper;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> skillName = instruction.get(Argument.STRING);
        final BetonQuestLogger log = loggerFactory.create(MythicCastSkillEvent.class);
        return new OnlineEventAdapter(
                new MythicCastSkillEvent(log, instruction.getPackage(), apiHelper, skillName),
                log,
                instruction.getPackage()
        );
    }
}
