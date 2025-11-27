package org.betonquest.betonquest.compatibility.mythicmobs.event;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;

/**
 * Casts a skill as a player.
 */
public class MythicCastSkillEvent implements OnlineEvent {
    /**
     * Logger instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Source pack for logging.
     */
    private final QuestPackage pack;

    /**
     * The BukkitAPIHelper used to cast the skill.
     */
    private final BukkitAPIHelper apiHelper;

    /**
     * Name of the skill.
     */
    private final Variable<String> skillName;

    /**
     * Constructs a new MythicCastSkillEvent.
     *
     * @param log       logs when the skill could not be cast
     * @param pack      the source pack used as log source
     * @param apiHelper the BukkitAPIHelper to cast the skill
     * @param skillName the name of the skill
     */
    public MythicCastSkillEvent(final BetonQuestLogger log, final QuestPackage pack, final BukkitAPIHelper apiHelper,
                                final Variable<String> skillName) {
        this.log = log;
        this.pack = pack;
        this.apiHelper = apiHelper;
        this.skillName = skillName;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final String name = skillName.getValue(profile);
        if (!apiHelper.castSkill(profile.getPlayer(), name)) {
            log.debug(pack, "Could not cast skill '" + name + "' for profile " + profile);
        }
    }
}
