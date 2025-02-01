package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.compatibility.holograms.lines.AbstractLine;
import org.betonquest.betonquest.compatibility.holograms.lines.ItemLine;
import org.betonquest.betonquest.compatibility.holograms.lines.TextLine;
import org.betonquest.betonquest.compatibility.holograms.lines.TopLine;
import org.betonquest.betonquest.compatibility.holograms.lines.TopXObject;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hides and shows holograms to players, based on conditions.
 */
public abstract class HologramLoop {
    /**
     * The regex for one color.
     */
    private static final String COLOR_REGEX = ";?([&§]?[0-9a-f])?";

    /**
     * Pattern to match the correct syntax for the top line content.
     */
    private static final Pattern TOP_LINE_VALIDATOR = Pattern.compile("^top:([\\w.]+);(\\w+);(\\d+)" + COLOR_REGEX + COLOR_REGEX + COLOR_REGEX + COLOR_REGEX + "$", Pattern.CASE_INSENSITIVE);

    /**
     * The string to match the descending order.
     */
    private static final String ORDER_DESC = "desc";

    /**
     * The string to match the ascending order.
     */
    private static final String ORDER_ASC = "asc";

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    protected final BetonQuestLogger log;

    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of the loop.
     *
     * @param loggerFactory logger factory to use
     * @param log           the logger that will be used for logging
     */
    public HologramLoop(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log) {
        this.loggerFactory = loggerFactory;
        this.log = log;
    }

    /**
     * Initializes the holograms.
     *
     * @param name the name of the holograms to initialize
     * @return the list of holograms
     */
    protected final List<HologramWrapper> initialize(final String name) {
        final List<HologramWrapper> holograms = new ArrayList<>();
        final int defaultInterval = BetonQuest.getInstance().getPluginConfig().getInt("hologram_update_interval", 10 * 20);

        for (final QuestPackage pack : Config.getPackages().values()) {
            final ConfigurationSection section = pack.getConfig().getConfigurationSection(name);
            if (section == null) {
                continue;
            }
            for (final String key : section.getKeys(false)) {
                final ConfigurationSection hologramSection = section.getConfigurationSection(key);
                if (hologramSection == null) {
                    continue;
                }
                try {
                    holograms.add(initializeHolograms(defaultInterval, pack, hologramSection));
                } catch (final QuestException e) {
                    log.warn(pack, "Error while loading hologram '" + key + "' in package '" + pack.getQuestPath() + "': " + e.getMessage(), e);
                }
            }
        }
        return holograms;
    }

    private HologramWrapper initializeHolograms(final int defaultInterval, final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final String checkIntervalString = GlobalVariableResolver.resolve(pack, section.getString("check_interval"));
        final int checkInterval;
        try {
            checkInterval = checkIntervalString != null ? Integer.parseInt(checkIntervalString) : defaultInterval;
        } catch (final NumberFormatException e) {
            throw new QuestException("Could not parse check interval", e);
        }
        final VariableNumber maxRange = new VariableNumber(BetonQuest.getInstance().getVariableProcessor(), pack, section.getString("max_range", "0"));

        final List<String> lines = GlobalVariableResolver.resolve(pack, section.getStringList("lines"));
        final String rawConditions = GlobalVariableResolver.resolve(pack, section.getString("conditions"));

        final ConditionID[] conditions = parseConditions(pack, rawConditions);

        final List<AbstractLine> cleanedLines = new ArrayList<>();
        for (final String line : lines) {
            if (line.startsWith("item:")) {
                cleanedLines.add(parseItemLine(pack, line));
            } else if (line.startsWith("top:")) {
                cleanedLines.add(parseTopLine(pack, line));
            } else {
                cleanedLines.add(parseTextLine(pack, line.replace('&', '§')));
            }
        }
        final List<BetonHologram> holograms = getHologramsFor(pack, section);
        for (final BetonHologram hologram : holograms) {
            hologram.hideAll();
        }
        final HologramWrapper hologramWrapper = new HologramWrapper(
                checkInterval,
                holograms,
                isStaticHologram(cleanedLines),
                conditions,
                cleanedLines,
                pack,
                maxRange);
        HologramRunner.addHologram(hologramWrapper);
        return hologramWrapper;
    }

    /**
     * Creates and returns a list of holograms for the given section.
     *
     * @param pack    the package of the holograms
     * @param section the section of the holograms
     * @return a list of holograms
     * @throws QuestException if there is an error while parsing the holograms
     */
    protected abstract List<BetonHologram> getHologramsFor(QuestPackage pack, ConfigurationSection section) throws QuestException;

    private ConditionID[] parseConditions(final QuestPackage pack, @Nullable final String rawConditions) throws QuestException {
        ConditionID[] conditions = {};
        if (rawConditions != null) {
            final String[] parts = rawConditions.split(",");
            conditions = new ConditionID[parts.length];
            for (int i = 0; i < conditions.length; i++) {
                try {
                    conditions[i] = new ConditionID(pack, parts[i]);
                } catch (final QuestException e) {
                    throw new QuestException("Error while loading condition '" + parts[i] + "': " + e.getMessage(), e);
                }
            }
        }
        return conditions;
    }

    private boolean isStaticHologram(final List<AbstractLine> lines) {
        return lines.stream().noneMatch(AbstractLine::isNotStaticText);
    }

    @SuppressWarnings("PMD.LocalVariableCouldBeFinal")
    private ItemLine parseItemLine(final QuestPackage pack, final String line) throws QuestException {
        try {
            final String[] args = line.substring(5).split(":");
            final ItemID itemID = new ItemID(pack, args[0]);
            int stackSize;
            try {
                stackSize = Integer.parseInt(args[1]);
            } catch (final NumberFormatException | ArrayIndexOutOfBoundsException e) {
                stackSize = 1;
            }
            return new ItemLine(new QuestItem(itemID).generate(stackSize));
        } catch (final QuestException e) {
            throw new QuestException("Error while loading item: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private TopLine parseTopLine(final QuestPackage pack, final String line) throws QuestException {
        final Matcher validator = TOP_LINE_VALIDATOR.matcher(line);
        if (!validator.matches()) {
            throw new QuestException("Malformed top line in hologram! Expected format: 'top:<point>;<order>;<limit>[;<color>][;<color>][;<color>][;<color>]'.");
        }

        String pointName = validator.group(1);
        if (!pointName.contains(".")) {
            pointName = pack.getQuestPath() + '.' + pointName;
        }

        final TopXObject.OrderType orderType;
        if (ORDER_DESC.equalsIgnoreCase(validator.group(2))) {
            orderType = TopXObject.OrderType.DESCENDING;
        } else if (ORDER_ASC.equalsIgnoreCase(validator.group(2))) {
            orderType = TopXObject.OrderType.ASCENDING;
        } else {
            throw new QuestException("Top list order type '" + validator.group(2) + "' unknown! Expected 'asc' or 'desc'.");
        }

        final int limit;
        try {
            limit = Integer.parseInt(validator.group(3));
        } catch (final NumberFormatException e) {
            throw new QuestException("Top list limit must be numeric! Expected format: 'top:<point>;<order>;<limit>[;<color>][;<color>][;<color>][;<color>]'.", e);
        }
        final ChatColor colorPlace = getColorCodes(validator.group(4));
        final ChatColor colorName = getColorCodes(validator.group(5));
        final ChatColor colorDash = getColorCodes(validator.group(6));
        final ChatColor colorScore = getColorCodes(validator.group(7));
        return new TopLine(loggerFactory, pointName, orderType, limit, new TopLine.FormatColors(colorPlace, colorName, colorDash, colorScore));
    }

    private ChatColor getColorCodes(@Nullable final String color) {
        if (color != null) {
            final int length = color.length();
            if (length == 1 || length == 2) {
                final char colorChar = color.charAt(length - 1);
                final ChatColor byChar = ChatColor.getByChar(colorChar);
                if (byChar != null) {
                    return byChar;
                }
            }
        }
        return ChatColor.WHITE;
    }

    private TextLine parseTextLine(final QuestPackage pack, final String line) {
        final Matcher matcher = HologramProvider.VARIABLE_VALIDATOR.matcher(line);
        return new TextLine(matcher.find()
                ? HologramProvider.getInstance().parseVariable(pack, line)
                : line);
    }
}
