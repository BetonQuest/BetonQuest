package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.argument.types.NumberParser;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.compatibility.holograms.lines.AbstractLine;
import org.betonquest.betonquest.compatibility.holograms.lines.ItemLine;
import org.betonquest.betonquest.compatibility.holograms.lines.TextLine;
import org.betonquest.betonquest.compatibility.holograms.lines.TopLine;
import org.betonquest.betonquest.compatibility.holograms.lines.TopXObject;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
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
@SuppressWarnings("PMD.CouplingBetweenObjects")
public abstract class HologramLoop extends SectionProcessor<HologramLoop.HologramID, HologramWrapper> {
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
     * The {@link VariableProcessor} to use.
     */
    protected final VariableProcessor variableProcessor;

    /**
     * Hologram provider to create new Holograms.
     */
    protected final HologramProvider hologramProvider;

    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Default refresh Interval for Holograms.
     */
    private int defaultInterval = 10 * 20;

    /**
     * Creates a new instance of the loop.
     *
     * @param loggerFactory     logger factory to use
     * @param log               the logger that will be used for logging
     * @param packManager       the quest package manager to get quest packages from
     * @param variableProcessor the {@link VariableProcessor} to use
     * @param hologramProvider  the hologram provider to create new holograms
     * @param readable          the type name used for logging, with the first letter in upper case
     * @param internal          the section name and/or bstats topic identifier
     */
    public HologramLoop(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log,
                        final QuestPackageManager packManager, final VariableProcessor variableProcessor,
                        final HologramProvider hologramProvider, final String readable, final String internal) {
        super(log, packManager, readable, internal);
        this.loggerFactory = loggerFactory;
        this.variableProcessor = variableProcessor;
        this.hologramProvider = hologramProvider;
    }

    @Override
    public void clear() {
        super.clear();
        defaultInterval = BetonQuest.getInstance().getPluginConfig().getInt("hologram.update_interval", 10 * 20);
    }

    @Override
    protected HologramWrapper loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final String checkIntervalString = section.getString("check_interval", String.valueOf(defaultInterval));
        final Variable<Number> checkInterval = new Variable<>(variableProcessor, pack, checkIntervalString, Argument.NUMBER);
        final Variable<Number> maxRange = new Variable<>(variableProcessor, pack, section.getString("max_range", "0"), NumberParser.NUMBER);

        final List<String> lines = section.getStringList("lines");
        final List<ConditionID> conditions = new VariableList<>(variableProcessor, pack, section.getString("conditions", ""),
                value -> new ConditionID(packManager, pack, value)).getValue(null);

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
                loggerFactory.create(HologramWrapper.class),
                BetonQuest.getInstance().getQuestTypeApi(),
                BetonQuest.getInstance().getProfileProvider(),
                checkInterval.getValue(null).intValue(),
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

    private boolean isStaticHologram(final List<AbstractLine> lines) {
        return lines.stream().noneMatch(AbstractLine::isNotStaticText);
    }

    private ItemLine parseItemLine(final QuestPackage pack, final String line) throws QuestException {
        try {
            final String[] args = line.substring(5).split(":");
            final ItemID itemID = new ItemID(packManager, pack, args[0]);
            int stackSize;
            try {
                stackSize = Integer.parseInt(args[1]);
            } catch (final NumberFormatException | ArrayIndexOutOfBoundsException e) {
                stackSize = 1;
            }
            return new ItemLine(BetonQuest.getInstance().getFeatureApi().getItem(itemID, null).generate(stackSize));
        } catch (final QuestException e) {
            throw new QuestException("Error while loading item: " + e.getMessage(), e);
        }
    }

    private TopLine parseTopLine(final QuestPackage pack, final String line) throws QuestException {
        final Matcher validator = TOP_LINE_VALIDATOR.matcher(line);
        if (!validator.matches()) {
            throw new QuestException("Malformed top line in hologram! Expected format: 'top:<point>;<order>;<limit>[;<color>][;<color>][;<color>][;<color>]'.");
        }

        final String pointName = PackageArgument.IDENTIFIER.apply(pack, validator.group(1));

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
                ? hologramProvider.parseVariable(pack, line)
                : line);
    }

    @Override
    protected HologramID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new HologramID(packManager, pack, identifier);
    }

    /**
     * Internal identifier/key for a Hologram.
     */
    protected static class HologramID extends Identifier {

        /**
         * Creates a new ID.
         *
         * @param packManager the quest package manager to get quest packages from
         * @param pack        the package the ID is in
         * @param identifier  the id instruction string
         * @throws QuestException if the ID could not be parsed
         */
        protected HologramID(final QuestPackageManager packManager, @Nullable final QuestPackage pack, final String identifier) throws QuestException {
            super(packManager, pack, identifier);
        }
    }
}
