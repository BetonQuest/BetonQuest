package org.betonquest.betonquest.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.ComponentLineWrapper;
import org.betonquest.betonquest.api.config.Translations;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.item.typehandler.QuestHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static org.betonquest.betonquest.feature.journal.Journal.JOURNAL_KEY;
import static org.betonquest.betonquest.item.typehandler.QuestHandler.QUEST_ITEM_KEY;

/**
 * Adds the {@link QuestHandler#QUEST_ITEM_KEY} to all "legacy" Quest Items
 * and {@link Journal#JOURNAL_KEY} to all "legacy" Journals on player join.
 */
public class QuestItemConvertListener implements Listener {

    /**
     * Custom logger for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Config to load from.
     */
    private final Supplier<Boolean> migrateSupplier;

    /**
     * Plugin message to get the quest item and journal lines.
     */
    private final Translations translations;

    /**
     * Profile provider to get profiles for message resolving.
     */
    private final ProfileProvider profileProvider;

    /**
     * Create a new Convert Listener.
     *
     * @param log             the custom logger for this class
     * @param migrateSupplier the supplier for if the ItemStacks in inventories should be updated
     * @param translations    the plugin message to get the quest item and journal lines
     * @param profileProvider the profile provider to get profiles for message resolving
     */
    public QuestItemConvertListener(final BetonQuestLogger log, final Supplier<Boolean> migrateSupplier,
                                    final Translations translations, final ProfileProvider profileProvider) {
        this.log = log;
        this.migrateSupplier = migrateSupplier;
        this.translations = translations;
        this.profileProvider = profileProvider;
    }

    /**
     * Set the tags on legacy items.
     *
     * @param event the join event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(final PlayerJoinEvent event) {
        if (!migrateSupplier.get()) {
            return;
        }
        final OnlineProfile profile = profileProvider.getProfile(event.getPlayer());
        final Component questItemLine;
        final Component journalTitle;
        final List<Component> journalLines;
        try {
            questItemLine = translations.getMessage(null, "quest_item");
            journalTitle = translations.getMessage(profile, "journal_title");
            journalLines = ComponentLineWrapper.splitNewLine(translations.getMessage(profile, "journal_title"));
        } catch (final QuestException e) {
            log.warn("Can't migrate items on player join, failed to load message for profile '" + profile + "': "
                    + e.getMessage(), e);
            return;
        }
        for (final ItemStack stack : event.getPlayer().getInventory()) {
            if (stack == null || !stack.hasItemMeta()) {
                continue;
            }
            final ItemMeta meta = stack.getItemMeta();
            if (!meta.hasLore()) {
                continue;
            }
            if (isJournal(meta, journalTitle, journalLines)) {
                meta.getPersistentDataContainer().set(JOURNAL_KEY, PersistentDataType.BYTE, (byte) 1);
                stack.setItemMeta(meta);
            } else if (meta.lore().stream().anyMatch(line -> line.contains(questItemLine, this::contains))) {
                meta.getPersistentDataContainer().set(QUEST_ITEM_KEY, PersistentDataType.BYTE, (byte) 1);
                stack.setItemMeta(meta);
            }
        }
    }

    private boolean isJournal(final ItemMeta meta, final Component journalTitle, final List<Component> journalLines) {
        return meta instanceof final BookMeta bookMeta && bookMeta.hasTitle()
                && bookMeta.title().contains(journalTitle, this::contains)
                && Objects.equals(compactList(meta.lore()), compactList(journalLines));
    }

    private List<Component> compactList(@Nullable final List<Component> list) {
        if (list == null) {
            return List.of();
        }
        return list.stream().map(Component::compact).toList();
    }

    private boolean contains(final Component component, final Component part) {
        if (!(component instanceof final TextComponent componentText) || !(part instanceof final TextComponent partText)) {
            return false;
        }
        if (!componentText.content().equals(partText.content())) {
            return false;
        }
        final Style componentStyle = componentText.style();
        final Style partStyle = partText.style();
        if (!Objects.equals(componentStyle.color(), partStyle.color())) {
            return false;
        }
        final Map<TextDecoration, TextDecoration.State> componentDecorations = componentStyle.decorations();
        final Map<TextDecoration, TextDecoration.State> partDecorations = partStyle.decorations();
        for (final Map.Entry<TextDecoration, TextDecoration.State> entry : componentDecorations.entrySet()) {
            final TextDecoration.State componentState = entry.getValue();
            final TextDecoration.State partState = partDecorations.get(entry.getKey());
            if (componentState == TextDecoration.State.NOT_SET || partState == TextDecoration.State.NOT_SET) {
                continue;
            }
            if (componentState != partState) {
                return false;
            }
        }
        return true;
    }
}
