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

import java.text.SimpleDateFormat;
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
        final String intOrVar = instruction.next();
        if (intOrVar.startsWith("%")) {
            delay = new VariableNumber(instruction.getPackage().getPackagePath(), intOrVar);
        } else {
            final int time = Integer.parseInt(intOrVar);
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

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.AvoidLiteralsInIfCondition", "PMD.CognitiveComplexity"})
    @Override
    public String getProperty(final String name, final String playerID) {
        if ("left".equalsIgnoreCase(name)) {
            final String lang = BetonQuest.getInstance().getPlayerData(playerID).getLanguage();
            final String daysWord = Config.getMessage(lang, "days");
            final String hoursWord = Config.getMessage(lang, "hours");
            final String minutesWord = Config.getMessage(lang, "minutes");
            final String secondsWord = Config.getMessage(lang, "seconds");
            final double timeLeft = ((DelayData) dataMap.get(playerID)).getTime() - new Date().getTime();
            final double seconds = (timeLeft / (1000)) % 60;
            final double minutes = (timeLeft / (1000 * 60)) % 60;
            final double hours = (timeLeft / (1000 * 60 * 60)) % 24;
            final double days = timeLeft / (1000 * 60 * 60 * 24);
            final StringBuilder time = new StringBuilder();
            final String[] words = new String[3];
            if (days > 0) {
                words[0] = days + " " + daysWord;
            }
            if (hours > 0) {
                words[1] = hours + " " + hoursWord;
            }
            if (minutes > 0) {
                words[2] = minutes + " " + minutesWord;
            }
            int count = 0;
            for (final String word : words) {
                if (word != null) {
                    count++;
                }
            }
            if (count == 0) {
                time.append(seconds).append(' ').append(secondsWord);
            } else if (count == 1) {
                for (final String word : words) {
                    if (word == null) {
                        continue;
                    }
                    time.append(word);
                }
            } else if (count == 2) {
                boolean second = false;
                for (final String word : words) {
                    if (word == null) {
                        continue;
                    }
                    if (second) {
                        time.append(' ').append(word);
                    } else {
                        time.append(word).append(' ').append(Config.getMessage(lang, "and"));
                        second = true;
                    }
                }
            } else {
                time.append(words[0]).append(", ").append(words[1]).append(' ').append(Config.getMessage(lang, "and")).append(' ').append(words[2]);
            }
            return time.toString();
        } else if ("date".equalsIgnoreCase(name)) {
            return new SimpleDateFormat(Config.getString("config.date_format"), Locale.ROOT)
                    .format(new Date((long) ((DelayData) dataMap.get(playerID)).getTime()));
        }
        return "";
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
