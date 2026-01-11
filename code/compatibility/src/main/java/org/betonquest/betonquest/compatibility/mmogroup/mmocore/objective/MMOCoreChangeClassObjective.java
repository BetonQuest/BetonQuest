package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import net.Indyuce.mmocore.api.event.PlayerChangeClassEvent;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.jetbrains.annotations.Nullable;

/**
 * An objective that listens for the player changing their MMOCore class.
 */
public class MMOCoreChangeClassObjective extends DefaultObjective {

    /**
     * The name of the class that the player needs to change to.
     */
    @Nullable
    private final Argument<String> targetClassName;

    /**
     * Constructor for the MMOCoreChangeClassObjective.
     *
     * @param service         the objective service
     * @param targetClassName the name of the class to be changed to
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public MMOCoreChangeClassObjective(final ObjectiveService service, @Nullable final Argument<String> targetClassName) throws QuestException {
        super(service);
        this.targetClassName = targetClassName;
    }

    /**
     * Listens for the player changing their MMOCore class.
     *
     * @param event         the event
     * @param onlineProfile the player
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onClassChange(final PlayerChangeClassEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (targetClassName == null) {
            getService().complete(onlineProfile);
            return;
        }

        if (targetClassName.getValue(onlineProfile).equalsIgnoreCase(event.getNewClass().getName())) {
            getService().complete(onlineProfile);
        }
    }
}
