package org.betonquest.betonquest.compatibility.traincarts.objectives;

import com.bergerkiller.bukkit.tc.events.seat.MemberSeatEnterEvent;
import com.bergerkiller.bukkit.tc.events.seat.MemberSeatExitEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.compatibility.traincarts.TrainCartsUtils;
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
     * The {@link Map} that stores the current amount of time the player has ridden the train.
     */
    private final Map<UUID, Pair<Long, BukkitTask>> startTimes;

    /**
     * The name of the train, maybe empty.
     */
    private final Variable<String> name;

    /**
     * Creates a new {@link TrainCartsRideObjective} from the given instruction.
     *
     * @param instruction  the user-provided instruction
     * @param targetAmount the target amount of time in seconds
     * @param name         the name of the train, maybe empty
     * @throws QuestException if the instruction is invalid
     */
    public TrainCartsRideObjective(final Instruction instruction, final Variable<Number> targetAmount,
                                   final Variable<String> name) throws QuestException {
        super(instruction, targetAmount, null);
        this.name = name;
        this.startTimes = new HashMap<>();
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
        final OnlineProfile onlineProfile = profileProvider.getProfile(player);
        if (!containsPlayer(onlineProfile) || !checkConditions(onlineProfile)) {
            return;
        }
        qeHandler.handle(() -> {
            if (TrainCartsUtils.isValidTrain(name.getValue(onlineProfile),
                    event.getMember().getGroup().getProperties().getTrainName())) {
                startCount(onlineProfile);
            }
        });
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
        stopCount(profileProvider.getProfile(player));
    }

    /**
     * Stops the time measurement when the player quits the server.
     *
     * @param event the {@link PlayerQuitEvent}.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onQuit(final PlayerQuitEvent event) {
        stopCount(profileProvider.getProfile(event.getPlayer()));
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
                stopCount(profileProvider.getProfile(player));
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
        final BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater(BetonQuest.getInstance(),
                () -> stopCount(onlineProfile), ticksToCompletion);

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
