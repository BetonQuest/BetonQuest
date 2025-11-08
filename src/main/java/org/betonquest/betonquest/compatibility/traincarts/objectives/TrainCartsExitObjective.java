package org.betonquest.betonquest.compatibility.traincarts.objectives;

import com.bergerkiller.bukkit.tc.events.seat.MemberSeatExitEvent;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.compatibility.traincarts.TrainCartsUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * This {@link Objective} is completed when a player exits a train.
 */
public class TrainCartsExitObjective extends Objective implements Listener {

    /**
     * The name of the train, maybe empty.
     */
    private final Variable<String> name;

    /**
     * The constructor takes an Instruction object as a parameter and throws an QuestException.
     *
     * @param instruction the Instruction object to be used in the constructor
     * @param name        the name of the train, maybe empty
     * @throws QuestException if there is an error while parsing the instruction
     */
    public TrainCartsExitObjective(final Instruction instruction, final Variable<String> name) throws QuestException {
        super(instruction);
        this.name = name;
    }

    /**
     * The method is called when a player exits a train.
     *
     * @param event The {@link MemberSeatExitEvent}.
     */
    @EventHandler
    public void onMemberSeatExit(final MemberSeatExitEvent event) {
        if (!(event.getEntity() instanceof final Player player)) {
            return;
        }
        final OnlineProfile onlineProfile = profileProvider.getProfile(player);
        if (!containsPlayer(onlineProfile) || !checkConditions(onlineProfile)) {
            return;
        }
        qeHandler.handle(() -> {
            if (TrainCartsUtils.isValidTrain(name.getValue(onlineProfile),
                    event.getMember().getGroup().getProperties().getTrainName())) {
                completeObjective(onlineProfile);
            }
        });
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
