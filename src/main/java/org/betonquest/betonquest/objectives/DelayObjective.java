package org.betonquest.betonquest.objectives;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map.Entry;

/**
 * Player has to wait specified amount of time. He may logout, the objective
 * will be completed as soon as the time is up and he logs in again.
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class DelayObjective extends Objective {

    private final int interval;
    private VariableNumber delay;
    private BukkitTask runnable;

    public DelayObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = DelayData.class;

        parseDelay();
        interval = instruction.getInt(instruction.getOptional("interval"), 20 * 10);
        if (interval <= 0) {
            throw new InstructionParseException("Interval cannot be less than 1 tick");
        }
    }

    private void parseDelay() throws InstructionParseException {
        final String doubleOrVar = instruction.next();
        if (doubleOrVar.startsWith("%")) {
            delay = new VariableNumber(instruction.getPackage().getPackagePath(), doubleOrVar);
        } else {
            final double time = Double.parseDouble(doubleOrVar);
            if (time < 0) {
                throw new InstructionParseException("Error in delay objective '" + instruction.getID() + "': Delay cannot be less than 0");
            }
            delay = new VariableNumber(time);
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
                final LinkedList<String> players = new LinkedList<>();
                final long time = new Date().getTime();
                for (final Entry<String, ObjectiveData> entry : dataMap.entrySet()) {
                    final String playerID = entry.getKey();
                    final DelayData playerData = (DelayData) entry.getValue();
                    if (time >= playerData.getTime() && checkConditions(playerID)) {
                        // don't complete the objective, it will throw CME/
                        // store the player instead, complete later
                        players.add(playerID);
                    }
                }
                for (final String playerID : players) {
                    completeObjective(playerID);
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
        return null;
    }

    @Override
    public String getDefaultDataInstruction(final String playerID) {
        double millis = 0;
        try {
            final double time = delay.getDouble(playerID);
            millis = timeToMilliSeconds(time);
        } catch (final InstructionParseException | QuestRuntimeException e) {
            LOG.warn("Error in delay objective '" + instruction.getID() + "': " + e.getMessage());
        }
        return Double.toString(new Date().getTime() + millis);
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    @Override
    public String getProperty(final String name, final String playerID) {
        return switch (name.toUpperCase(Locale.ROOT)) {
            case "LEFT" -> parseVariableLeft(playerID);
            case "DATE" -> parseVariableDate(playerID);
            case "RAWSECONDS" -> parseVariableRawSeconds(playerID);
            default -> "";
        };
    }

    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.AvoidLiteralsInIfCondition"})
    @NotNull
    private String parseVariableLeft(final String playerID) {
        final String lang = BetonQuest.getInstance().getPlayerData(playerID).getLanguage();
        final String daysWord = Config.getMessage(lang, "days");
        final String daysWordSingular = Config.getMessage(lang, "days_singular");
        final String hoursWord = Config.getMessage(lang, "hours");
        final String hoursWordSingular = Config.getMessage(lang, "hours_singular");
        final String minutesWord = Config.getMessage(lang, "minutes");
        final String minutesWordSingular = Config.getMessage(lang, "minutes_singular");
        final String secondsWord = Config.getMessage(lang, "seconds");
        final String secondsWordSingular = Config.getMessage(lang, "seconds_singular");

        final long endTimestamp = (long) ((DelayData) dataMap.get(playerID)).getTime();
        final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTimestamp), ZoneId.systemDefault());
        final Duration duration = Duration.between(LocalDateTime.now(), end);

        final String days = buildTimeDescription(daysWord, daysWordSingular, duration.toDaysPart());
        final String hours = buildTimeDescription(hoursWord, hoursWordSingular, duration.toHoursPart());
        final String minutes = buildTimeDescription(minutesWord, minutesWordSingular, duration.toMinutesPart());
        final String seconds = buildTimeDescription(secondsWord, secondsWordSingular, duration.toSecondsPart());
        return days + hours + minutes + seconds;
    }


    private String buildTimeDescription(final String timeUnitWord, final String timeUnitSingularWord, final long timeAmount) {
        return timeAmount >= 1 ? timeAmount + " " + (timeAmount == 1 ? timeUnitSingularWord : timeUnitWord + " ") : "";
    }

    private String parseVariableDate(final String playerID) {
        return new SimpleDateFormat(Config.getString("config.date_format"), Locale.ROOT)
                .format(new Date((long) ((DelayData) dataMap.get(playerID)).getTime()));
    }

    private String parseVariableRawSeconds(final String playerID) {
        final double timeLeft = ((DelayData) dataMap.get(playerID)).getTime() - new Date().getTime();
        return String.valueOf(timeLeft / 1000);
    }

    public static class DelayData extends ObjectiveData {

        private final double timestamp;

        public DelayData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            timestamp = Double.parseDouble(instruction);
        }

        private double getTime() {
            return timestamp;
        }

    }
}
