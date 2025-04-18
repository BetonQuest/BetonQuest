package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.menu.Menu;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.menu.event.MenuOpenEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Completed if menu with given id is opened.
 */
public class MenuObjective extends Objective implements Listener {
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
    private final MenuID menuID;

    /**
     * Construct a new Menu Objective from Instruction.
     *
     * @param instruction the instruction to get the id from
     * @param log         the logger for this objective
     * @param rpgMenu     the RPGMenu instance
     * @param menuID      the menu id to open
     * @throws QuestException if the menu id does not exist
     */
    public MenuObjective(final Instruction instruction, final BetonQuestLogger log, final RPGMenu rpgMenu, final MenuID menuID) throws QuestException {
        super(instruction);
        this.log = log;
        this.rpgMenu = rpgMenu;
        this.menuID = menuID;
    }

    /**
     * Completes the objective when the matching menu is opened.
     *
     * @param event the open menu event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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
            final Menu menuData = rpgMenu.getMenu(menuID);
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
