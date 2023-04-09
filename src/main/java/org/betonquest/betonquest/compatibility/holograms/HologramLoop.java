package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.compatibility.holograms.lines.AbstractLine;
import org.betonquest.betonquest.compatibility.holograms.lines.ItemLine;
import org.betonquest.betonquest.compatibility.holograms.lines.TextLine;
import org.betonquest.betonquest.compatibility.holograms.lines.TopLine;
import org.betonquest.betonquest.compatibility.holograms.lines.TopXObject;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hides and shows holograms to players, based on conditions.
 */
public abstract class HologramLoop {
    /**
     * Pattern to match the correct syntax for the top line content
     */
    protected static final Pattern TOP_LINE_VALIDATOR = Pattern.compile("^top:([\\w.]+);(\\w+);(\\d+);?[&§]?([\\da-f])?;?[&§]?([\\da-f])?;?[&§]?([\\da-f])?;?[&§]?([\\da-f])?$", Pattern.CASE_INSENSITIVE);
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(HologramLoop.class);


    /**
     * Creates a new instance of the loop.
     */
    public HologramLoop() {
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
                } catch (final InstructionParseException e) {
                    LOG.warn(pack, "Error while loading hologram '" + key + "' in package '" + pack.getQuestPath() + "': " + e.getMessage(), e);
                }
            }
        }
        return holograms;
    }

    private HologramWrapper initializeHolograms(final int defaultInterval, final QuestPackage pack, final ConfigurationSection section) throws InstructionParseException {
        final String checkIntervalString = GlobalVariableResolver.resolve(pack, section.getString("check_interval"));
        final int checkInterval;
        try {
            checkInterval = checkIntervalString != null ? Integer.parseInt(checkIntervalString) : defaultInterval;
        } catch (final NumberFormatException e) {
            throw new InstructionParseException("Could not parse check interval", e);
        }
        final List<String> lines = GlobalVariableResolver.resolve(pack, section.getStringList("lines"));
        final String rawConditions = GlobalVariableResolver.resolve(pack, section.getString("conditions"));

        final ConditionID[] conditions = parseConditions(pack, rawConditions);

        final ArrayList<AbstractLine> cleanedLines = new ArrayList<>();
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
                pack);
        HologramRunner.addHologram(hologramWrapper);
        return hologramWrapper;
    }

    /**
     * Creates and returns a list of holograms for the given section.
     *
     * @param pack    the package of the holograms
     * @param section the section of the holograms
     * @return a list of holograms
     * @throws InstructionParseException if there is an error while parsing the holograms
     */
    protected abstract List<BetonHologram> getHologramsFor(QuestPackage pack, ConfigurationSection section) throws InstructionParseException;

    @NotNull
    private ConditionID[] parseConditions(final QuestPackage pack, final String rawConditions) throws InstructionParseException {
        ConditionID[] conditions = {};
        if (rawConditions != null) {
            final String[] parts = rawConditions.split(",");
            conditions = new ConditionID[parts.length];
            for (int i = 0; i < conditions.length; i++) {
                try {
                    conditions[i] = new ConditionID(pack, parts[i]);
                } catch (final ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while loading condition '" + parts[i] + "': " + e.getMessage(), e);
                }
            }
        }
        return conditions;
    }

    private boolean isStaticHologram(final List<AbstractLine> lines) {
        return lines.stream().noneMatch(AbstractLine::isNotStaticText);
    }

    @NotNull
    private ItemLine parseItemLine(final QuestPackage pack, final String line) throws InstructionParseException {
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
        } catch (final ObjectNotFoundException | InstructionParseException e) {
            throw new InstructionParseException("Error while loading item: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @NotNull
    private TopLine parseTopLine(final QuestPackage pack, final String line) throws InstructionParseException {
        final Matcher validator = TOP_LINE_VALIDATOR.matcher(line);
        if (!validator.matches()) {
            throw new InstructionParseException("Malformed top line in hologram! Expected format: 'top:<point>;<order>;<limit>[;<color>][;<color>][;<color>][;<color>]'.");
        }

        String pointName = validator.group(1);
        if (!pointName.contains(".")) {
            pointName = pack.getQuestPath() + '.' + pointName;
        }

        final TopXObject.OrderType orderType;
        if ("desc".equalsIgnoreCase(validator.group(2))) {
            orderType = TopXObject.OrderType.DESCENDING;
        } else if ("asc".equalsIgnoreCase(validator.group(2))) {
            orderType = TopXObject.OrderType.ASCENDING;
        } else {
            throw new InstructionParseException("Top list order type '" + validator.group(2) + "' unknown! Expected 'asc' or 'desc'.");
        }

        final int limit;
        try {
            limit = Integer.parseInt(validator.group(3));
        } catch (final NumberFormatException e) {
            throw new InstructionParseException("Top list limit must be numeric! Expected format: 'top:<point>;<order>;<limit>[;<color>][;<color>][;<color>][;<color>]'.", e);
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
        return new TopLine(pointName, orderType, limit, colorCodes.toString().toCharArray());
    }

    @NotNull
    private TextLine parseTextLine(final QuestPackage pack, final String line) {
        final Matcher matcher = HologramProvider.VARIABLE_VALIDATOR.matcher(line);
        return new TextLine(matcher.find()
                ? HologramProvider.getInstance().parseVariable(pack, line)
                : line);
    }
}
