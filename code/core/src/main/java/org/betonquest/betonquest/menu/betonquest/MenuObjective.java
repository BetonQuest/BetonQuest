package org.betonquest.betonquest.menu.betonquest;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.MenuIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.menu.Menu;
import org.betonquest.betonquest.menu.event.MenuOpenEvent;
import org.betonquest.betonquest.menu.kernel.MenuProcessor;

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
     * The MenuProcessor instance.
     */
    private final MenuProcessor menuProcessor;

    /**
     * The menu to open.
     */
    private final Argument<MenuIdentifier> menuID;

    /**
     * Construct a new Menu Objective from Instruction.
     *
     * @param service       the objective service
     * @param log           the logger for this objective
     * @param menuProcessor the MenuProcessor instance
     * @param menuID        the menu id to open
     * @throws QuestException if the menu id does not exist
     */
    public MenuObjective(final ObjectiveService service, final BetonQuestLogger log, final MenuProcessor menuProcessor, final Argument<MenuIdentifier> menuID) throws QuestException {
        super(service);
        this.log = log;
        this.menuProcessor = menuProcessor;
        this.menuID = menuID;
        service.getProperties().setProperty(MENU_PROPERTY, this::getMenuProperty);
    }

    /**
     * Completes the objective when the matching menu is opened.
     *
     * @param event   the open menu event
     * @param profile the player profile
     */
    public void onMenuOpen(final MenuOpenEvent event, final Profile profile) {
        try {
            if (!event.getMenu().equals(menuID.getValue(profile))) {
                return;
            }
        } catch (final QuestException e) {
            log.debug(getPackage(), "Could not get menu placeholder value in '" + getPackage() + "' objective:"
                    + e.getMessage(), e);
            return;
        }
        this.getService().complete(profile);
    }

    private String getMenuProperty(final Profile profile) {
        final Menu menuData;
        try {
            menuData = menuProcessor.get(menuID.getValue(profile));
        } catch (final QuestException e) {
            log.warn(getPackage(), "Error while getting menu property in '" + getObjectiveID() + "' objective: "
                    + e.getMessage(), e);
            return "";
        }
        try {
            return LegacyComponentSerializer.legacySection().serialize(menuData.getTitle(profile));
        } catch (final QuestException e) {
            log.debug(getPackage(), "Error while getting menu property in '" + getObjectiveID() + "' objective: "
                    + e.getMessage(), e);
        }
        return "";
    }
}
