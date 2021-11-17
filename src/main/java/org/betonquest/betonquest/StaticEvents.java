package org.betonquest.betonquest;

import lombok.CustomLog;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.EventID;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * StaticEvents contains logic for running events that aren't tied to any player
 */
@CustomLog
public class StaticEvents {

    /**
     * Contains pointers to timers, so they can be canceled if needed
     */
    private static final List<EventTimer> TIMERS = new ArrayList<>();

    /**
     * Creates new instance of a StaticEvents object, scheduling static events
     * to run at specified times
     */
    public StaticEvents() {
        LOG.debug("Initializing static events");
        // old timers need to be deleted in case of reloading the plugin
        boolean deleted = false;
        for (final EventTimer eventTimer : TIMERS) {
            eventTimer.cancel();
            deleted = true;
        }
        if (deleted) {
            LOG.debug("Previous timers has been canceled");
        }
        for (final ConfigPackage pack : Config.getPackages().values()) {
            final String packName = pack.getName();
            LOG.debug(pack, "Searching package " + packName);
            // get those hours and events
            final ConfigurationSection config = pack.getMain().getConfig().getConfigurationSection("static");
            if (config == null) {
                LOG.debug(pack, "There are no static events defined, skipping");
                continue;
            }
            // for each hour, create an event timer
            for (final String key : config.getKeys(false)) {
                final String value = config.getString(key);
                final long timeStamp = getTimestamp(key);
                if (timeStamp < 0) {
                    LOG.warning(pack, "Incorrect time value in static event declaration (" + key + "), skipping this one");
                    continue;
                }
                LOG.debug(pack, "Scheduling static events " + value + " at hour " + key + ". Current timestamp: "
                        + new Date().getTime() + ", target timestamp: " + timeStamp);
                try {
                    final String[] events = value.split(",");
                    final EventID[] eventIDS = new EventID[events.length];
                    for (int i = 0; i < events.length; i++) {
                        eventIDS[i] = new EventID(pack, events[i]);
                    }
                    TIMERS.add(new EventTimer(timeStamp, eventIDS));
                } catch (final ObjectNotFoundException e) {
                    LOG.warning(pack, "Could not load static event '" + packName + "." + key + "': " + e.getMessage(), e);
                }
            }
        }
        LOG.debug("Static events initialization done");
    }

    /**
     * Cancels all scheduled timers
     */
    public static void stop() {
        LOG.debug("Killing all timers on disable");
        for (final EventTimer timer : TIMERS) {
            timer.cancel();
        }
    }

    /**
     * Generates a timestamp closest to the specified hour
     *
     * @param hour time of the day
     * @return timestamp representing next occurence of specified hour
     */
    private long getTimestamp(final String hour) {
        // get the current day and add the given hour to it
        final Date time = new Date();
        final String timeString = new SimpleDateFormat("dd.MM.yy", Locale.ROOT).format(time) + " " + hour;
        // convert it into a timestamp
        long timeStamp = -1;
        try {
            timeStamp = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.ROOT).parse(timeString).getTime();
        } catch (final ParseException e) {
            LOG.warning("Error in time setting in static event declaration: " + hour, e);
        }
        // if the timestamp is too old, add one day to it
        if (timeStamp < new Date().getTime()) {
            timeStamp += 24 * 60 * 60 * 1000;
        }
        return timeStamp;
    }

    /**
     * EventTimer represents a timer for an event
     */
    @SuppressWarnings("PMD.CommentRequired")
    private static class EventTimer extends TimerTask {

        protected final EventID[] event;

        /**
         * Creates and schedules a new timer for specified event, based on given
         * timeStamp
         *
         * @param timeStamp
         * @param eventID
         */
        public EventTimer(final long timeStamp, final EventID... eventID) {
            super();
            event = eventID == null ? null : Arrays.copyOf(eventID, eventID.length);
            new Timer().schedule(this, timeStamp - new Date().getTime(), 24 * 60 * 60 * 1000);
        }

        @Override
        public void run() {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (final EventID eventID : event) {
                        BetonQuest.event(null, eventID);
                    }
                }
            }.runTask(BetonQuest.getInstance());
        }
    }

}
