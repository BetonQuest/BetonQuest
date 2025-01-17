package org.betonquest.betonquest;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.api.bukkit.event.PlayerJournalAddEvent;
import org.betonquest.betonquest.api.bukkit.event.PlayerJournalDeleteEvent;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.database.Saver.Record;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.utils.Utils;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents player's journal.
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidLiteralsInIfCondition", "PMD.CyclomaticComplexity",
        "PMD.TooManyMethods"})
public class Journal {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log = BetonQuest.getInstance().getLoggerFactory().create(getClass());

    private final Profile profile;

    private final List<Pointer> pointers;

    private final List<String> texts = new ArrayList<>();

    private final ConfigurationFile config;

    private String lang;

    @Nullable
    private String mainPage;

    /**
     * Creates new Journal instance from List of Pointers.
     *
     * @param profile the {@link OnlineProfile} of the player whose journal is created
     * @param lang    default language to use when generating the journal
     * @param list    list of pointers to journal entries
     * @param config  a {@link ConfigurationFile} that contains the plugin's configuration
     */
    public Journal(final Profile profile, final String lang, final List<Pointer> list, final ConfigurationFile config) {
        // generate texts from list of pointers
        this.profile = profile;
        this.lang = lang;
        this.pointers = list;
        this.config = config;
    }

    /**
     * Checks if the item is journal
     *
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @param item          ItemStack to check against being the journal
     * @return true if the ItemStack is the journal, false otherwise
     */
    public static boolean isJournal(final OnlineProfile onlineProfile, @Nullable final ItemStack item) {
        // if there is no item then it's not a journal
        if (item == null) {
            return false;
        }
        // get language
        final String playerLang = BetonQuest.getInstance().getPlayerDataStorage().get(onlineProfile).getLanguage();
        // check all properties of the item and return the result
        return item.getType().equals(Material.WRITTEN_BOOK) && ((BookMeta) item.getItemMeta()).hasTitle()
                && ((BookMeta) item.getItemMeta()).getTitle().equals(Config.getMessage(playerLang, "journal_title"))
                && item.getItemMeta().hasLore()
                && Objects.equals(item.getItemMeta().getLore(), getJournalLore(playerLang));
    }

    private static List<String> getJournalLore(final String lang) {
        return Arrays.asList(Utils.format(Config.getMessage(lang, "journal_lore")).split("\n"));
    }

    /**
     * Checks if the player has his journal in the inventory. Returns false if
     * the player is not online.
     *
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @return true if the player has his journal, false otherwise
     */
    public static boolean hasJournal(final OnlineProfile onlineProfile) {
        final Player player = onlineProfile.getPlayer();
        for (final ItemStack item : player.getInventory().getContents()) {
            if (isJournal(onlineProfile, item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the list of pointers in this journal.
     *
     * @return this Journal's list of pointers to journal entries
     */
    public List<Pointer> getPointers() {
        return pointers;
    }

    /**
     * Adds pointer to the journal. It needs to be updated now.
     *
     * @param pointer the pointer to be added
     */
    public void addPointer(final Pointer pointer) {
        final BetonQuest betonQuest = BetonQuest.getInstance();
        betonQuest.callSyncBukkitEvent(new PlayerJournalAddEvent(profile, this, pointer));
        pointers.add(pointer);
        // SQLite doesn't accept formatted date and MySQL doesn't accept numeric
        // timestamp
        final String date = betonQuest.isMySQLUsed()
                ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(new Date(pointer.getTimestamp()))
                : Long.toString(pointer.getTimestamp());
        betonQuest.getSaver()
                .add(new Record(UpdateType.ADD_JOURNAL, profile.getProfileUUID().toString(), pointer.getPointer(), date));
    }

    /**
     * Removes the pointer from journal. It needs to be updated now.
     *
     * @param pointerName the name of the pointer to remove
     */
    public void removePointer(final String pointerName) {
        for (final Pointer pointer : pointers) {
            if (pointer.getPointer().equalsIgnoreCase(pointerName)) {
                final BetonQuest betonQuest = BetonQuest.getInstance();
                betonQuest.callSyncBukkitEvent(new PlayerJournalDeleteEvent(profile, this, pointer));
                final String date = betonQuest.isMySQLUsed()
                        ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(new Date(pointer.getTimestamp()))
                        : Long.toString(pointer.getTimestamp());
                betonQuest.getSaver()
                        .add(new Record(UpdateType.REMOVE_JOURNAL, profile.getProfileUUID().toString(), pointer.getPointer(), date));
                pointers.remove(pointer);
                break;
            }
        }
    }

    /**
     * Retrieves the list of generated texts.
     *
     * @return list of Strings - texts for every journal entry
     */
    public List<String> getText() {
        final List<String> list;
        if (Boolean.parseBoolean(config.getString("journal.reversed_order"))) {
            list = Lists.reverse(texts);
        } else {
            list = new ArrayList<>(texts);
        }
        final List<String> pagesList = new ArrayList<>();
        for (final String entry : list) {
            pagesList.addAll(Utils.pagesFromString(entry));
        }
        return pagesList;
    }

    /**
     * Generates texts for every pointer and places them inside a List
     *
     * @param lang the language to use while generating text
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    public void generateTexts(final String lang) {
        // remove previous texts
        texts.clear();
        this.lang = lang;
        // generate the first page
        mainPage = generateMainPage();
        for (final Pointer pointer : pointers) {
            // if date should not be hidden, generate the date prefix
            String datePrefix = "";
            if ("false".equalsIgnoreCase(config.getString("journal.hide_date"))) {
                final String date = new SimpleDateFormat(config.getString("date_format"), Locale.ROOT)
                        .format(pointer.getTimestamp());
                final String[] dateParts = date.split(" ");
                final String day = "§" + config.getString("journal_colors.date.day") + dateParts[0];
                String hour = "";
                if (dateParts.length > 1) {
                    hour = "§" + config.getString("journal_colors.date.hour") + dateParts[1];
                }
                datePrefix = day + " " + hour + "\n";
            }
            // get package and name of the pointer
            final String[] parts = pointer.getPointer().split("\\.");
            final String packName = parts[0];
            final QuestPackage pack = Config.getPackages().get(packName);
            if (pack == null) {
                continue;
            }
            final String pointerName = parts[1];
            // resolve the text in player's language
            String text;
            final ConfigurationSection journal = pack.getConfig().getConfigurationSection("journal");
            if (journal != null && journal.contains(pointerName)) {
                if (journal.isConfigurationSection(pointerName)) {
                    text = pack.getFormattedString("journal." + pointerName + "." + lang);
                    if (text == null) {
                        text = pack.getFormattedString("journal." + pointerName + "." + Config.getLanguage());
                    }
                } else {
                    text = pack.getFormattedString("journal." + pointerName);
                }
            } else {
                log.warn(pack, "No defined journal entry " + pointerName + " in package " + pack.getQuestPath());
                text = "error";
            }

            // handle case when the text isn't defined
            if (text == null) {
                log.warn(pack, "No text defined for journal entry " + pointerName + " in language " + lang);
                text = "error";
            }

            try {
                text = new VariableString(pack, text).getString(profile);
            } catch (final QuestException e) {
                log.warn(pack, "Error while creating variable on journal page '" + pointerName + "' in "
                        + profile + " journal: " + e.getMessage(), e);
            }

            // add the entry to the list
            texts.add(datePrefix + "§" + config.getString("journal_colors.text") + text);
        }
    }

    /**
     * Generates the main page for this journal.
     *
     * @return the main page string or null, if there is no main page
     */
    @SuppressWarnings({"PMD.NcssCount", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    @Nullable
    private String generateMainPage() {
        final Map<Integer, List<String>> lines = new HashMap<>(); // holds text lines with their priority
        final Set<Integer> numbers = new HashSet<>(); // stores numbers that are used, so there's no need to search them
        for (final QuestPackage pack : Config.getPackages().values()) {
            final String packName = pack.getQuestPath();
            final ConfigurationSection section = pack.getConfig().getConfigurationSection("journal_main_page");
            if (section == null) {
                continue;
            }
            for (final String key : section.getKeys(false)) {
                final int number = section.getInt(key + ".priority", -1);
                if (number >= 0) {
                    final String rawConditions = GlobalVariableResolver.resolve(pack, section.getString(key + ".conditions"));
                    if (rawConditions != null && !rawConditions.isEmpty()) {
                        try {
                            final List<ConditionID> pageConditions = new ArrayList<>();
                            for (final String conditionString : rawConditions.split(",")) {
                                if (!conditionString.isEmpty()) {
                                    pageConditions.add(new ConditionID(pack, conditionString));
                                }
                            }

                            if (!BetonQuest.conditions(profile, pageConditions)) {
                                continue;
                            }
                        } catch (final ObjectNotFoundException e) {
                            log.warn(pack, "Error while generating main page in " + profile + " journal: " + e.getMessage(), e);
                            continue;
                        }
                    }
                    // here conditions are met, get the text in player's language
                    String text;
                    if (section.isConfigurationSection(key + ".text")) {
                        text = section.getString(key + ".text." + lang);
                        if (text == null) {
                            text = section.getString(key + ".text." + Config.getLanguage());
                        }
                        if (text == null) {
                            text = section.getString(key + ".text.en");
                        }
                    } else {
                        text = section.getString(key + ".text");
                    }
                    if (text == null || text.length() == 0) {
                        continue;
                    }
                    text = GlobalVariableResolver.resolve(pack, text);
                    try {
                        text = new VariableString(pack, text).getString(profile);
                    } catch (final QuestException e) {
                        log.warn(pack, "Error while creating variable on main page in "
                                + profile + " journal: " + e.getMessage(), e);
                    }
                    // add the text to HashMap
                    numbers.add(number);
                    final List<String> linesOrder;
                    if (lines.containsKey(number)) {
                        linesOrder = lines.get(number);
                    } else {
                        linesOrder = new ArrayList<>();
                        lines.put(number, linesOrder);
                    }
                    linesOrder.add(text + "§r"); // reset the formatting
                } else {
                    log.warn(pack, "Priority of " + packName + "." + key
                            + " journal main page line is not defined");
                }
            }
        }
        if (numbers.isEmpty()) {
            return null;
        }
        return sort(numbers, lines);
    }

    @SuppressWarnings("NullAway")
    private String sort(final Set<Integer> numbers, final Map<Integer, List<String>> lines) {
        // now all lines from all packages are extracted, sort numbers
        Integer[] sorted = new Integer[numbers.size()];
        sorted = numbers.toArray(sorted);
        Arrays.sort(sorted);
        // build the string and return it
        final List<String> sortedLines = new ArrayList<>();
        for (final int i : sorted) {
            final List<String> linesOrder = lines.get(i);
            String[] sortedLinesOrder = new String[linesOrder.size()];
            sortedLinesOrder = linesOrder.toArray(sortedLinesOrder);
            Arrays.sort(sortedLinesOrder);
            sortedLines.addAll(Arrays.asList(sortedLinesOrder));
        }
        return StringUtils.join(sortedLines, '\n').replace('&', '§');
    }

    /**
     * Clears the Journal completely but doesn't touch the database.
     */
    public void clear() {
        texts.clear();
        pointers.clear();
    }

    /**
     * Adds journal to player inventory.
     */
    public void addToInv() {
        final int targetSlot = getJournalSlot();
        generateTexts(lang);
        final Inventory inventory = profile.getOnlineProfile().get().getPlayer().getInventory();
        final ItemStack item = getAsItem();
        if (inventory.firstEmpty() >= 0) {
            if (targetSlot < 0) {
                inventory.addItem(item);
            } else {
                final ItemStack oldItem = inventory.getItem(targetSlot);
                inventory.setItem(targetSlot, item);
                if (oldItem != null) {
                    inventory.addItem(oldItem);
                }
            }
        } else {
            try {
                Config.sendNotify(null, profile.getOnlineProfile().get(), "inventory_full_backpack", null, "inventory_full_backpack,inventory_full,error");
            } catch (final QuestException e) {
                log.warn("The notify system was unable to play a sound for the 'inventory_full_backpack' category. Error was: '" + e.getMessage() + "'", e);
            }
        }
    }

    @SuppressWarnings("PMD.PrematureDeclaration")
    private int getJournalSlot() {
        final int slot = config.getInt("default_journal_slot");
        final boolean forceJournalSlot = config.getBoolean("journal.lock_default_journal_slot");
        final int oldSlot = removeFromInv();
        if (forceJournalSlot) {
            return slot;
        }
        return oldSlot == -1 ? slot : oldSlot;
    }

    /**
     * Generates the journal as ItemStack
     *
     * @return the journal ItemStack
     */
    @SuppressWarnings("PMD.CognitiveComplexity")
    public ItemStack getAsItem() {
        final ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        final BookMeta meta = (BookMeta) item.getItemMeta();
        meta.setTitle(Utils.format(Config.getMessage(lang, "journal_title")));
        meta.setAuthor(profile.getPlayer().getName());
        meta.setCustomModelData(config.getInt("journal.custom_model_data"));
        meta.setLore(getJournalLore(lang));

        // add main page and generate pages from texts
        final List<String> finalList = new ArrayList<>();
        if (config.getBoolean("journal.one_entry_per_page")) {
            if (mainPage != null && mainPage.length() > 0) {
                finalList.addAll(Utils.pagesFromString(mainPage));
            }
            finalList.addAll(getText());
        } else {
            final String color = config.getString("journal_colors.line");
            String separator = Config.parseMessage(null, profile.getOnlineProfile().get(), "journal_separator");
            if (separator == null) {
                separator = "---------------";
            }

            final String line;
            if (config.getBoolean("journal.show_separator")) {
                line = "\n§" + color + separator + "\n";
            } else {
                line = "\n";
            }

            final StringBuilder stringBuilder = new StringBuilder();
            for (final String entry : getText()) {
                stringBuilder.append(entry).append(line);
            }
            if (mainPage != null && mainPage.length() > 0) {
                if (config.getBoolean("journal.full_main_page")) {
                    finalList.addAll(Utils.pagesFromString(mainPage));
                } else {
                    stringBuilder.insert(0, mainPage + line);
                }
            }
            final String wholeString = stringBuilder.toString().trim();
            finalList.addAll(Utils.pagesFromString(wholeString));
        }
        if (finalList.isEmpty()) {
            meta.addPage("");
        } else {
            meta.setPages(Utils.multiLineColorCodes(finalList, "§" + config.getString("journal_colors.line")));
        }
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Updates journal by removing it and adding it again
     */
    public void update() {
        if (hasJournal(profile.getOnlineProfile().get())) {
            addToInv();
        }
    }

    /**
     * Removes journal from player's inventory.
     *
     * @return the slot from which the journal was removed
     */
    public int removeFromInv() {
        // loop all items and check if any of them is a journal
        final Inventory inventory = profile.getOnlineProfile().get().getPlayer().getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (isJournal(profile.getOnlineProfile().get(), inventory.getItem(i))) {
                inventory.setItem(i, new ItemStack(Material.AIR));
                return i;
            }
        }
        return -1;
    }
}
