package org.betonquest.betonquest.menu.commands;

import lombok.CustomLog;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.menu.MenuID;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.menu.config.RPGMenuConfig;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * The plugins main command
 */
@CustomLog
@SuppressWarnings("PMD.CommentRequired")
public class RPGMenuCommand extends SimpleCommand {

    private final RPGMenu menu = BetonQuest.getInstance().getRpgMenu();

    public RPGMenuCommand() {
        super("rpgmenu", new Permission("betonquest.admin"), 0, "qm", "menu", "menus", "rpgmenus", "rpgm");
        setDescription("Core command of the RPGMenu addon for BetonQuest");
        setUsage("/rpgmenu <reload/open/list>");
        register();
    }

    @Override
    @SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.AvoidLiteralsInIfCondition", "PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    public List<String> simpleTabComplete(final CommandSender sender, final String alias, final String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "open", "list");
        }
        if (args.length > 2) {
            return new ArrayList<>();
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            //complete menu ids
            case "open":
            case "o":
            case "reload":
                if (!args[1].contains(".")) {
                    return new ArrayList<>(Config.getPackages().keySet());
                }
                final String pack = args[1].substring(0, args[1].indexOf('.'));
                final QuestPackage configPack = Config.getPackages().get(pack);
                if (configPack == null) {
                    return new ArrayList<>();
                }
                final List<String> completions = new ArrayList<>();
                for (final MenuID id : menu.getMenus()) {
                    if (id.getPackage().equals(configPack)) {
                        completions.add(id.toString());
                    }
                }
                return completions;
            default:
                return new ArrayList<>();
        }
    }

    @Override
    @SuppressWarnings({"PMD.SwitchDensity", "PMD.NPathComplexity", "PMD.CyclomaticComplexity", "PMD.NcssCount", "PMD.SwitchStmtsShouldHaveDefault", "PMD.ExcessiveMethodLength", "PMD.AvoidLiteralsInIfCondition", "PMD.CognitiveComplexity"})
    public boolean simpleCommand(final CommandSender sender, final String alias, final String[] args) {
        if (args == null || args.length == 0) {
            //display command help
            showHelp(sender);
            return false;
        }
        MenuID menu = null;
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "l":
            case "list":
                break;
            case "o":
            case "open":
            case "reload":
                //parse menu id
                if (args.length >= 2) {
                    try {
                        menu = new MenuID(null, args[1]);
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
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "l":
            case "list":
                final ComponentBuilder builder = new ComponentBuilder("");
                builder.append(TextComponent.fromLegacyText(RPGMenuConfig.getMessage(sender, "command_list")));
                final Collection<MenuID> ids = this.menu.getMenus();
                if (ids.isEmpty()) {
                    builder.append("\n - ").color(ChatColor.GRAY);
                } else {
                    for (final MenuID menuID : ids) {
                        builder
                                .append("\n" + menuID, ComponentBuilder.FormatRetention.FORMATTING)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        TextComponent.fromLegacyText(RPGMenuConfig.getMessage(sender, "click_to_open"))))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + getName() + " open " + menuID));
                    }
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
                    if (sender instanceof Player) {
                        player = (Player) sender;
                    } else {
                        RPGMenuConfig.sendMessage(sender, "command_no_player");
                        return false;
                    }
                }
                //handle unspecified ids
                if (menu == null) {
                    RPGMenuConfig.sendMessage(sender, "command_no_menu");
                    return false;
                }
                //open the menu and send feedback
                this.menu.openMenu(PlayerConverter.getID(player), menu);
                RPGMenuConfig.sendMessage(sender, "command_open_successful", menu.toString());
                break;
            case "reload":
                final RPGMenu.ReloadInformation info;
                ChatColor color = ChatColor.GRAY;
                if (menu == null) {
                    //reload all data
                    info = this.menu.reloadData();
                } else {
                    // reload one menu
                    info = this.menu.reloadMenu(menu);
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
