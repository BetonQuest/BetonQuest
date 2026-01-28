package org.betonquest.betonquest.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.ComponentLineWrapper;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.MenuItemIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.text.Text;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * An Item which Is displayed as option in a menu and has some actions that are fired when item is clicked.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class MenuItem {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The Quest TypeAPI.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The betonquest quest item this item is based on.
     */
    private final Argument<ItemWrapper> item;

    /**
     * The ID of this item.
     */
    private final MenuItemIdentifier itemId;

    /**
     * Name and lore for displayed item.
     */
    @Nullable
    private final Text descriptions;

    /**
     * Ids of all actions that should be run on clicks.
     */
    private final ClickActions clickActions;

    /**
     * Conditions that have to be matched to view the item.
     */
    private final Argument<List<ConditionIdentifier>> conditions;

    /**
     * If the menu should be closed when the item is clicked.
     */
    private final Argument<Boolean> close;

    /**
     * Creates a new Menu Item.
     *
     * @param log          the custom logger for this class
     * @param questTypeApi the Quest Type API
     * @param item         the item to display
     * @param itemId       the id of the item
     * @param descriptions the descriptions overriding name and lore of the Item
     * @param clickActions the actions to execute on click
     * @param conditions   the conditions required to show the item
     * @param close        if the item click closes
     */
    public MenuItem(final BetonQuestLogger log, final QuestTypeApi questTypeApi, final Argument<ItemWrapper> item,
                    final MenuItemIdentifier itemId, @Nullable final Text descriptions, final ClickActions clickActions,
                    final Argument<List<ConditionIdentifier>> conditions, final Argument<Boolean> close) {
        this.log = log;
        this.questTypeApi = questTypeApi;
        this.item = item;
        this.itemId = itemId;
        this.descriptions = descriptions;
        this.clickActions = clickActions;
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
            case LEFT -> executeActions(clickActions.leftClick, profile);
            case SHIFT_LEFT -> executeActions(clickActions.shiftLeftClick, profile);
            case RIGHT -> executeActions(clickActions.rightClick, profile);
            case SHIFT_RIGHT -> executeActions(clickActions.shiftRightClick, profile);
            case MIDDLE -> executeActions(clickActions.middleMouseClick, profile);
            default -> false;
        };
    }

    private boolean executeActions(final Argument<List<ActionIdentifier>> actions, final OnlineProfile profile) {
        final List<ActionIdentifier> resolved;
        try {
            resolved = actions.getValue(profile);
        } catch (final QuestException exception) {
            log.warn(itemId.getPackage(), "Error while resolving actions in menu item '" + itemId + "': " + exception.getMessage(), exception);
            return false;
        }
        log.debug(itemId.getPackage(), "Item " + itemId + ": Run actions");
        questTypeApi.actions(profile, resolved);
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
        try {
            final boolean result = questTypeApi.conditions(profile, this.conditions.getValue(profile));
            log.debug(itemId.getPackage(), "Item '" + itemId + "' display check for profile '" + profile + "': " + result);
            return result;
        } catch (final QuestException exception) {
            log.warn(itemId.getPackage(), "Error while resolving condition in menu item '" + itemId + "': " + exception.getMessage(), exception);
            return false;
        }
    }

    /**
     * Generates the menu item for a specific player.
     *
     * @param profile the player from the {@link Profile} this item will be displayed to
     * @return the item as a bukkit item stack
     */
    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
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
    public MenuItemIdentifier getId() {
        return itemId;
    }

    /**
     * Contains the ids of actions that should be run on a click.
     *
     * @param leftClick        for the left click
     * @param shiftLeftClick   for the left click with shift
     * @param rightClick       for the right click
     * @param shiftRightClick  for the right click with shift
     * @param middleMouseClick for the middle mouse click
     */
    public record ClickActions(Argument<List<ActionIdentifier>> leftClick,
                               Argument<List<ActionIdentifier>> shiftLeftClick,
                               Argument<List<ActionIdentifier>> rightClick,
                               Argument<List<ActionIdentifier>> shiftRightClick,
                               Argument<List<ActionIdentifier>> middleMouseClick) {

        /**
         * Fills all click types with the same list.
         *
         * @param click the actions to execute on any click
         */
        public ClickActions(final Argument<List<ActionIdentifier>> click) {
            this(click, click, click, click, click);
        }
    }
}
