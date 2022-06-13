package org.betonquest.betonquest.modules.schedule.impl.gametime;

import org.betonquest.betonquest.api.schedule.CronSchedule;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.modules.schedule.ScheduleID;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class GametimeSchedule extends CronSchedule {

    /**
     * Server instances for retrieving the worlds
     */
    private static final Server SERVER = Bukkit.getServer();

    /**
     * The world that provides the time for this schedule.
     */
    private final World world;

    /**
     * List of skip reasons.
     * If the game time was skipped for one of the reasons from this list, no catchup will be performed.
     */
    private final List<GametimeSkipReason> skip = new ArrayList<>();

    public GametimeSchedule(ScheduleID scheduleId, ConfigurationSection instruction) throws InstructionParseException {
        super(scheduleId, instruction, DEFAULT_CRON_DEFINITION, false);

        final String worldName = Optional.ofNullable(instruction.getString("options.world"))
                .orElseThrow(() -> new InstructionParseException("World not set!"));
        world = Optional.ofNullable(SERVER.getWorld(worldName))
                .orElseThrow(() -> new InstructionParseException("World '" + worldName + "' not found!"));

        for (final String skipReason : instruction.getStringList("options.skip")) {
            try {
                skip.add(GametimeSkipReason.valueOf(skipReason.trim().toUpperCase(Locale.ROOT)));
            } catch (final IllegalArgumentException e) {
                throw new IllegalArgumentException("'" + skipReason + "' is not a valid skip reason!");
            }
        }
    }

    /**
     * Get the world that provides the time for this schedule.
     *
     * @return the schedules world
     */
    public World getWorld() {
        return world;
    }

    /**
     * If the game time was skipped for one of the reasons from this list, no catchup will be performed.
     *
     * @return the list of skip reasons
     */
    public List<GametimeSkipReason> getSkip() {
        return Collections.unmodifiableList(skip);
    }
}
