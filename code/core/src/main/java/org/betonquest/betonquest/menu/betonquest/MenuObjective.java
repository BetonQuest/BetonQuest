package org.betonquest.betonquest.menu.betonquest;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.menu.Menu;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.menu.event.MenuOpenEvent;

/**
 * Completed if menu with given id is opened.
 */
public class MenuObjective extends DefaultObjective {

    /**
     * The key for the menu property.
     */
    private static final String MENU_PROPERTY = "menu";

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The RPGMenu instance.
     */
    private final RPGMenu rpgMenu;

    /**
     * The menu to open.
     */
    private final Argument<MenuID> menuID;

    /**
     * Construct a new Menu Objective from Instruction.
     *
     * @param service the objective factory service
     * @param log     the logger for this objective
     * @param rpgMenu the RPGMenu instance
     * @param menuID  the menu id to open
     * @throws QuestException if the menu id does not exist
     */
    public MenuObjective(final ObjectiveFactoryService service, final BetonQuestLogger log, final RPGMenu rpgMenu, final Argument<MenuID> menuID) throws QuestException {
        super(service);
        this.log = log;
        this.rpgMenu = rpgMenu;
        this.menuID = menuID;
    }

    /**
     * Completes the objective when the matching menu is opened.
     *
     * @param event   the open menu event
     * @param profile the player profile
     */
    public void onMenuOpen(final MenuOpenEvent event, final Profile profile) {
        if (!containsPlayer(profile)) {
            return;
        }
        try {
            if (!event.getMenu().equals(menuID.getValue(profile))) {
                return;
            }
        } catch (final QuestException e) {
            log.debug(getPackage(), "Could not get menu placeholder value in '" + getPackage() + "' objective:"
                    + e.getMessage(), e);
            return;
        }
        if (!checkConditions(profile)) {
            return;
        }
        this.completeObjective(profile);
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        if (MENU_PROPERTY.equalsIgnoreCase(name)) {
            final Menu menuData;
            try {
                menuData = rpgMenu.getMenu(menuID.getValue(profile));
            } catch (final QuestException e) {
                log.warn(getPackage(), "Error while getting menu property in '" + getObjectiveID() + "' objective: "
                        + e.getMessage(), e);
                return "";
            }
            if (menuData == null) {
                log.debug(getPackage(), "Error while getting menu property in '" + getObjectiveID() + "' objective: "
                        + "menu with id " + menuID + " isn't loaded");
                return "";
            }
            try {
                return LegacyComponentSerializer.legacySection().serialize(menuData.getTitle(profile));
            } catch (final QuestException e) {
                log.debug(getPackage(), "Error while getting menu property in '" + getObjectiveID() + "' objective: "
                        + e.getMessage(), e);
            }
        }
        return "";
    }
}
