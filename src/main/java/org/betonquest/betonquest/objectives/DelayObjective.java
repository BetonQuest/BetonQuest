package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Player has to wait specified amount of time. He may logout, the objective
 * will be completed as soon as the time is up and he logs in again.
 */
@SuppressWarnings("PMD.CommentRequired")
public class DelayObjective extends Objective {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    private final int interval;

    private final VariableNumber delay;

    @Nullable
    private BukkitTask runnable;

    public DelayObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        log = BetonQuest.getInstance().getLoggerFactory().create(this.getClass());
        template = DelayData.class;

        delay = parseDelay();
        interval = instruction.getInt(instruction.getOptional("interval"), 20 * 10);
        if (interval <= 0) {
            throw new InstructionParseException("Interval cannot be less than 1 tick");
        }
    }

    private VariableNumber parseDelay() throws InstructionParseException {
        final String doubleOrVar = instruction.next();
        if (doubleOrVar.startsWith("%")) {
            return new VariableNumber(instruction.getPackage(), doubleOrVar);
        } else {
            final double time = Double.parseDouble(doubleOrVar);
            if (time < 0) {
                throw new InstructionParseException("Error in delay objective '" + instruction.getID() + "': Delay cannot be less than 0");
            }
            return new VariableNumber(time);
        }
    }

    private double timeToMilliSeconds(final double time) throws InstructionParseException {
        if (time < 0) {
            throw new InstructionParseException("Delay cannot be less than 0");
        }
        if (instruction.hasArgument("ticks")) {
            return time * 50;
        } else if (instruction.hasArgument("seconds")) {
            return time * 1000;
        } else {
            return time * 1000 * 60;
        }
    }

    @Override
    public void start() {
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                final LinkedList<Profile> players = new LinkedList<>();
                final long time = new Date().getTime();
                for (final Entry<Profile, ObjectiveData> entry : dataMap.entrySet()) {
                    final Profile profile = entry.getKey();
                    final DelayData playerData = (DelayData) entry.getValue();
                    if (time >= playerData.getTime() && checkConditions(profile)) {
                        // don't complete the objective, it will throw CME/
                        // store the player instead, complete later
                        players.add(profile);
                    }
                }
                for (final Profile profile : players) {
                    completeObjective(profile);
                }
            }
        }.runTaskTimer(BetonQuest.getInstance(), 0, interval);
    }

    @Override
    public void stop() {
        if (runnable != null) {
            runnable.cancel();
        }
    }

    @Override
    public String getDefaultDataInstruction() {
        //Empty to satisfy bad API needs
        return "";
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        double millis = 0;
        try {
            final double time = delay.getDouble(profile);
            millis = timeToMilliSeconds(time);
        } catch (final InstructionParseException e) {
            log.warn("Error in delay objective '" + instruction.getID() + "': " + e.getMessage());
        }
        return Double.toString(new Date().getTime() + millis);
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    @Override
    public String getProperty(final String name, final Profile profile) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "left" -> parseVariableLeft(profile);
            case "date" -> parseVariableDate(profile);
            case "rawseconds" -> parseVariableRawSeconds(profile);
            default -> "";
        };
    }

    @NotNull
    private String parseVariableLeft(final Profile profile) {
        final String lang = BetonQuest.getInstance().getPlayerData(profile).getLanguage();
        final String daysWord = Config.getMessage(lang, "days");
        final String daysWordSingular = Config.getMessage(lang, "days_singular");
        final String hoursWord = Config.getMessage(lang, "hours");
        final String hoursWordSingular = Config.getMessage(lang, "hours_singular");
        final String minutesWord = Config.getMessage(lang, "minutes");
        final String minutesWordSingular = Config.getMessage(lang, "minutes_singular");
        final String secondsWord = Config.getMessage(lang, "seconds");
        final String secondsWordSingular = Config.getMessage(lang, "seconds_singular");

        final long endTimestamp = (long) ((DelayData) dataMap.get(profile)).getTime();
        final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTimestamp), ZoneId.systemDefault());
        final Duration duration = Duration.between(LocalDateTime.now(), end);

        final String days = buildTimeDescription(daysWord, daysWordSingular, duration.toDaysPart());
        final String hours = buildTimeDescription(hoursWord, hoursWordSingular, duration.toHoursPart());
        final String minutes = buildTimeDescription(minutesWord, minutesWordSingular, duration.toMinutesPart());
        final String seconds = buildTimeDescription(secondsWord, secondsWordSingular, duration.toSecondsPart());

        return Stream.of(days, hours, minutes, seconds)
                .filter(word -> !word.isEmpty())
                .collect(Collectors.joining(" "));
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private String buildTimeDescription(final String timeUnitWord, final String timeUnitSingularWord, final long timeAmount) {
        if (timeAmount > 1) {
            return timeAmount + " " + timeUnitWord;
        } else if (timeAmount == 1) {
            return timeAmount + " " + timeUnitSingularWord;
        } else {
            return "";
        }
    }

    private String parseVariableDate(final Profile profile) {
        return new SimpleDateFormat(Config.getString("config.date_format"), Locale.ROOT)
                .format(new Date((long) ((DelayData) dataMap.get(profile)).getTime()));
    }

    private String parseVariableRawSeconds(final Profile profile) {
        final double timeLeft = ((DelayData) dataMap.get(profile)).getTime() - new Date().getTime();
        return String.valueOf(timeLeft / 1000);
    }

    public static class DelayData extends ObjectiveData {

        private final double timestamp;

        public DelayData(final String instruction, final Profile profile, final String objID) {
            super(instruction, profile, objID);
            timestamp = Double.parseDouble(instruction);
        }

        private double getTime() {
            return timestamp;
        }

    }
}
