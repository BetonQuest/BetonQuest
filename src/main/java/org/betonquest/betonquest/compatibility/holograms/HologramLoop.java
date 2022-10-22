package org.betonquest.betonquest.compatibility.holograms;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.compatibility.holograms.lines.AbstractLine;
import org.betonquest.betonquest.compatibility.holograms.lines.ItemLine;
import org.betonquest.betonquest.compatibility.holograms.lines.StaticVariableTextLine;
import org.betonquest.betonquest.compatibility.holograms.lines.TextLine;
import org.betonquest.betonquest.compatibility.holograms.lines.TopLine;
import org.betonquest.betonquest.compatibility.holograms.lines.TopXObject;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hides and shows holograms to players, based on conditions.
 */
@CustomLog
public class HologramLoop {
    /**
     * Pattern to match the correct syntax for the top line content
     */
    private static final Pattern TOP_LINE_VALIDATOR = Pattern.compile("^top:([\\w.]+);(\\w+);(\\d+);?[&§]?([\\da-f])?;?[&§]?([\\da-f])?;?[&§]?([\\da-f])?;?[&§]?([\\da-f])?$", Pattern.CASE_INSENSITIVE);

    /**
     * Starts a loop, which checks hologram conditions and shows them to players.
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength", "PMD.NcssCount", "PMD.NPathComplexity", "PMD.CognitiveComplexity", "PMD.UseStringBufferForStringAppends"})
    public HologramLoop() {
        final int defaultInterval = BetonQuest.getInstance().getPluginConfig().getInt("hologram_update_interval", 10 * 20);
        // get all holograms and their condition

        for (final QuestPackage pack : Config.getPackages().values()) {
            final String packName = pack.getPackagePath();
            final ConfigurationSection section = pack.getConfig().getConfigurationSection("holograms");
            if (section == null) {
                continue;
            }
            for (final String key : section.getKeys(false)) {
                final List<String> lines = section.getStringList(key + ".lines");
                final String rawConditions = section.getString(key + ".conditions");
                final String rawLocation = section.getString(key + ".location");
                final int checkInterval = section.getInt(key + ".check_interval", defaultInterval);
                if (rawLocation == null) {
                    LOG.warn(pack, "Location is not specified in " + key + " hologram");
                    continue;
                }

                ConditionID[] conditions = {};
                if (rawConditions != null) {
                    final String[] parts = rawConditions.split(",");
                    conditions = new ConditionID[parts.length];
                    for (int i = 0; i < conditions.length; i++) {
                        try {
                            conditions[i] = new ConditionID(pack, parts[i]);
                        } catch (final ObjectNotFoundException e) {
                            LOG.warn(pack, "Error while loading " + parts[i] + " condition for hologram " + packName + "."
                                    + key + ": " + e.getMessage(), e);
                        }
                    }
                }

                final Location location;
                try {
                    location = new CompoundLocation(packName, pack.subst(rawLocation)).getLocation(null);
                } catch (QuestRuntimeException | InstructionParseException e) {
                    LOG.warn(pack, "Could not parse location in " + key + " hologram: " + e.getMessage(), e);
                    continue;
                }

                boolean staticText = true;
                final ArrayList<AbstractLine> cleanedLines = new ArrayList<>();
                for (final String line : lines) {
                    // If line begins with 'item:', then we will assume it is a
                    // floating item
                    if (line.startsWith("item:")) {
                        try {
                            final String[] args = line.substring(5).split(":");
                            final ItemID itemID = new ItemID(pack, args[0]);
                            int stackSize;
                            try {
                                stackSize = Integer.parseInt(args[1]);
                            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                                stackSize = 1;
                            }
                            final ItemStack stack = new QuestItem(itemID).generate(stackSize);
                            cleanedLines.add(new ItemLine(stack));
                        } catch (final InstructionParseException e) {
                            LOG.warn(pack, "Could not parse item in " + key + " hologram: " + e.getMessage(), e);
                        } catch (final ObjectNotFoundException e) {
                            LOG.warn(pack, "Could not find item in " + key + " hologram: " + e.getMessage(), e);
                        }
                    } else if (line.startsWith("top:")) {
                        final Matcher validator = TOP_LINE_VALIDATOR.matcher(line);
                        if (!validator.matches()) {
                            LOG.warn("Malformed top hologram line! Expected format: 'top:<point>;<order>;<limit>[;<color>][;<color>][;<color>][;<color>]'.");
                            continue;
                        }

                        String pointName = validator.group(1);
                        if (!pointName.contains(".")) {
                            pointName = packName + '.' + pointName;
                        }

                        final TopXObject.OrderType orderType;
                        if ("desc".equalsIgnoreCase(validator.group(2))) {
                            orderType = TopXObject.OrderType.DESCENDING;
                        } else if ("asc".equalsIgnoreCase(validator.group(2))) {
                            orderType = TopXObject.OrderType.ASCENDING;
                        } else { // other variants not checked in regex to give specific warning instead of generic malformed line warning
                            LOG.warn(pack, "Top list order type '" + validator.group(2) + "' unknown! Using descending order.");
                            orderType = TopXObject.OrderType.DESCENDING;
                        }

                        int limit;
                        try { // negative limits are checked by regex
                            limit = Integer.parseInt(validator.group(3));
                        } catch (final NumberFormatException e) {
                            LOG.warn(pack, "Top list limit must be numeric! Using limit 10.");
                            limit = 10;
                        }

                        final StringBuilder colorCodes = new StringBuilder();
                        for (int i = 4; i <= 7; i++) {
                            String code = "";
                            if (validator.group(i) != null) {
                                code = validator.group(i).toLowerCase(Locale.ROOT);
                            }

                            if ("".equals(code)) {
                                colorCodes.append('f');
                            } else {
                                colorCodes.append(code);
                            }
                        }
                        staticText = false;
                        cleanedLines.add(new TopLine(pointName, orderType, limit, colorCodes.toString().toCharArray()));
                    } else {
                        cleanedLines.add(parseTextLine(pack, line.replace('&', '§')));
                    }
                }

                final BetonHologram hologram = HologramIntegrator.createHologram(key, location);
                hologram.hideAll();
                HologramRunner.addHologram(new HologramWrapper(
                        checkInterval,
                        hologram,
                        staticText,
                        conditions,
                        cleanedLines,
                        key,
                        pack));
            }
        }
    }

    private AbstractLine parseTextLine(final QuestPackage pack, final String line) {
        AbstractLine textLine = null;
        final Matcher staticMatcher = HologramIntegrator.STATIC_VARIABLE_VALIDATOR.matcher(line);
        if (staticMatcher.find()) {
            try {
                textLine = new StaticVariableTextLine(pack, line);
                //Global variables are updated by BetonQuest, rather than going through external API.
            } catch (final InstructionParseException e) {
                LOG.warn("Malformed text hologram line static variable! Expected format: '$<variable>.<property>$'");
            }
        }
        final Matcher instructionMatcher = HologramIntegrator.INSTRUCTION_VARIABLE_VALIDATOR.matcher(line);
        if (instructionMatcher.find()) {
            try {
                new VariableString(pack, line); //Validate the variable
                textLine = new TextLine(HologramIntegrator.parseInstructionVariable(pack, line));
            } catch (final InstructionParseException e) {
                LOG.warn("Malformed text hologram line instruction variable! Expected format: '%<variable>.<property>%'");
            }
        }
        if (textLine == null) {
            textLine = new TextLine(line.replace('&', '§'));
        }
        return textLine;
    }

    /**
     * Cancels hologram updating loop and removes all BetonQuest-registered holograms.
     */
    public void cancel() {
        for (final HologramRunner runner : HologramRunner.getRunners()) {
            runner.runnable.cancel();
            for (final HologramWrapper hologramWrapper : runner.getHolograms()) {
                hologramWrapper.hologram().delete();
            }
        }
        HologramRunner.clearRunners();
    }

    /**
     * Groups together all holograms with same update interval and updates them with the same {@link BukkitRunnable}
     */
    private static final class HologramRunner {
        /**
         * Static HashMap of all active runners. The key is the interval of the runner, the value is the runner itself.
         */
        private static final Map<Integer, HologramRunner> RUNNERS = new HashMap<>();
        /**
         * ArrayList of all holograms of a single runner.
         */
        private final List<HologramWrapper> holograms = new ArrayList<>();
        /**
         * Times the periodic execution of content and visibility refresh.
         */
        private final BukkitRunnable runnable;

        /**
         * Creates a new instance of the HologramRunner with the specified interval.
         *
         * @param interval Interval in ticks
         */
        private HologramRunner(final int interval) {
            runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    holograms.forEach(h -> {
                        h.updateContent();
                        h.updateVisibility();
                    });
                }
            };
            runnable.runTaskTimerAsynchronously(BetonQuest.getInstance(), 20, interval);
        }

        /**
         * Adds a new HologramWrapper to the execution cycle. Decides whether to create a new runner or add the
         * Hologram to an existing runner that shares the same cycle in ticks.
         *
         * @param hologram Hologram to be added
         */
        public static void addHologram(final HologramWrapper hologram) {
            if (!RUNNERS.containsKey(hologram.interval())) {
                RUNNERS.put(hologram.interval(), new HologramRunner(hologram.interval()));
            }
            RUNNERS.get(hologram.interval()).holograms.add(hologram);

            hologram.initialiseContent();
            hologram.updateVisibility();
        }

        /**
         * Returns all active HologramRunner instances.
         *
         * @return All active HologramRunner instances
         */
        public static Collection<HologramRunner> getRunners() {
            return RUNNERS.values();
        }

        public static void clearRunners() {
            RUNNERS.clear();
        }

        /**
         * Returns all HologramWrappers from a runner.
         *
         * @return all HologramWrappers from a runner
         */
        public List<HologramWrapper> getHolograms() {
            return holograms;
        }
    }
}
