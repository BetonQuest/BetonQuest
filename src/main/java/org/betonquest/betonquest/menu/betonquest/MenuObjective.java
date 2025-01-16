package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.menu.Menu;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.menu.events.MenuOpenEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Completed if menu with given id is opened
 */
@SuppressWarnings("PMD.CommentRequired")
public class MenuObjective extends Objective implements Listener {
    /**
     * The key for the menu property.
     */
    private static final String MENU_PROPERTY = "menu";

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    private final MenuID menuID;

    public MenuObjective(final Instruction instruction) throws QuestException {
        super(instruction);
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
        try {
            this.menuID = new MenuID(instruction.getPackage(), instruction.next());
        } catch (final ObjectNotFoundException e) {
            throw new QuestException("Error while parsing 1 argument: Error while loading menu: " + e.getMessage(), e);
        }
    }

    @EventHandler
    public void onMenuOpen(final MenuOpenEvent event) {
        final Profile profile = event.getProfile();
        if (!containsPlayer(profile)) {
            return;
        }
        if (!event.getMenu().equals(menuID)) {
            return;
        }
        if (!checkConditions(profile)) {
            return;
        }
        this.completeObjective(profile);
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        if (MENU_PROPERTY.equalsIgnoreCase(name)) {
            final Menu menuData = BetonQuest.getInstance().getRpgMenu().getMenu(menuID);
            if (menuData == null) {
                log.debug(instruction.getPackage(), "Error while getting menu property in '" + instruction.getID() + "' objective: "
                        + "menu with id " + menuID + " isn't loaded");
                return "";
            }
            return menuData.getTitle(profile);
        }
        return "";
    }
}
