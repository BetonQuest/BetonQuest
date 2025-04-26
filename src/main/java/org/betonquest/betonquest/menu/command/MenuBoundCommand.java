package org.betonquest.betonquest.menu.command;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.menu.Menu;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.menu.RPGMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command which can be used to open the gui.
 * To perform the command a player must match all open conditions.
 */
public class MenuBoundCommand extends SimpleCommand {

    /**
     * The RPGMenu "plugin" instance to open menus.
     */
    private final RPGMenu rpgMenu;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Menu to open.
     */
    private final Menu menu;

    /**
     * Creates a new command for opening this menu.
     *
     * @param log             the custom logger
     * @param rpgMenu         the rpg menu instance to open menus
     * @param profileProvider the profile provider instance
     * @param menu            the menu to open
     * @param name            the command name
     */
    public MenuBoundCommand(final BetonQuestLogger log, final RPGMenu rpgMenu, final ProfileProvider profileProvider,
                            final Menu menu, final String name) {
        super(log, name, 0);
        this.menu = menu;
        this.rpgMenu = rpgMenu;
        this.profileProvider = profileProvider;
    }

    @Override
    public boolean simpleCommand(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage("Command can only be run by players!");
            return false;
        }
        final OnlineProfile onlineProfile = profileProvider.getProfile(player);
        if (menu.mayOpen(onlineProfile)) {
            final MenuID menuID = menu.getMenuID();
            log.debug(menuID.getPackage(), onlineProfile + " run bound command of " + menuID);
            try {
                rpgMenu.openMenu(onlineProfile, menuID);
                return true;
            } catch (final QuestException e) {
                log.error(menu.getMenuID().getPackage(), "Could not open menu '" + menuID + "': " + e.getMessage(), e);
                return false;
            }
        } else {
            sendMessage(sender, "no_permission");
            return false;
        }
    }
}
