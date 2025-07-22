package org.betonquest.betonquest.quest;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Data holder for the required server, scheduler and plugin to execute the quest type logic
 * on the primary server thread by {@link PrimaryServerThreadType}.
 *
 * @param server    The Server to use to determine if currently on the primary server thread.
 * @param scheduler The Scheduler for scheduling the quest type action on the primary server thread.
 * @param plugin    The Plugin to associate the scheduled task with.
 */
public record PrimaryServerThreadData(Server server, BukkitScheduler scheduler, Plugin plugin) {
}
