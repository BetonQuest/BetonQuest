package org.betonquest.betonquest.util;

import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.config.Zipper;
import org.betonquest.betonquest.database.Backup;
import org.betonquest.betonquest.id.ConditionID;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Various utilities.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public final class Utils {

    /**
     * BiPredicate that checks if a {@link Component} contains another one while ironing unset values.
     */
    public static final BiPredicate<Component, Component> COMPONENT_BI_PREDICATE = (component1, component2) -> {
        if (!(component1 instanceof final TextComponent textComponent1) || !(component2 instanceof final TextComponent textComponent2)) {
            return false;
        }
        if (!textComponent1.content().equals(textComponent2.content())) {
            return false;
        }
        final Style style1 = textComponent1.style();
        final Style style2 = textComponent2.style();
        if (!Objects.equals(style1.color(), style2.color())) {
            return false;
        }
        final Map<TextDecoration, TextDecoration.State> decorations1 = style1.decorations();
        final Map<TextDecoration, TextDecoration.State> decorations2 = style2.decorations();
        for (final Map.Entry<TextDecoration, TextDecoration.State> entry : decorations1.entrySet()) {
            final TextDecoration.State state1 = entry.getValue();
            final TextDecoration.State state2 = decorations2.get(entry.getKey());
            if (state1 == TextDecoration.State.NOT_SET || state2 == TextDecoration.State.NOT_SET) {
                continue;
            }
            if (state1 != state2) {
                return false;
            }
        }
        return true;
    };

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuest.getInstance().getLoggerFactory().create(Utils.class);

    private Utils() {
    }

    /**
     * Does a full configuration backup.
     *
     * @param configAccessorFactory the factory that will be used to create
     *                              {@link org.betonquest.betonquest.api.config.ConfigAccessor}s
     */
    public static void backup(final ConfigAccessorFactory configAccessorFactory) {
        LOG.info("Backing up!");
        final long time = new Date().getTime();
        final BetonQuest instance = BetonQuest.getInstance();
        if (!Backup.backupDatabase(configAccessorFactory, new File(instance.getDataFolder(), "database-backup.yml"))) {
            LOG.warn("There was an error during backing up the database! This does not affect"
                    + " the configuration backup, nor damage your database. You should backup"
                    + " the database manually if you want to be extra safe, but it's not necessary if"
                    + " you don't want to downgrade later.");
        }
        // create Backups folder if it does not exist
        final File backupFolder = new File(instance.getDataFolder(), "Backups");
        if (!backupFolder.isDirectory() && !backupFolder.mkdir()) {
            LOG.error("Could not create backup folder!");
        }
        // zip all the files
        final String outputPath = backupFolder.getAbsolutePath() + File.separator + "backup-"
                + instance.getDescription().getVersion();

        Zipper.zip(instance.getDataFolder(), outputPath, "^backup.*", "^database\\.db$", "^logs$");
        // delete database backup so it doesn't make a mess later on
        if (!new File(instance.getDataFolder(), "database-backup.yml").delete()) {
            LOG.warn("Could not delete database backup file!");
        }
        // done
        LOG.debug("Done in " + (new Date().getTime() - time) + "ms");
        LOG.info("Done, you can find the backup in 'Backups' directory.");
    }

    /**
     * Converts string to a list of pages for a book.
     *
     * @param string text to convert
     * @return the list of pages for a book
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    public static List<String> pagesFromString(final String string) {
        final List<String> pages = new ArrayList<>();
        final String[] bigPages = string.split("\\|");
        final ConfigAccessor config = BetonQuest.getInstance().getPluginConfig();
        final int linesPerPage = config.getInt("journal.format.lines_per_page");
        final int charsPerLine = config.getInt("journal.format.chars_per_line");
        for (final String bigPage : bigPages) {
            StringBuilder page = new StringBuilder();
            int lines = 0;
            for (final String line : bigPage.split("((?<!\\\\)\\\\n|\n)")) {
                StringBuilder lineBuilder = new StringBuilder();
                if (getStringLength(line) <= charsPerLine) {
                    lines++;
                    if (lines > linesPerPage) {
                        pages.add(page.toString());
                        lines = 1;
                        page = new StringBuilder();
                    }
                    page.append(line).append('\n');
                    continue;
                }
                for (final String word : line.split(" ")) {
                    final int stringLength = getStringLength(word);
                    final int lineBuilderLength = getStringLength(lineBuilder.toString());
                    if (lineBuilderLength + stringLength > charsPerLine) {
                        lines++;
                        if (lines > linesPerPage) {
                            pages.add(page.toString());
                            lines = 1;
                            page = new StringBuilder();
                        }
                        page.append(lineBuilder.toString().stripTrailing()).append('\n');
                        lineBuilder = new StringBuilder();
                    }
                    lineBuilder.append(word).append(' ');
                }
                lines++;
                if (lines > linesPerPage) {
                    pages.add(page.toString());
                    lines = 1;
                    page = new StringBuilder();
                }
                page.append(lineBuilder.toString().stripTrailing()).append('\n');
            }
            if (!page.isEmpty()) {
                pages.add(page.toString());
            }
        }
        return pages;
    }

    private static int getStringLength(final String string) {
        return string.replaceAll("[&§][0-9A-Fa-fK-Ok-oRrXx]", "").replaceAll("((?<!\\\\)\\\\n|\n)", "").length();
    }

    /**
     * Checks if the ItemStack is a quest item.
     *
     * @param item ItemStack to check
     * @return true if the supplied ItemStack is a quest item, false otherwise
     */
    public static boolean isQuestItem(@Nullable final ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }
        final List<Component> lore = item.getItemMeta().lore();
        if (lore == null) {
            return false;
        }
        try {
            final Component questItemLore = BetonQuest.getInstance().getPluginMessage().getMessage(null, "quest_item");
            return lore.stream().anyMatch(line -> line.contains(questItemLore, COMPONENT_BI_PREDICATE));
        } catch (final QuestException e) {
            LOG.warn("Failed to get quest item message: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Gets the party of the location.
     * A range of 0 means worldwide and -1 means server-wide.
     *
     * @param questTypeAPI the Quest Type API to check the conditions
     * @param profiles     the online profiles in question
     * @param location     the location to get the party of
     * @param range        the range of the party
     * @param conditions   conditions that the party members must meet
     * @return the party of the location
     */
    public static Map<OnlineProfile, Double> getParty(final QuestTypeAPI questTypeAPI, final Collection<OnlineProfile> profiles,
                                                      final Location location, final double range, final List<ConditionID> conditions) {
        final World world = location.getWorld();
        final double squared = range * range;

        final Stream<OnlineProfile> players = profiles.stream();
        final Stream<OnlineProfile> worldPlayers = range == -1 ? players : players.filter(profile -> world.equals(profile.getPlayer().getWorld()));
        final Stream<Pair<OnlineProfile, Double>> distancePlayers = worldPlayers.map(profile -> Pair.of(profile, getDistanceSquared(profile, location)));
        final Stream<Pair<OnlineProfile, Double>> rangePlayers = range <= 0 ? distancePlayers : distancePlayers.filter(pair -> pair.right() <= squared);
        return rangePlayers
                .filter(pair -> questTypeAPI.conditions(pair.left(), conditions))
                .collect(Collectors.toMap(Pair::left, Pair::right));
    }

    private static double getDistanceSquared(final OnlineProfile profile, final Location loc) {
        try {
            return profile.getPlayer().getLocation().distanceSquared(loc);
        } catch (final IllegalArgumentException e) {
            return Double.MAX_VALUE;
        }
    }

    /**
     * Parses the string as RGB or as DyeColor and returns it as Color.
     *
     * @param string string to parse as a Color
     * @return the Color (never null)
     * @throws QuestException when something goes wrong
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    public static Color getColor(final String string) throws QuestException {
        if (string.isEmpty()) {
            throw new QuestException("Color is not specified");
        }
        try {
            return Color.fromRGB(Integer.parseInt(string));
        } catch (final NumberFormatException e1) {
            LOG.debug("Could not parse number!", e1);
            // string is not a decimal number
            try {
                return Color.fromRGB(Integer.parseInt(string.replace("#", ""), 16));
            } catch (final NumberFormatException e2) {
                LOG.debug("Could not parse number!", e2);
                // string is not a hexadecimal number, try dye color
                try {
                    return DyeColor.valueOf(string.trim().toUpperCase(Locale.ROOT).replace(' ', '_')).getColor();
                } catch (final IllegalArgumentException e3) {
                    // this was not a dye color name
                    throw new QuestException("Dye color does not exist: " + string, e3);
                }
            }
        } catch (final IllegalArgumentException e) {
            // string was a number, but incorrect
            throw new QuestException("Incorrect RGB code: " + string, e);
        }
    }

    /**
     * Resets any color resets to def. Also ensures any new lines copy the colors and format from the previous line
     *
     * @param pages multiple pages to process
     * @param def   default color code to use instead of resetting; use null for regular reset code
     * @return the colorful pages ready to split into multiple lines
     */
    public static List<String> multiLineColorCodes(final List<String> pages, final String def) {
        String lastCodes = "";
        final ListIterator<String> iterator = pages.listIterator();
        final List<String> result = new ArrayList<>();

        while (iterator.hasNext()) {
            final String line = iterator.next();
            result.add(lastCodes + replaceReset(line, def));
            lastCodes = ChatColor.getLastColors(line);
        }

        return result;
    }

    /**
     * Replace resets with colorcode.
     *
     * @param string input string.
     * @param color  default color.
     * @return the formatted string.
     */
    public static String replaceReset(final String string, final String color) {
        return string.replace(ChatColor.RESET.toString(), ChatColor.RESET + color);
    }

    /**
     * Formats the string by replacing {@code \\n} with {@code \n} and resolving alternate color codes with {@code &}.
     *
     * @param string the input string
     * @return a formatted version of the input string
     */
    public static String format(final String string) {
        String input = string;
        input = ChatColor.translateAlternateColorCodes('&', input);
        input = input.replaceAll("(?<!\\\\)\\\\n", "\n");
        return input;
    }

    /**
     * Checks the argument for null and throws when it is actual not present.
     * <p>
     * Primary used in constructors to check against nullable values.
     *
     * @param argument to check for null
     * @param message  of the exception when the argument is null
     * @param <A>      type of the argument
     * @return the argument, if not null
     * @throws QuestException if the argument is null
     */
    public static <A> A getNN(@Nullable final A argument, final String message) throws QuestException {
        if (argument == null) {
            throw new QuestException(message);
        }
        return argument;
    }
}
