package org.betonquest.betonquest.menu.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.menu.config.RPGMenuConfig;
import org.betonquest.betonquest.menu.utils.Log;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * The plugins main command
 * <p>
 * Created on 14.01.2018
 *
 * @author Jonas Blocher
 */
public class RPGMenuCommand extends SimpleCommand {

    RPGMenu menu = BetonQuest.getInstance().getRpgMenu();

    public RPGMenuCommand() {
        super("rpgmenu", new Permission("betonquest.admin"), 0, "qm", "menu", "menus", "rpgmenus", "rpgm");
        setDescription("Core command of the RPGMenu addon for BetonQuest");
        setUsage("/rpgmenu <reload/open/list>");
        register();
    }

    @Override
    public List<String> simpleTabComplete(final CommandSender sender, final String alias, final String[] args) {
        if (args.length == 1) return Arrays.asList("reload", "open", "list");
        if (args.length > 2) {
            if (args[0].equalsIgnoreCase("open") || args[0].equalsIgnoreCase("o"))
                //return player names
                return null;
            else return new ArrayList<>();
        }
        switch (args[0].toLowerCase()) {
            //complete menu ids
            case "open":
            case "o":
            case "reload":
                if (!args[1].contains(".")) {
                    return new ArrayList<>(Config.getPackages().keySet());
                }
                final String pack = args[1].substring(0, args[1].indexOf("."));
                Log.debug(pack);
                final ConfigPackage configPack = Config.getPackages().get(pack);
                Log.debug(pack);
                if (configPack == null) return new ArrayList<>();
                final List<String> completations = new ArrayList<>();
                for (final MenuID id : menu.getMenus()) {
                    if (id.getPackage().equals(configPack)) completations.add(id.toString());
                    Log.debug(id);
                }
                return completations;
            default:
                return new ArrayList<>();
        }
    }

    @Override
    public boolean simpleCommand(final CommandSender sender, final String alias, final String[] args) {
        if (args == null || args.length == 0) {
            //display command help
            showHelp(sender);
            return false;
        }
        MenuID id = null;
        switch (args[0].toLowerCase()) {
            case "l":
            case "list":
                break;
            case "o":
            case "open":
            case "reload":
                //parse menu id
                if (args.length >= 2) {
                    try {
                        id = new MenuID(null, args[1]);
                    } catch (final ObjectNotFoundException e) {
                        RPGMenuConfig.sendMessage(sender, "command_invalid_menu", args[1]);
                        return false;
                    }
                }
                break;
            default:
                // diplay help
                showHelp(sender);
                return false;
        }
        switch (args[0].toLowerCase()) {
            case "l":
            case "list":
                final ComponentBuilder builder = new ComponentBuilder("");
                builder
                        .append(TextComponent.fromLegacyText(RPGMenuConfig.getMessage(sender, "command_list")));
                final Collection<MenuID> ids = menu.getMenus();
                if (ids.isEmpty()) builder.append("\n - ").color(ChatColor.GRAY);
                else for (final MenuID menuID : ids) {
                    builder
                            .append("\n" + menuID, ComponentBuilder.FormatRetention.FORMATTING)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    TextComponent.fromLegacyText(RPGMenuConfig.getMessage(sender, "click_to_open"))))
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + getName() + " open " + menuID));
                }
                sender.spigot().sendMessage(builder.create());
                return true;
            case "o":
            case "open":
                Player player = null;
                //parse player
                if (args.length >= 3) {
                    player = Bukkit.getPlayer(args[2]);
                    if (player == null) {
                        RPGMenuConfig.sendMessage(sender, "command_invalid_player", args[1]);
                        return false;
                    }
                }
                //handle unspecified players
                if (player == null) {
                    if (!(sender instanceof Player)) {
                        RPGMenuConfig.sendMessage(sender, "command_no_player");
                        return false;
                    } else {
                        player = (Player) sender;
                    }
                }
                //handle unspecified ids
                if (id == null) {
                    RPGMenuConfig.sendMessage(sender, "command_no_menu");
                    return false;
                }
                //open the menu and send feedback
                menu.openMenu(player, id);
                RPGMenuConfig.sendMessage(sender, "command_open_successful", id.toString());
                break;
            case "reload":
                final RPGMenu.ReloadInformation info;
                ChatColor color = ChatColor.GRAY;
                if (id == null) {
                    //reload all data
                    info = menu.reloadData();
                } else {
                    // reload one menu
                    info = menu.reloadMenu(id);
                }
                //notify player, console gets automatically informed
                if (sender instanceof Player) {
                    switch (info.getResult()) {
                        case FAILED:
                            for (final String errorMessage : info.getErrorMessages()) {
                                sender.sendMessage(errorMessage);
                            }
                            RPGMenuConfig.sendMessage(sender, "command_reload_failed");
                            return true;
                        case SUCCESS:
                            color = ChatColor.YELLOW;
                            break;
                        case FULL_SUCCESS:
                            color = ChatColor.GREEN;
                            break;
                    }
                    for (final String errorMessage : info.getErrorMessages()) {
                        sender.sendMessage(errorMessage);
                    }
                    RPGMenuConfig
                            .sendMessage(sender, "command_reload_successful", color.toString(), String.valueOf(info.getLoaded()));
                }
        }
        return true;
    }

    /**
     * Displays the full help message to the command sender
     *
     * @param sender player who issued the command or console
     */
    private void showHelp(final CommandSender sender) {
        final ComponentBuilder builder = new ComponentBuilder("");
        builder
                .append(TextComponent.fromLegacyText("§e----- §aRPGMenu for Betonquest §e-----\n"))
                .append("/rpgmenu reload [menu]\n").color(ChatColor.RED)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        TextComponent.fromLegacyText(RPGMenuConfig.getMessage(sender, "command_info_reload"))))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rpgmenu reload "))
                .append("/rpgmenu open <menu> [player]\n", ComponentBuilder.FormatRetention.FORMATTING)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        TextComponent.fromLegacyText(RPGMenuConfig.getMessage(sender, "command_info_open"))))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rpgmenu open"))
                .append("/rpgmenu list", ComponentBuilder.FormatRetention.FORMATTING)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        TextComponent.fromLegacyText(RPGMenuConfig.getMessage(sender, "command_info_list"))))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rpgmenu list"));
        sender.spigot().sendMessage(builder.create());
    }
}
