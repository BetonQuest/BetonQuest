package org.betonquest.betonquest.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.betonquest.betonquest.api.common.component.ComponentLineWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.message.Message;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * An Item which Is displayed as option in a menu and has some events that are fired when item is clicked.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class MenuItem {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The QuestTypeAPI.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * The betonquest quest item this item is based on.
     */
    private final Variable<Item> item;

    /**
     * The ID of this item.
     */
    private final MenuItemID itemId;

    /**
     * Name and lore for displayed item.
     */
    @Nullable
    private final Message descriptions;

    /**
     * Ids of all events that should be run on clicks.
     */
    private final ClickEvents clickEvents;

    /**
     * Conditions that have to be matched to view the item.
     */
    private final Variable<List<ConditionID>> conditions;

    /**
     * If the menu should be closed when the item is clicked.
     */
    private final Variable<Boolean> close;

    /**
     * Creates a new Menu Item.
     *
     * @param log          the custom logger for this class
     * @param questTypeAPI the QuestTypeAPI
     * @param item         the item to display
     * @param itemId       the id of the item
     * @param descriptions the descriptions overriding name and lore of the Item
     * @param clickEvents  the events to execute on click
     * @param conditions   the conditions required to show the item
     * @param close        if the item click closes
     */
    public MenuItem(final BetonQuestLogger log, final QuestTypeAPI questTypeAPI, final Variable<Item> item, final MenuItemID itemId,
                    @Nullable final Message descriptions, final ClickEvents clickEvents,
                    final Variable<List<ConditionID>> conditions, final Variable<Boolean> close) {
        this.log = log;
        this.questTypeAPI = questTypeAPI;
        this.item = item;
        this.itemId = itemId;
        this.descriptions = descriptions;
        this.clickEvents = clickEvents;
        this.conditions = conditions;
        this.close = close;
    }

    /**
     * Action that happens on click.
     *
     * @param profile that has clicked the item
     * @param type    type of the click action
     * @return if the menu should be closed after this operation
     */
    public boolean onClick(final OnlineProfile profile, final ClickType type) {
        return switch (type) {
            case LEFT -> executeEvents(clickEvents.leftClick, profile);
            case SHIFT_LEFT -> executeEvents(clickEvents.shiftLeftClick, profile);
            case RIGHT -> executeEvents(clickEvents.rightClick, profile);
            case SHIFT_RIGHT -> executeEvents(clickEvents.shiftRightClick, profile);
            case MIDDLE -> executeEvents(clickEvents.middleMouseClick, profile);
            default -> false;
        };
    }

    private boolean executeEvents(final Variable<List<EventID>> events, final OnlineProfile profile) {
        final List<EventID> resolved;
        try {
            resolved = events.getValue(profile);
        } catch (final QuestException exception) {
            log.warn(itemId.getPackage(), "Error while resolving events in menu item '" + itemId + "': " + exception.getMessage(), exception);
            return false;
        }
        for (final EventID eventID : resolved) {
            log.debug(itemId.getPackage(), "Item " + itemId + ": Run event " + eventID);
            questTypeAPI.event(profile, eventID);
        }
        try {
            return this.close.getValue(profile);
        } catch (final QuestException e) {
            log.warn(itemId.getPackage(), "Error resolving close for '" + itemId + "': " + e.getMessage(), e);
            return true;
        }
    }

    /**
     * Checks if this item should be displayed to the player.
     *
     * @param profile the player of the {@link Profile} should get the item displayed
     * @return true if all display conditions are met, false otherwise
     */
    public boolean display(final Profile profile) {
        final List<ConditionID> resolved;
        try {
            resolved = this.conditions.getValue(profile);
        } catch (final QuestException exception) {
            log.warn(itemId.getPackage(), "Error while resolving condition in menu item '" + itemId + "': " + exception.getMessage(), exception);
            return false;
        }
        for (final ConditionID condition : resolved) {
            if (questTypeAPI.condition(profile, condition)) {
                log.debug(itemId.getPackage(), "Item " + itemId + ": condition " + condition + " returned true");
            } else {
                log.debug(itemId.getPackage(), "Item " + itemId + " wont be displayed: condition" + condition + " returned false.");
                return false;
            }
        }
        return true;
    }

    /**
     * Generates the menu item for a specific player.
     *
     * @param profile the player from the {@link Profile} this item will be displayed to
     * @return the item as a bukkit item stack
     */
    public ItemStack generateItem(final Profile profile) {
        try {
            final ItemStack item = this.item.getValue(profile).generate(profile);
            if (descriptions != null) {
                Component description = descriptions.asComponent(profile);
                if (description.decoration(TextDecoration.ITALIC) == TextDecoration.State.NOT_SET) {
                    // TODO version switch:
                    //  Replace with decorationIfAbsent with Adventure 4.12 (in Paper 1.19?)
                    description = description.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
                }
                final List<Component> lines = ComponentLineWrapper.splitNewLine(description);
                if (!lines.isEmpty()) {
                    final Component displayName = lines.get(0);
                    final List<Component> lore = lines.subList(1, lines.size());
                    item.editMeta(meta -> {
                        meta.displayName(displayName);
                        if (!lore.isEmpty()) {
                            meta.lore(lore);
                        }
                    });
                }
            }
            return item;
        } catch (final QuestException e) {
            log.error(itemId.getPackage(), "QuestException while creating '" + itemId + "': " + e.getMessage());
            return new ItemStack(Material.AIR);
        }
    }

    /**
     * The id of the menu item.
     *
     * @return the items internal id
     */
    public MenuItemID getId() {
        return itemId;
    }

    /**
     * Contains the ids of events that should be run on a click.
     */
    public record ClickEvents(Variable<List<EventID>> leftClick, Variable<List<EventID>> shiftLeftClick,
                              Variable<List<EventID>> rightClick, Variable<List<EventID>> shiftRightClick,
                              Variable<List<EventID>> middleMouseClick) {

        /**
         * Fills all click types with the same list.
         *
         * @param click the events to execute on any click
         */
        public ClickEvents(final Variable<List<EventID>> click) {
            this(click, click, click, click, click);
        }
    }
}
