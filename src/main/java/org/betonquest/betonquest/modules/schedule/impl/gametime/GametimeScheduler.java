package org.betonquest.betonquest.modules.schedule.impl.gametime;

import org.betonquest.betonquest.api.schedule.Scheduler;
import org.betonquest.betonquest.modules.schedule.ScheduleID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.betonquest.betonquest.modules.schedule.impl.gametime.MinecraftTimeUtils.ZONE_OFFSET;
import static org.betonquest.betonquest.modules.schedule.impl.gametime.MinecraftTimeUtils.durationToTicks;
import static org.betonquest.betonquest.modules.schedule.impl.gametime.MinecraftTimeUtils.worldTime;

public class GametimeScheduler extends Scheduler<GametimeSchedule> implements Listener {

    private final Plugin plugin;

    private final Map<ScheduleID, BukkitTask> tasks = new HashMap<>();

    public GametimeScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void start() {
        super.start();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        schedules.values().forEach(this::schedule);
    }

    private void schedule(final GametimeSchedule schedule) {
        final Instant worldTime = worldTime(schedule.getWorld());
        final Optional<Instant> nextRun = schedule.getExecutionTime().nextExecution(worldTime.atZone(ZONE_OFFSET)).map(Instant::from);
        if (nextRun.isPresent()) {
            final long ticks = durationToTicks(Duration.between(worldTime, nextRun.get()));
            final BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                tasks.remove(schedule.getId());
                executeEvents(schedule);
                schedule(schedule);
            }, ticks);
            tasks.put(schedule.getId(), task);
        }
    }

    @EventHandler //todo set priority
    public void onTimeSkipEvent(final TimeSkipEvent event) {
        if (event.isCancelled()) {
            return;
        }
        for (final GametimeSchedule schedule : schedules.values()) {
            if (schedule.getSkip().stream().map(GametimeSkipReason::toBukkit).anyMatch(event.getSkipReason()::equals)) {
                //todo catchup
            }
            //todo reschedule
        }
    }

    @Override
    public void stop() {
        super.stop();

        HandlerList.unregisterAll(this);
        tasks.values().forEach(BukkitTask::cancel);
        tasks.clear();
    }
}
