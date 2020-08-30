/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.objectives;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map.Entry;

/**
 * Player has to wait specified amount of time. He may logout, the objective
 * will be completed as soon as the time is up and he logs in again.
 *
 * @author Jakub Sapalski
 */
public class DelayObjective extends Objective {

    private final double delay;
    private BukkitTask runnable;
    private int interval;

    public DelayObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = DelayData.class;
        if (instruction.hasArgument("ticks")) {
            delay = instruction.getDouble() * 50;
        } else if (instruction.hasArgument("seconds")) {
            delay = instruction.getDouble() * 1000;
        } else {
            delay = instruction.getDouble() * 1000 * 60;
        }
        if (delay < 0) {
            throw new InstructionParseException("Delay cannot be less than 0");
        }
        interval = instruction.getInt(instruction.getOptional("interval"), 20 * 10);
        if (interval < 1) {
            throw new InstructionParseException("Interval cannot be less than 1 tick");
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
        return Long.toString(new Date().getTime() + (long) delay);
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        if (name.equalsIgnoreCase("left")) {
            final String lang = BetonQuest.getInstance().getPlayerData(playerID).getLanguage();
            final String daysWord = Config.getMessage(lang, "days");
            final String hoursWord = Config.getMessage(lang, "hours");
            final String minutesWord = Config.getMessage(lang, "minutes");
            final String secondsWord = Config.getMessage(lang, "seconds");
            final long timeLeft = ((DelayData) dataMap.get(playerID)).getTime() - new Date().getTime();
            final long seconds = (timeLeft / (1000)) % 60;
            final long minutes = (timeLeft / (1000 * 60)) % 60;
            final long hours = (timeLeft / (1000 * 60 * 60)) % 24;
            final long days = timeLeft / (1000 * 60 * 60 * 24);
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
                time.append(seconds + " " + secondsWord);
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
                        time.append(" " + word);
                    } else {
                        time.append(word + " " + Config.getMessage(lang, "and"));
                        second = true;
                    }
                }
            } else {
                time.append(words[0] + ", " + words[1] + " " + Config.getMessage(lang, "and ") + words[2]);
            }
            return time.toString();
        } else if (name.equalsIgnoreCase("date")) {
            return new SimpleDateFormat(Config.getString("config.date_format"))
                    .format(new Date(((DelayData) dataMap.get(playerID)).getTime()));
        }
        return "";
    }

    public static class DelayData extends ObjectiveData {

        private final long timestamp;

        public DelayData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            timestamp = Long.parseLong(instruction);
        }

        private long getTime() {
            return timestamp;
        }

    }
}
