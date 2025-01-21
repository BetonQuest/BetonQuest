package org.betonquest.betonquest.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unimi.dsi.fastutil.Pair;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.Config;
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
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Various utilities.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.TooManyMethods", "PMD.GodClass"})
public final class Utils {
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
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
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
                + instance.getPluginConfig().getString("version", null);

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
        for (final String bigPage : bigPages) {
            if (Config.getConfigString("journal.lines_per_page") == null) {
                final int charsPerPage = Integer.parseInt(Config.getConfigString("journal.chars_per_page"));
                StringBuilder page = new StringBuilder();
                for (final String word : bigPage.split(" ")) {
                    if (getStringLength(page.toString()) + getStringLength(word) + 1 > charsPerPage) {
                        pages.add(page.toString().stripTrailing());
                        page = new StringBuilder();
                    }
                    page.append(word).append(' ');
                }
                pages.add(page.toString().stripTrailing().replaceAll("(?<!\\\\)\\\\n", "\n"));
            } else {
                final int charsPerLine = Integer.parseInt(Config.getConfigString("journal.chars_per_line"));
                final int linesPerPage = Integer.parseInt(Config.getConfigString("journal.lines_per_page"));
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
        if (item == null) {
            return false;
        }
        return item.hasItemMeta() && item.getItemMeta().hasLore()
                && item.getItemMeta().getLore().contains(Config.getMessage(Config.getLanguage(), "quest_item"));
    }

    /**
     * Gets the party of the location.
     * A range of 0 means worldwide and -1 means server-wide.
     *
     * @param location   the location to get the party of
     * @param range      the range of the party
     * @param conditions conditions that the party members must meet
     * @return the party of the location
     */
    public static Map<OnlineProfile, Double> getParty(final Location location, final double range, final List<ConditionID> conditions) {
        final World world = location.getWorld();
        final double squared = range * range;

        final Stream<OnlineProfile> players = PlayerConverter.getOnlineProfiles().stream();
        final Stream<OnlineProfile> worldPlayers = range == -1 ? players : players.filter(profile -> world.equals(profile.getPlayer().getWorld()));
        final Stream<Pair<OnlineProfile, Double>> distancePlayers = worldPlayers.map(profile -> Pair.of(profile, getDistanceSquared(profile, location)));
        final Stream<Pair<OnlineProfile, Double>> rangePlayers = range <= 0 ? distancePlayers : distancePlayers.filter(pair -> pair.right() <= squared);
        return rangePlayers
                .filter(pair -> BetonQuest.conditions(pair.left(), conditions))
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
     * Inserts a package before this string if there is no package, or does
     * nothing if the package is already there.
     *
     * @param pack   the package
     * @param string ID of event/condition/objective/item etc.
     * @return full ID with package prefix
     */
    public static String addPackage(final QuestPackage pack, final String string) {
        if (string.contains(".")) {
            return string;
        } else {
            return pack.getQuestPath() + "." + string;
        }
    }

    /**
     * Parses the string as RGB or as DyeColor and returns it as Color.
     *
     * @param string string to parse as a Color
     * @return the Color (never null)
     * @throws QuestException when something goes wrong
     */
    @SuppressWarnings({"PMD.PreserveStackTrace", "PMD.CyclomaticComplexity"})
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
     * <p>
     * {@code format(string, false, false)} will return the string with no formatting done.
     *
     * @param string     the input string
     * @param colorCodes if alternate color codes should be resolved
     * @param lineBreaks if {@code \\n} should be replaced with {@code \n}
     * @return a formatted version of the input string
     */
    public static String format(final String string, final boolean colorCodes, final boolean lineBreaks) {
        String input = string;
        if (colorCodes) {
            input = ChatColor.translateAlternateColorCodes('&', input);
        }
        if (lineBreaks) {
            input = input.replaceAll("(?<!\\\\)\\\\n", "\n");
        }
        return input;
    }

    /**
     * Formats the string by replacing {@code \\n} with {@code \n} and resolving alternate color codes with {@code &}.
     *
     * @param string the input string
     * @return a formatted version of the input string
     */
    public static String format(final String string) {
        return format(string, true, true);
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
