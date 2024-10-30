package org.betonquest.betonquest.compatibility.traincarts.objectives;

import com.bergerkiller.bukkit.tc.events.seat.MemberSeatExitEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.compatibility.traincarts.TrainCartsUtils;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * This {@link Objective} is completed when a player exits a train.
 */
public class TrainCartsExitObjective extends Objective implements Listener {
    /**
     * The {@link BetonQuestLogger} for logging.
     */
    private final BetonQuestLogger log;

    /**
     * The optional {@link VariableString} that stores the name of the train.
     */
    private final VariableString name;

    /**
     * The constructor takes an Instruction object as a parameter and throws an InstructionParseException.
     *
     * @param instruction the Instruction object to be used in the constructor
     * @throws InstructionParseException if there is an error while parsing the instruction
     */
    public TrainCartsExitObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());

        final QuestPackage pack = instruction.getPackage();
        this.name = new VariableString(BetonQuest.getInstance().getVariableProcessor(), pack, instruction.getOptional("name", ""));
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
        final OnlineProfile onlineProfile = PlayerConverter.getID(player);
        if (!containsPlayer(onlineProfile) || !checkConditions(onlineProfile)) {
            return;
        }
        if (TrainCartsUtils.isValidTrain(log, instruction.getPackage(), instruction.getID(), onlineProfile, name,
                event.getMember().getGroup().getProperties().getTrainName())) {
            completeObjective(onlineProfile);
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
