package org.betonquest.betonquest.compatibility.traincarts.objectives;

import com.bergerkiller.bukkit.tc.events.seat.MemberSeatEnterEvent;
import com.bergerkiller.bukkit.tc.events.seat.MemberSeatExitEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.compatibility.traincarts.TrainCartsUtils;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This {@link CountingObjective} is completed when a player rides a train for a certain amount of time.
 * The scripter can specify the train name and the time in seconds the player has to ride the train.
 * If the train name is not specified, the {@link CountingObjective} will be completed when the player rides any train.
 */
public class TrainCartsRideObjective extends CountingObjective implements Listener {
    /**
     * The conversion factor from milliseconds to seconds.
     */
    private static final long MILLISECONDS_TO_SECONDS = 1000L;

    /**
     * The {@link BetonQuestLogger} for logging.
     */
    private final BetonQuestLogger log;

    /**
     * The {@link Map} that stores the current amount of time the player has ridden the train.
     */
    private final Map<UUID, Pair<Long, BukkitTask>> startTimes;

    /**
     * The {@link VariableString} that stores the optional name of the train.
     */
    private final VariableString name;

    /**
     * Creates a new {@link TrainCartsRideObjective} from the given instruction.
     *
     * @param instruction the user-provided instruction
     * @throws QuestException if the instruction is invalid
     */
    public TrainCartsRideObjective(final Instruction instruction) throws QuestException {
        super(instruction);
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
        this.startTimes = new HashMap<>();

        final QuestPackage pack = instruction.getPackage();
        this.name = new VariableString(BetonQuest.getInstance().getVariableProcessor(), pack, instruction.getOptional("name", ""));
        targetAmount = instruction.getVarNum(instruction.getOptional("amount", "0"), VariableNumber.NOT_LESS_THAN_ZERO_CHECKER);
    }

    /**
     * Checks if the player enters a train seat.
     * If the train name is not specified in the instruction,
     * the {@link CountingObjective} will be completed when the player rides any train.
     * The measurement is started if the player enters a train seat.
     *
     * @param event the {@link MemberSeatEnterEvent}.
     */
    @EventHandler
    public void onMemberSeatEnter(final MemberSeatEnterEvent event) {
        if (!(event.getEntity() instanceof final Player player)) {
            return;
        }
        final OnlineProfile onlineProfile = PlayerConverter.getID(player);
        if (!containsPlayer(onlineProfile) || !checkConditions(onlineProfile)) {
            return;
        }
        if (TrainCartsUtils.isValidTrain(log, instruction.getPackage(), instruction.getID(), onlineProfile, name,
                event.getMember().getGroup().getProperties().getTrainName())) {
            startCount(onlineProfile);
        }
    }

    /**
     * The {@link MemberSeatExitEvent} is used to stop the time measurement when the player exits the seat.
     *
     * @param event the {@link MemberSeatExitEvent}.
     */
    @EventHandler
    public void onMemberSeatExit(final MemberSeatExitEvent event) {
        if (!(event.getEntity() instanceof final Player player)) {
            return;
        }
        final OnlineProfile onlineProfile = PlayerConverter.getID(player);
        stopCount(onlineProfile);
    }

    /**
     * Stops the time measurement when the player quits the server.
     *
     * @param event the {@link PlayerQuitEvent}.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onQuit(final PlayerQuitEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        stopCount(onlineProfile);
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
        while (!startTimes.isEmpty()) {
            final Player player = Bukkit.getPlayer(startTimes.keySet().iterator().next());
            if (player != null) {
                final OnlineProfile profile = PlayerConverter.getID(player);
                stopCount(profile);
            }
        }
    }

    @Override
    public void stop(final Profile profile) {
        final Pair<Long, BukkitTask> remove = startTimes.remove(profile.getPlayerUUID());
        if (remove != null) {
            remove.getValue().cancel();
        }
    }

    private void startCount(final OnlineProfile onlineProfile) {
        final int ticksToCompletion = getCountingData(onlineProfile).getAmountLeft() * 20;
        final BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater(BetonQuest.getInstance(), () -> {
            stopCount(onlineProfile);
        }, ticksToCompletion);

        startTimes.put(onlineProfile.getPlayerUUID(), Pair.of(System.currentTimeMillis(), bukkitTask));
    }

    private void stopCount(final OnlineProfile onlineProfile) {
        if (!startTimes.containsKey(onlineProfile.getPlayerUUID())) {
            return;
        }
        final Pair<Long, BukkitTask> remove = startTimes.remove(onlineProfile.getPlayerUUID());
        remove.getValue().cancel();
        if (!checkConditions(onlineProfile)) {
            return;
        }
        final int ridden = (int) ((System.currentTimeMillis() - remove.getKey()) / MILLISECONDS_TO_SECONDS);
        final CountingData countingData = getCountingData(onlineProfile);
        countingData.add(ridden);
        if (countingData.isComplete()) {
            completeObjective(onlineProfile);
        }
    }
}
