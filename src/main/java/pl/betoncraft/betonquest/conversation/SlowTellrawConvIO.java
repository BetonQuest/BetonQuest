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
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.LocalChatPaginator;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlowTellrawConvIO extends TellrawConvIO {

    private String npcTextColor;
    private List<String> endLines;

    public SlowTellrawConvIO(Conversation conv, String playerID) {
        super(conv, playerID);
        StringBuilder string = new StringBuilder();
        for (ChatColor color : colors.get("text")) {
            string.append(color);
        }
        this.npcTextColor = string.toString();

    }

    @Override
    public void display() {
        if (npcText == null && options.isEmpty()) {
            end();
            return;
        }

        // NPC Text
        List<String> lines = new ArrayList<>(Arrays.asList(LocalChatPaginator.wordWrap(
                Utils.replaceReset(textFormat.replace("%npc%", npcName) + npcText, npcTextColor),
                60)));

        endLines = new ArrayList<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (lines.size() == 0) {
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

                    // Display endLines
                    for (String message : endLines) {
                        SlowTellrawConvIO.super.print(message);
                    }

                    endLines = null;

                    this.cancel();
                    return;
                }

                conv.sendMessage(lines.remove(0));
            }
        }.runTaskTimer(BetonQuest.getInstance(), 0, 2);
    }

    @Override
    public void print(String message) {
        if (endLines == null) {
            super.print(message);
            return;
        }

        // If endLines is defined, we add to it to be outputted after we have outputted our previous text
        endLines.add(message);
    }
}
