package org.betonquest.betonquest.menu.betonquest;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
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
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(MenuObjective.class);

    private final MenuID menuID;

    public MenuObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        try {
            this.menuID = new MenuID(instruction.getPackage(), instruction.next());
        } catch (final ObjectNotFoundException e) {
            throw new InstructionParseException("Error while parsing 1 argument: Error while loading menu: " + e.getMessage(), e);
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
        if ("menu".equalsIgnoreCase(name)) {
            final Menu menuData = BetonQuest.getInstance().getRpgMenu().getMenu(menuID);
            if (menuData == null) {
                LOG.debug(instruction.getPackage(), "Error while getting menu property in '" + instruction.getID() + "' objective: "
                        + "menu with id " + menuID + " isn't loaded");
                return "";
            }
            return menuData.getTitle(profile);
        }
        return "";
    }
}
