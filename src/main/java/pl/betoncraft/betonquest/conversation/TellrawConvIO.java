/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.conversation;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Adds tellraw command handling to the SimpleConvIO
 *
 * @author Jakub Sapalski
 */
public class TellrawConvIO extends ChatConvIO {

    protected HashMap<Integer, String> hashes;
    protected ChatColor color;
    protected boolean italic;
    protected boolean bold;
    protected boolean underline;
    protected boolean strikethrough;
    protected boolean magic;
    protected String number;
    private int count = 0;

    public TellrawConvIO(Conversation conv, String playerID) {
        super(conv, playerID);
        hashes = new HashMap<>();
        for (ChatColor color : colors.get("option")) {
            if (color == ChatColor.STRIKETHROUGH) {
                strikethrough = true;
            } else if (color == ChatColor.MAGIC) {
                magic = true;
            } else if (color == ChatColor.ITALIC) {
                italic = true;
            } else if (color == ChatColor.BOLD) {
                bold = true;
            } else if (color == ChatColor.UNDERLINE) {
                underline = true;
            } else {
                this.color = color;
            }
        }
        StringBuilder string = new StringBuilder();
        for (ChatColor color : colors.get("number")) {
            string.append(color);
        }
        string.append("%number%. ");
        number = string.toString();
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommandAnswer(PlayerCommandPreprocessEvent event) {
        if (!event.getPlayer().equals(player))
            return;
        if (!event.getMessage().toLowerCase().startsWith("/betonquestanswer "))
            return;
        event.setCancelled(true);
        String[] parts = event.getMessage().split(" ");
        if (parts.length != 2)
            return;
        String hash = parts[1];
        for (int j = 1; j <= hashes.size(); j++) {
            if (hashes.get(j).equals(hash)) {
                conv.sendMessage(answerFormat + options.get(j));
                conv.passPlayerAnswer(j);
                return;
            }
        }
    }

    @Override
    public void display() {
        super.display();
        for (int j = 1; j <= options.size(); j++) {
            // Build ColorString
            TextComponent colorComponent = new TextComponent();
            colorComponent.setBold(bold);
            colorComponent.setStrikethrough(strikethrough);
            colorComponent.setObfuscated(magic);
            colorComponent.setColor(color.asBungee());
            String colorString = colorComponent.toLegacyText();

            // We avoid ComponentBuilder as it's not available pre 1.9
            List<BaseComponent> parts = new ArrayList<>(Arrays.asList(TextComponent.fromLegacyText(number.replace("%number%", Integer.toString(j)))));
            parts.addAll(Arrays.asList(TextComponent.fromLegacyText(colorString + Utils.replaceReset(StringUtils.stripEnd(options.get(j), "\n"), colorString))));
            for (BaseComponent component : parts) {
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/betonquestanswer " + hashes.get(j)));
            }

            conv.sendMessage(parts.toArray(new BaseComponent[0]));
        }
    }

    @Override
    public void addPlayerOption(String option) {
        super.addPlayerOption(option);
        count++;
        hashes.put(count, UUID.randomUUID().toString());
    }

    @Override
    public void clear() {
        super.clear();
        hashes.clear();
        count = 0;
    }
}
