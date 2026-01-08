package org.betonquest.betonquest.compatibility.traincarts.objectives;

import com.bergerkiller.bukkit.tc.events.seat.MemberSeatExitEvent;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.compatibility.traincarts.TrainCartsUtils;

/**
 * This {@link DefaultObjective} is completed when a player exits a train.
 */
public class TrainCartsExitObjective extends DefaultObjective {

    /**
     * The name of the train, maybe empty.
     */
    private final Argument<String> name;

    /**
     * The constructor takes an Instruction object as a parameter and throws an QuestException.
     *
     * @param service the ObjectiveFactoryService to be used in the constructor
     * @param name    the name of the train, maybe empty
     * @throws QuestException if there is an error while parsing the instruction
     */
    public TrainCartsExitObjective(final ObjectiveFactoryService service, final Argument<String> name) throws QuestException {
        super(service);
        this.name = name;
    }

    /**
     * The method is called when a player exits a train.
     *
     * @param event         The {@link MemberSeatExitEvent}.
     * @param onlineProfile The {@link OnlineProfile}.
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onMemberSeatExit(final MemberSeatExitEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (!containsPlayer(onlineProfile) || !checkConditions(onlineProfile)) {
            return;
        }
        if (TrainCartsUtils.isValidTrain(name.getValue(onlineProfile),
                event.getMember().getGroup().getProperties().getTrainName())) {
            completeObjective(onlineProfile);
        }
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
