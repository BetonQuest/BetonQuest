package org.betonquest.betonquest.compatibility.traincarts.objectives;

import com.bergerkiller.bukkit.tc.events.seat.MemberSeatEnterEvent;
import com.bergerkiller.bukkit.tc.events.seat.MemberSeatExitEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This {@link CountingObjective} is completed when a player rides a train for a certain amount of time.
 * The scripter can specify the train name and the time in seconds the player has to ride the train.
 * If the train name is not specified, the {@link CountingObjective} will be completed when the player rides any train.
 */
public class TrainCartsRideObjective extends CountingObjective implements Listener {
    /**
     * The number of ticks per second.
     */
    private static final long TICKS_PER_SECOND = 20L;

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
    private final Map<OnlineProfile, Long> currentAmount;

    /**
     * The {@link VariableString} that stores the optional name of the train.
     */
    private final VariableString name;

    /**
     * The task ID of the Bukkit scheduler.
     */
    private int taskId;

    /**
     * Creates a new {@link TrainCartsRideObjective} from the given instruction.
     *
     * @param instruction the user-provided instruction
     * @throws InstructionParseException if the instruction is invalid
     */
    public TrainCartsRideObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
        this.currentAmount = new HashMap<>();

        final QuestPackage pack = instruction.getPackage();
        this.name = new VariableString(BetonQuest.getInstance().getVariableProcessor(), pack, instruction.getOptional("name", ""));
        final String secondsToRideString = instruction.getOptional("amount", "0");
        final long parsedSecondsToRide = instruction.getLong(secondsToRideString, 0) * MILLISECONDS_TO_SECONDS;
        targetAmount = instruction.getVarNum(String.valueOf(parsedSecondsToRide), VariableNumber.NOT_LESS_THAN_ZERO_CHECKER);
    }

    /**
     * Checks if the player enters a train seat.
     * If the train name is not specified in the instruction,
     * the {@link CountingObjective} will be completed when the player rides any train.
     * The time counter will be started when the player enters the seat.
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
        final String nameFromInstruction = resolveVariableString(onlineProfile);
        if (nameFromInstruction.isEmpty()) {
            startCount(onlineProfile);
            return;
        }
        final String trainName = event.getMember().getGroup().getProperties().getTrainName();
        if (nameFromInstruction.equalsIgnoreCase(trainName)) {
            startCount(onlineProfile);
        }
    }

    /**
     * The time counter will be stopped for the specific player when he exits the seat.
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

    @Override
    public void start() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(BetonQuest.getInstance(), this::repeatedCountTask, 0L, TICKS_PER_SECOND);
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        Bukkit.getScheduler().cancelTask(taskId);
        HandlerList.unregisterAll(this);

        final List<OnlineProfile> profilesToRemove = new ArrayList<>(currentAmount.keySet());
        profilesToRemove.forEach(this::stopCount);
    }

    private void startCount(final OnlineProfile onlineProfile) {
        currentAmount.put(onlineProfile, System.currentTimeMillis());
    }

    private void stopCount(@NotNull final OnlineProfile onlineProfile) {
        if (!currentAmount.containsKey(onlineProfile)) {
            return;
        }
        currentAmount.remove(onlineProfile);
    }

    private void repeatedCountTask() {
        for (final Map.Entry<OnlineProfile, Long> onlineProfileLongEntry : currentAmount.entrySet()) {
            final OnlineProfile onlineProfile = onlineProfileLongEntry.getKey();
            final long lastTimeEntry = onlineProfileLongEntry.getValue();
            final long currentTime = System.currentTimeMillis();
            final int totalTime = Math.toIntExact(currentTime - lastTimeEntry);
            final CountingData countingData = getCountingData(onlineProfile);
            currentAmount.put(onlineProfile, currentTime);
            countingData.add(totalTime);

            if (countingData.isComplete()) {
                completeObjective(onlineProfile);
            }
        }
    }

    private String resolveVariableString(final OnlineProfile onlineProfile) {
        String result = "";
        try {
            result = name.getValue(onlineProfile);
        } catch (final QuestRuntimeException e) {
            log.warn("Failed to resolve variable string.", e);
        }
        return result;
    }
}
