package org.betonquest.betonquest.menu;

import net.kyori.adventure.text.Component;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Class representing a menu.
 */
@SuppressWarnings("PMD.ShortClassName")
public class Menu {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * The internal id of the menu.
     */
    private final MenuID menuID;

    /**
     * The general Menu Data.
     */
    private final MenuData data;

    /**
     * Item this menu is bound to or is empty if none is bound.
     */
    @Nullable
    private final Variable<Item> boundItem;

    /**
     * Creates a new Menu.
     *
     * @param log          the custom logger for this class
     * @param menuID       the id of the menu
     * @param questTypeAPI the Quest Type API
     * @param menuData     the Menu Data
     * @param boundItem    the optional bound Item
     * @throws QuestException if the bound command is invalid
     */
    public Menu(final BetonQuestLogger log, final MenuID menuID, final QuestTypeAPI questTypeAPI,
                final MenuData menuData, @Nullable final Variable<Item> boundItem) throws QuestException {
        this.log = log;
        this.questTypeAPI = questTypeAPI;
        this.menuID = menuID;
        this.data = menuData;
        this.boundItem = boundItem;
    }

    /**
     * Checks whether a player of the {@link Profile} may open this menu.
     *
     * @param profile the {@link Profile} to check
     * @return true if all opening conditions are true, false otherwise
     */
    public boolean mayOpen(final Profile profile) {
        final List<ConditionID> resolved;
        try {
            resolved = data.openConditions.getValue(profile);
        } catch (final QuestException exception) {
            log.warn(menuID.getPackage(), "Error while resolving open_conditions in menu '" + menuID + "': " + exception.getMessage(), exception);
            return false;
        }
        for (final ConditionID conditionID : resolved) {
            if (!questTypeAPI.condition(profile, conditionID)) {
                log.debug(menuID.getPackage(), "Denied opening of " + menuID + ": Condition " + conditionID + "returned false.");
                return false;
            }
        }
        return true;
    }

    /**
     * Runs all open events for the specified player of the {@link Profile}.
     *
     * @param profile the {@link Profile} to run the events for
     */
    public void runOpenEvents(final OnlineProfile profile) {
        executeEvents(data.openEvents, profile, "open");
    }

    /**
     * Runs all close events for the specified player of the {@link Profile}.
     *
     * @param profile the profile of the player to run the events for
     */
    public void runCloseEvents(final OnlineProfile profile) {
        executeEvents(data.closeEvents, profile, "close");
    }

    private void executeEvents(final Variable<List<EventID>> events, final OnlineProfile profile, final String type) {
        log.debug(menuID.getPackage(), "Menu " + menuID + ": Running " + type + " events");
        final List<EventID> resolved;
        try {
            resolved = events.getValue(profile);
        } catch (final QuestException exception) {
            log.warn(menuID.getPackage(), "Error while resolving " + type + " events in menu item '" + menuID + "': "
                    + exception.getMessage(), exception);
            return;
        }
        for (final EventID event : resolved) {
            log.debug(menuID.getPackage(), "Menu " + menuID + ": Run event " + event);
            questTypeAPI.event(profile, event);
        }
    }

    /**
     * Get the id.
     *
     * @return the menu id of this menu
     */
    public MenuID getMenuID() {
        return menuID;
    }

    /**
     * Get the inventory size.
     *
     * @return the size of the menu in slots
     */
    public final int getSize() {
        return data.height * 9;
    }

    /**
     * Get the title.
     *
     * @param profile the {@link Profile} of the player
     * @return the title of the menu
     * @throws QuestException if the title cannot be parsed
     */
    public Component getTitle(final Profile profile) throws QuestException {
        return data.title.asComponent(profile);
    }

    /**
     * Get the items.
     *
     * @param profile the player of the {@link Profile} to get the items for
     * @return get the items for all slots
     */
    public MenuItem[] getItems(final Profile profile) {
        final MenuItem[] items = new MenuItem[this.getSize()];
        for (int i = 0; i < items.length; i++) {
            items[i] = this.getItem(profile, i);
        }
        return items;
    }

    /**
     * Get the item which interaction opens this menu.
     *
     * @return the bound item, if any
     */
    @Nullable
    public Variable<Item> getBoundItem() {
        return boundItem;
    }

    /**
     * Get a menu item for a specific slot.
     *
     * @param profile the player {@link Profile} to get the item for
     * @param slot    for which the item should be got
     * @return menu item for that slot or null if none is specified
     */
    @Nullable
    public MenuItem getItem(final Profile profile, final int slot) {
        for (final Slots slots : data.slots) {
            if (slots.containsSlot(slot)) {
                try {
                    return slots.getItem(profile, slot);
                } catch (final QuestException e) {
                    log.warn("Could not get Item for slot '" + slots + "': " + e.getMessage(), e);
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Core Data of a Menu.
     *
     * @param title          The title of the menu.
     * @param height         The height of the menu in slots.
     * @param slots          List of all slots objects.
     * @param openConditions Conditions which have to be matched to open the menu.
     * @param openEvents     Events which are fired when the menu is opened.
     * @param closeEvents    Events which are fired when the menu is closed.
     */
    public record MenuData(Message title, int height, List<Slots> slots,
                           Variable<List<ConditionID>> openConditions,
                           Variable<List<EventID>> openEvents, Variable<List<EventID>> closeEvents) {

    }
}
