package org.betonquest.betonquest;

import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.utils.LogUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * StaticEvents contains logic for running events that aren't tied to any player
 */
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
        LogUtils.getLogger().log(Level.FINE, "Initializing static events");
        // old timers need to be deleted in case of reloading the plugin
        boolean deleted = false;
        for (final EventTimer eventTimer : TIMERS) {
            eventTimer.cancel();
            deleted = true;
        }
        if (deleted) {
            LogUtils.getLogger().log(Level.FINE, "Previous timers has been canceled");
        }
        for (final ConfigPackage pack : Config.getPackages().values()) {
            final String packName = pack.getName();
            LogUtils.getLogger().log(Level.FINE, "Searching package " + packName);
            // get those hours and events
            final ConfigurationSection config = pack.getMain().getConfig().getConfigurationSection("static");
            if (config == null) {
                LogUtils.getLogger().log(Level.FINE, "There are no static events defined, skipping");
                continue;
            }
            // for each hour, create an event timer
            for (final String key : config.getKeys(false)) {
                final String value = config.getString(key);
                final long timeStamp = getTimestamp(key);
                if (timeStamp < 0) {
                    LogUtils.getLogger().log(Level.WARNING, "Incorrect time value in static event declaration (" + key + "), skipping this one");
                    continue;
                }
                LogUtils.getLogger().log(Level.FINE, "Scheduling static events " + value + " at hour " + key + ". Current timestamp: "
                        + new Date().getTime() + ", target timestamp: " + timeStamp);
                try {
                    final String[] events = value.split(",");
                    final EventID[] eventIDS = new EventID[events.length];
                    for (int i = 0; i < events.length; i++) {
                        eventIDS[i] = new EventID(pack, events[i]);
                    }
                    TIMERS.add(new EventTimer(timeStamp, eventIDS));
                } catch (final ObjectNotFoundException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Could not load static event '" + packName + "." + key + "': " + e.getMessage());
                    LogUtils.logThrowable(e);
                }
            }
        }
        LogUtils.getLogger().log(Level.FINE, "Static events initialization done");
    }

    /**
     * Cancels all scheduled timers
     */
    public static void stop() {
        LogUtils.getLogger().log(Level.FINE, "Killing all timers on disable");
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
            LogUtils.getLogger().log(Level.WARNING, "Error in time setting in static event declaration: " + hour);
            LogUtils.logThrowable(e);
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
