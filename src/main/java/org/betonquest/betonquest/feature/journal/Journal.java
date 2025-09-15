package org.betonquest.betonquest.feature.journal;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.bukkit.event.PlayerJournalAddEvent;
import org.betonquest.betonquest.api.bukkit.event.PlayerJournalDeleteEvent;
import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.common.component.ComponentLineWrapper;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.database.Saver.Record;
import org.betonquest.betonquest.database.UpdateType;
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
@SuppressWarnings({"PMD.TooManyMethods", "PMD.CouplingBetweenObjects", "PMD.GodClass"})
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
     * The Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * The text parser used to parse text in the journal.
     */
    private final TextParser textParser;

    /**
     * The wrapper for formatting book pages.
     */
    private final BookPageWrapper bookWrapper;

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
    private final List<Component> texts = new ArrayList<>();

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
    private Component mainPage;

    /**
     * Creates new Journal instance from List of Pointers.
     *
     * @param pluginMessage the {@link PluginMessage} instance
     * @param questTypeApi  the Quest Type API
     * @param featureApi    the Feature API
     * @param textParser    the {@link TextParser} instance used to parse messages
     * @param fontRegistry  the {@link FontRegistry} used for font handling
     * @param profile       the {@link OnlineProfile} of the player whose journal is created
     * @param list          list of pointers to journal entries
     * @param config        a {@link ConfigAccessor} that contains the plugin's configuration
     */
    public Journal(final PluginMessage pluginMessage, final QuestTypeApi questTypeApi, final FeatureApi featureApi,
                   final TextParser textParser, final FontRegistry fontRegistry,
                   final Profile profile, final List<Pointer> list, final ConfigAccessor config) {
        this.pluginMessage = pluginMessage;
        this.questTypeApi = questTypeApi;
        this.featureApi = featureApi;
        this.textParser = textParser;
        this.bookWrapper = new BookPageWrapper(fontRegistry, config.getInt("journal.format.line_length"),
                config.getInt("journal.format.line_count"));
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
            return title.contains(journalTitle, Utils.COMPONENT_BI_PREDICATE)
                    && Objects.equals(compactList(item.getItemMeta().lore()), compactList(getJournalLore(onlineProfile)));
        } catch (final QuestException e) {
            LOG.warn("Failed to check if the journal's title is correct: " + e.getMessage(), e);
            return false;
        }
    }

    private static List<Component> compactList(@Nullable final List<Component> list) {
        if (list == null) {
            return List.of();
        }
        return list.stream()
                .map(Component::compact)
                .toList();
    }

    private static List<Component> getJournalLore(final Profile profile) throws QuestException {
        return ComponentLineWrapper.splitNewLine(BetonQuest.getInstance().getPluginMessage().getMessage(profile, "journal_lore"));
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
        new PlayerJournalAddEvent(profile, !betonQuest.getServer().isPrimaryThread(), this, pointer).callEvent();
        pointers.add(pointer);
        final String date = betonQuest.isMySQLUsed()
                ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(new Date(pointer.timestamp()))
                : Long.toString(pointer.timestamp());
        betonQuest.getSaver().add(new Record(UpdateType.ADD_JOURNAL, profile.getProfileUUID().toString(),
                pointer.pointer().getFull(), date));
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
                new PlayerJournalDeleteEvent(profile, !betonQuest.getServer().isPrimaryThread(), this, pointer).callEvent();
                final String date = betonQuest.isMySQLUsed()
                        ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(new Date(pointer.timestamp()))
                        : Long.toString(pointer.timestamp());
                betonQuest.getSaver().add(new Record(UpdateType.REMOVE_JOURNAL, profile.getProfileUUID().toString(),
                        pointer.pointer().getFull(), date));
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
    public List<Component> getText() {
        final List<Component> list;
        if (config.getBoolean("journal.format.reversed_order")) {
            list = Lists.reverse(texts);
        } else {
            list = new ArrayList<>(texts);
        }
        final List<Component> pagesList = new ArrayList<>();
        for (final Component entry : list) {
            pagesList.addAll(bookWrapper.splitPages(entry));
        }
        return pagesList;
    }

    /**
     * Generates texts for every pointer and places them inside a List.
     *
     * @throws QuestException if an error occurs while generating the texts
     */
    public void generateTexts() throws QuestException {
        texts.clear();
        mainPage = generateMainPage();
        final boolean displayDatePrefix = !config.getBoolean("journal.format.hide_date");
        for (final Pointer pointer : pointers) {
            final Component datePrefix = displayDatePrefix ? pointer.generateDatePrefix(textParser, config).append(Component.newline()) : Component.empty();
            final JournalEntryID entryID = pointer.pointer();
            final Text journalEntry;
            try {
                journalEntry = featureApi.getJournalEntry(entryID);
            } catch (final QuestException e) {
                LOG.warn(entryID.getPackage(), "Cannot add journal entry to " + profile + ": " + e.getMessage(), e);
                continue;
            }

            Component text;
            try {
                text = journalEntry.asComponent(profile);
            } catch (final QuestException e) {
                LOG.warn(entryID.getPackage(), "Error while creating variable on journal page '" + entryID + "' in "
                        + profile + " journal: " + e.getMessage(), e);
                text = Component.text("error");
            }

            texts.add(Component.empty().append(datePrefix).append(textParser.parse(config.getString("journal.format.color.text")).append(text)));
        }
    }

    /**
     * Generates the main page for this journal.
     *
     * @return the main page string or null, if there is no main page
     */
    @Nullable
    private Component generateMainPage() {
        final Map<Integer, List<Component>> lines = new HashMap<>();
        final Set<Integer> numbers = new HashSet<>();
        for (final Map.Entry<JournalMainPageID, JournalMainPageEntry> entry : featureApi.getJournalMainPages().entrySet()) {
            final JournalMainPageEntry mainPageEntry = entry.getValue();
            Component text;
            try {
                final List<ConditionID> conditions = mainPageEntry.conditions().getValue(profile);
                if (!conditions.isEmpty() && !questTypeApi.conditions(profile, conditions)) {
                    continue;
                }
                text = mainPageEntry.entry().asComponent(profile);
            } catch (final QuestException e) {
                LOG.warn(entry.getKey().getPackage(), "Error while creating variable on main page in "
                        + profile + " journal: " + e.getMessage(), e);
                text = Component.text("error");
            }
            final int number = mainPageEntry.priority();
            numbers.add(number);
            final List<Component> linesOrder;
            if (lines.containsKey(number)) {
                linesOrder = lines.get(number);
            } else {
                linesOrder = new ArrayList<>();
                lines.put(number, linesOrder);
            }
            linesOrder.add(text);
        }
        if (numbers.isEmpty()) {
            return null;
        }
        return sort(numbers, lines);
    }

    @SuppressWarnings("NullAway")
    private Component sort(final Set<Integer> numbers, final Map<Integer, List<Component>> lines) {
        Integer[] sorted = new Integer[numbers.size()];
        sorted = numbers.toArray(sorted);
        Arrays.sort(sorted);
        final List<Component> sortedLines = new ArrayList<>();
        for (final int i : sorted) {
            final List<Component> linesOrder = lines.get(i);
            Component[] sortedLinesOrder = new Component[linesOrder.size()];
            sortedLinesOrder = linesOrder.toArray(sortedLinesOrder);
            Arrays.sort(sortedLinesOrder);
            sortedLines.addAll(Arrays.asList(sortedLinesOrder));
        }
        return Component.join(JoinConfiguration.newlines(), sortedLines);
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
        try {
            generateTexts();
        } catch (final QuestException e) {
            LOG.warn("Failed to generate texts for journal: " + e.getMessage(), e);
            return;
        }
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
    public ItemStack getAsItem() throws QuestException {
        final ItemStack item = new ItemStack(Material.WRITTEN_BOOK);

        final BookMeta meta = (BookMeta) item.getItemMeta();
        meta.title(pluginMessage.getMessage(profile, "journal_title"));
        meta.setAuthor(profile.getPlayer().getName());
        meta.setCustomModelData(config.getInt("journal.custom_model_data"));
        meta.lore(getJournalLore(profile));

        final List<Component> finalList = new ArrayList<>();
        final Component color = textParser.parse(config.getString("journal.format.color.line"));
        if (config.getBoolean("journal.format.one_entry_per_page")) {
            if (mainPage != null) {
                finalList.addAll(bookWrapper.splitPages(mainPage));
            }
            finalList.addAll(getText());
        } else {
            final Component separator = textParser.parse(config.getString("journal.format.separator"));
            final Component line = color.append(separator.append(Component.newline()));

            final TextComponent.Builder stringBuilder = Component.text();
            if (mainPage != null) {
                if (config.getBoolean("journal.full_main_page")) {
                    finalList.addAll(bookWrapper.splitPages(mainPage));
                } else {
                    stringBuilder.append(mainPage).append(line);
                }
            }
            for (final Component entry : getText()) {
                stringBuilder.append(entry).append(line);
            }
            finalList.addAll(bookWrapper.splitPages(stringBuilder.asComponent()));
        }
        if (finalList.isEmpty()) {
            meta.pages(Component.empty());
        } else {
            meta.pages(finalList);
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
