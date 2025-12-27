package org.betonquest.betonquest.quest.objective.delay;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.NumberParser;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.ObjectiveData;
import org.betonquest.betonquest.api.quest.objective.ObjectiveDataFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.config.PluginMessage;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * Player has to wait specified amount of time. He may logout, the objective
 * will be completed as soon as the time is up and he logs in again.
 */
public class DelayObjective extends Objective {

    /**
     * The Factory for the Delay Data.
     */
    private static final ObjectiveDataFactory DELAY_FACTORY = DelayData::new;

    /**
     * The delay time in seconds, minutes, or ticks.
     */
    private final Argument<Number> delay;

    /**
     * The runnable task that checks the delay.
     */
    private final BukkitTask runnable;

    /**
     * Constructor for the DelayObjective.
     *
     * @param instruction the instruction that created this objective
     * @param interval    the interval in ticks at which the objective checks if the time is up
     * @param delay       the delay time in seconds, minutes, or ticks
     * @throws QuestException if there is an error in the instruction
     */
    public DelayObjective(final Instruction instruction, final Argument<Number> interval,
                          final Argument<Number> delay) throws QuestException {
        super(instruction, DELAY_FACTORY);
        this.delay = delay;
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                final List<Profile> players = new LinkedList<>();
                final long time = System.currentTimeMillis();
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
        }.runTaskTimer(BetonQuest.getInstance(), 0, interval.getValue(null).longValue());
    }

    private double timeToMilliSeconds(final Profile profile, final double time) throws QuestException {
        final boolean ticks = instruction.bool().getFlag("ticks", false)
                .getValue(profile).orElse(false);
        if (ticks) {
            return time * 50;
        }
        final boolean seconds = instruction.bool().getFlag("seconds", false)
                .getValue(profile).orElse(false);
        if (seconds) {
            return time * 1000;
        }
        return time * 1000 * 60;
    }

    @Override
    public void close() {
        runnable.cancel();
        super.close();
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) throws QuestException {
        final double millis = timeToMilliSeconds(profile, delay.getValue(profile).doubleValue());
        return Double.toString(System.currentTimeMillis() + millis);
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "left" ->
                    qeHandler.handle(() -> LegacyComponentSerializer.legacySection().serialize(parseVariableLeft(profile)), "");
            case "date" -> parseVariableDate(profile);
            case "rawseconds" -> parseVariableRawSeconds(profile);
            default -> "";
        };
    }

    private Component parseVariableLeft(final Profile profile) throws QuestException {
        final PluginMessage pluginMessage = BetonQuest.getInstance().getPluginMessage();
        final Component daysWord = pluginMessage.getMessage(profile, "days");
        final Component daysWordSingular = pluginMessage.getMessage(profile, "days_singular");
        final Component hoursWord = pluginMessage.getMessage(profile, "hours");
        final Component hoursWordSingular = pluginMessage.getMessage(profile, "hours_singular");
        final Component minutesWord = pluginMessage.getMessage(profile, "minutes");
        final Component minutesWordSingular = pluginMessage.getMessage(profile, "minutes_singular");
        final Component secondsWord = pluginMessage.getMessage(profile, "seconds");
        final Component secondsWordSingular = pluginMessage.getMessage(profile, "seconds_singular");

        final long endTimestamp = (long) getDelayData(profile).getTime();
        final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTimestamp), ZoneId.systemDefault());
        final Duration duration = Duration.between(LocalDateTime.now(), end);

        final TextComponent.Builder builder = Component.text();
        buildTimeDescription(builder, daysWord, daysWordSingular, duration.toDaysPart());
        buildTimeDescription(builder, hoursWord, hoursWordSingular, duration.toHoursPart());
        buildTimeDescription(builder, minutesWord, minutesWordSingular, duration.toMinutesPart());
        buildTimeDescription(builder, secondsWord, secondsWordSingular, duration.toSecondsPart());

        return builder.build();
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private void buildTimeDescription(final TextComponent.Builder builder, final Component timeUnitWord, final Component timeUnitSingularWord, final long timeAmount) {
        if (!builder.children().isEmpty()) {
            builder.append(Component.space());
        }
        if (timeAmount > 1) {
            builder.append(Component.text(timeAmount)).append(Component.space()).append(timeUnitWord);
        } else if (timeAmount == 1) {
            builder.append(Component.text(timeAmount)).append(Component.space()).append(timeUnitSingularWord);
        }
    }

    private String parseVariableDate(final Profile profile) {
        final String pattern = BetonQuest.getInstance().getPluginConfig().getString("date_format", "");
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.ROOT);
        final long millis = (long) getDelayData(profile).getTime();
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(formatter);
    }

    private String parseVariableRawSeconds(final Profile profile) {
        final double timeLeft = getDelayData(profile).getTime() - System.currentTimeMillis();
        return String.valueOf(timeLeft / 1000);
    }

    /**
     * Get the delay data for a profile.
     *
     * @throws NullPointerException when {@link #containsPlayer(Profile)} is false
     */
    private DelayData getDelayData(final Profile profile) {
        return Objects.requireNonNull((DelayData) dataMap.get(profile));
    }

    /**
     * Data class for the DelayObjective.
     */
    public static class DelayData extends ObjectiveData {

        /**
         * The timestamp when the delay is over.
         */
        private final double timestamp;

        /**
         * Constructor for the DelayData.
         *
         * @param instruction the data of the objective
         * @param profile     the profile associated with this objective
         * @param objID       the ID of the objective
         * @throws QuestException when the instruction could not be parsed as number
         */
        public DelayData(final String instruction, final Profile profile, final ObjectiveID objID) throws QuestException {
            super(instruction, profile, objID);
            timestamp = NumberParser.DEFAULT.apply(instruction).doubleValue();
        }

        private double getTime() {
            return timestamp;
        }
    }
}
