package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import net.Indyuce.mmocore.experience.Profession;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;

/**
 * An objective that listens for the player leveling up in their MMOCore profession.
 */
public class MMOCoreProfessionObjective extends DefaultObjective {

    /**
     * The name of the profession that the player needs to level up.
     */
    private final Argument<String> professionName;

    /**
     * The target level to be reached.
     */
    private final Argument<Number> targetLevel;

    /**
     * Constructor for the MMOCoreProfessionObjective.
     *
     * @param service        the objective factory service
     * @param professionName the name of the profession to be leveled up, 'main' for class
     * @param targetLevel    the target level to be reached
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public MMOCoreProfessionObjective(final ObjectiveFactoryService service, final Argument<String> professionName,
                                      final Argument<Number> targetLevel) throws QuestException {
        super(service);
        this.professionName = professionName;
        this.targetLevel = targetLevel;
    }

    /**
     * Listens for the player leveling up in their MMOCore profession.
     *
     * @param event         the event
     * @param onlineProfile the player
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onLevelUp(final PlayerLevelUpEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (!containsPlayer(onlineProfile) || !checkConditions(onlineProfile)) {
            return;
        }
        final String professionName = this.professionName.getValue(onlineProfile);
        final Profession profession = event.getProfession();
        if (profession == null) {
            if (!"MAIN".equalsIgnoreCase(professionName)) {
                return;
            }
        } else if (!profession.getName().equalsIgnoreCase(professionName)) {
            return;
        }
        if (event.getNewLevel() < targetLevel.getValue(onlineProfile).intValue()) {
            return;
        }
        completeObjective(onlineProfile);
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
