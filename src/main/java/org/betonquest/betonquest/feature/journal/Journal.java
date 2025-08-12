package org.betonquest.betonquest.feature.journal;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.bukkit.event.PlayerJournalAddEvent;
import org.betonquest.betonquest.api.bukkit.event.PlayerJournalDeleteEvent;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.database.Saver.Record;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.JournalEntryID;
import org.betonquest.betonquest.id.JournalMainPageID;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.Material;
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
@SuppressWarnings({"PMD.TooManyMethods", "PMD.CouplingBetweenObjects"})
public class Journal {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuest.getInstance().getLoggerFactory().create(Journal.class);

    /**
     * The plugin message instance used for getting messages.
     */
    private final PluginMessage pluginMessage;

    /**
     * The profile of the player whose journal is created.
     */
    private final Profile profile;

    /**
     * List of pointers to journal entries.
     */
    private final List<Pointer> pointers;

    /**
     * List of texts generated from pointers.
     */
    private final List<String> texts = new ArrayList<>();

    /**
     * The configuration accessor for the plugin's configuration.
     */
    private final ConfigAccessor config;

    /**
     * The sender for notifications when the player's inventory is full.
     */
    private final IngameNotificationSender inventoryFullBackpackSender;

    /**
     * The main page of the journal, which is generated from the main page entries.
     */
    @Nullable
    private String mainPage;

    /**
     * Creates new Journal instance from List of Pointers.
     *
     * @param pluginMessage the {@link PluginMessage} instance
     * @param profile       the {@link OnlineProfile} of the player whose journal is created
     * @param list          list of pointers to journal entries
     * @param config        a {@link ConfigAccessor} that contains the plugin's configuration
     */
    public Journal(final PluginMessage pluginMessage, final Profile profile, final List<Pointer> list, final ConfigAccessor config) {
        this.pluginMessage = pluginMessage;
        this.profile = profile;
        this.pointers = list;
        this.config = config;
        this.inventoryFullBackpackSender = new IngameNotificationSender(LOG, pluginMessage, null,
                "Journal", NotificationLevel.ERROR, "inventory_full_backpack");
    }

    /**
     * Checks if the item is the journal.
     *
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @param item          ItemStack to check against being the journal
     * @return true if the ItemStack is the journal, false otherwise
     */
    public static boolean isJournal(final OnlineProfile onlineProfile, @Nullable final ItemStack item) {
        if (item == null) {
            return false;
        }
        try {
            if (!(item.getItemMeta() instanceof final BookMeta bookMeta)) {
                return false;
            }
            final Component title = bookMeta.title();
            final List<Component> lore = bookMeta.lore();
            if (title == null || lore == null) {
                return false;
            }
            final Component journalTitle = BetonQuest.getInstance().getPluginMessage().getMessage(onlineProfile, "journal_title");
            return title.contains(journalTitle, Utils.COMPONENT_BI_PREDICATE) && Objects.equals(item.getItemMeta().getLore(), getJournalLore(onlineProfile));
        } catch (final QuestException e) {
            LOG.warn("Failed to check if the journal's title is correct: " + e.getMessage(), e);
            return false;
        }
    }

    private static List<String> getJournalLore(final Profile profile) throws QuestException {
        return Arrays.asList(Utils.format(LegacyComponentSerializer.legacySection()
                        .serialize(BetonQuest.getInstance().getPluginMessage().getMessage(profile, "journal_lore")))
                .split("\n"));
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
        // SQLite doesn't accept formatted date and MySQL doesn't accept numeric timestamp
        final String date = betonQuest.isMySQLUsed()
                ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(new Date(pointer.timestamp()))
                : Long.toString(pointer.timestamp());
        betonQuest.getSaver().add(new Record(UpdateType.ADD_JOURNAL, profile.getProfileUUID().toString(),
                pointer.pointer().getFullID(), date));
    }

    /**
     * Removes the pointer from journal. It needs to be updated now.
     *
     * @param pointerName the name of the pointer to remove
     */
    public void removePointer(final JournalEntryID pointerName) {
        for (final Pointer pointer : pointers) {
            if (pointer.pointer().equals(pointerName)) {
                final BetonQuest betonQuest = BetonQuest.getInstance();
                betonQuest.callSyncBukkitEvent(new PlayerJournalDeleteEvent(profile, this, pointer));
                final String date = betonQuest.isMySQLUsed()
                        ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(new Date(pointer.timestamp()))
                        : Long.toString(pointer.timestamp());
                betonQuest.getSaver().add(new Record(UpdateType.REMOVE_JOURNAL, profile.getProfileUUID().toString(),
                        pointer.pointer().getFullID(), date));
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
        if (Boolean.parseBoolean(config.getString("journal.format.reversed_order"))) {
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
     * Generates texts for every pointer and places them inside a List.
     */
    public void generateTexts() {
        texts.clear();
        mainPage = generateMainPage();
        final boolean displayDatePrefix = "false".equalsIgnoreCase(config.getString("journal.format.hide_date"));
        final FeatureAPI featureAPI = BetonQuest.getInstance().getFeatureAPI();
        for (final Pointer pointer : pointers) {
            final String datePrefix = displayDatePrefix ? pointer.generateDatePrefix(config) + "\n" : "";
            final JournalEntryID entryID = pointer.pointer();
            final Text journalEntry;
            try {
                journalEntry = featureAPI.getJournalEntry(entryID);
            } catch (final QuestException e) {
                LOG.warn(entryID.getPackage(), "Cannot add journal entry to " + profile + ": " + e.getMessage(), e);
                continue;
            }

            String text;
            try {
                text = LegacyComponentSerializer.legacySection().serialize(journalEntry.asComponent(profile));
            } catch (final QuestException e) {
                LOG.warn(entryID.getPackage(), "Error while creating variable on journal page '" + entryID + "' in "
                        + profile + " journal: " + e.getMessage(), e);
                text = "error";
            }

            texts.add(datePrefix + "§" + config.getString("journal.format.color.text") + Utils.format(text));
        }
    }

    /**
     * Generates the main page for this journal.
     *
     * @return the main page string or null, if there is no main page
     */
    @Nullable
    private String generateMainPage() {
        final Map<Integer, List<String>> lines = new HashMap<>(); // holds text lines with their priority
        final Set<Integer> numbers = new HashSet<>(); // stores numbers that are used, so there's no need to search them
        final BetonQuest betonQuest = BetonQuest.getInstance();
        final FeatureAPI featureAPI = betonQuest.getFeatureAPI();
        final QuestTypeAPI questTypeAPI = betonQuest.getQuestTypeAPI();
        for (final Map.Entry<JournalMainPageID, JournalMainPageEntry> entry : featureAPI.getJournalMainPages().entrySet()) {
            final JournalMainPageEntry mainPageEntry = entry.getValue();
            String text;
            try {
                final List<ConditionID> conditions = mainPageEntry.conditions().getValue(profile);
                if (!conditions.isEmpty() && !questTypeAPI.conditions(profile, conditions)) {
                    continue;
                }
                text = LegacyComponentSerializer.legacySection().serialize(mainPageEntry.entry().asComponent(profile));
            } catch (final QuestException e) {
                LOG.warn(entry.getKey().getPackage(), "Error while creating variable on main page in "
                        + profile + " journal: " + e.getMessage(), e);
                text = "error";
            }
            final int number = mainPageEntry.priority();
            numbers.add(number);
            final List<String> linesOrder;
            if (lines.containsKey(number)) {
                linesOrder = lines.get(number);
            } else {
                linesOrder = new ArrayList<>();
                lines.put(number, linesOrder);
            }
            linesOrder.add(text + "§r"); // reset the formatting
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
        generateTexts();
        final Inventory inventory = profile.getOnlineProfile().get().getPlayer().getInventory();
        final ItemStack item;
        try {
            item = getAsItem();
        } catch (final QuestException e) {
            LOG.warn("Failed to get journal as item: " + e.getMessage(), e);
            return;
        }
        final int targetSlot = getJournalSlot();
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
            inventoryFullBackpackSender.sendNotification(profile);
        }
    }

    private int getJournalSlot() {
        final int slot = config.getInt("journal.default_slot");
        final boolean forceJournalSlot = config.getBoolean("journal.lock_default_slot");
        final int oldSlot = removeFromInv();
        if (forceJournalSlot) {
            return slot;
        }
        return oldSlot == -1 ? slot : oldSlot;
    }

    /**
     * Generates the journal as ItemStack.
     *
     * @return the journal ItemStack
     * @throws QuestException if the journal cannot be generated
     */
    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CyclomaticComplexity"})
    public ItemStack getAsItem() throws QuestException {
        final ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        final BookMeta meta = (BookMeta) item.getItemMeta();
        meta.title(pluginMessage.getMessage(profile, "journal_title"));
        meta.setAuthor(profile.getPlayer().getName());
        meta.setCustomModelData(config.getInt("journal.custom_model_data"));
        meta.setLore(getJournalLore(profile));

        // add main page and generate pages from texts
        final List<String> finalList = new ArrayList<>();
        final String color = config.getString("journal.format.color.line");
        if (config.getBoolean("journal.format.one_entry_per_page")) {
            if (mainPage != null && !mainPage.isEmpty()) {
                finalList.addAll(Utils.pagesFromString(mainPage));
            }
            finalList.addAll(getText());
        } else {
            final String line;
            if (config.getBoolean("journal.format.show_separator")) {
                final String separator = config.getString("journal.format.separator");
                line = "\n§" + color + separator + "\n";
            } else {
                line = "\n";
            }

            final StringBuilder stringBuilder = new StringBuilder();
            for (final String entry : getText()) {
                stringBuilder.append(entry).append(line);
            }
            if (mainPage != null && !mainPage.isEmpty()) {
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
            meta.setPages(Utils.multiLineColorCodes(finalList, "§" + color));
        }
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Updates journal by removing it and adding it again.
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
        final OnlineProfile onlineProfile = profile.getOnlineProfile().get();
        final Inventory inventory = onlineProfile.getPlayer().getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (isJournal(onlineProfile, inventory.getItem(i))) {
                inventory.setItem(i, new ItemStack(Material.AIR));
                return i;
            }
        }
        return -1;
    }
}
