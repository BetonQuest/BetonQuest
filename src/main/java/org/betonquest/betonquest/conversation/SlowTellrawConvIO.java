package org.betonquest.betonquest.conversation;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.utils.LocalChatPaginator;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class SlowTellrawConvIO extends TellrawConvIO {

    private final String npcTextColor;
    private List<String> endLines;

    public SlowTellrawConvIO(final Conversation conv, final String playerID) {
        super(conv, playerID);
        final StringBuilder string = new StringBuilder();
        for (final ChatColor color : colors.get("text")) {
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
        final List<String> lines = new ArrayList<>(Arrays.asList(LocalChatPaginator.wordWrap(
                Utils.replaceReset(textFormat.replace("%npc%", npcName) + npcText, npcTextColor),
                60)));

        endLines = new ArrayList<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (lines.isEmpty()) {
                    for (int j = 1; j <= options.size(); j++) {
                        // Build ColorString
                        final TextComponent colorComponent = new TextComponent();
                        colorComponent.setBold(bold);
                        colorComponent.setStrikethrough(strikethrough);
                        colorComponent.setObfuscated(magic);
                        colorComponent.setColor(color.asBungee());
                        final String colorString = colorComponent.toLegacyText();

                        // We avoid ComponentBuilder as it's not available pre 1.9
                        final List<BaseComponent> parts = new ArrayList<>(Arrays.asList(TextComponent.fromLegacyText(number.replace("%number%", Integer.toString(j)))));
                        parts.addAll(Arrays.asList(TextComponent.fromLegacyText(colorString + Utils.replaceReset(StringUtils.stripEnd(options.get(j), "\n"), colorString))));
                        for (final BaseComponent component : parts) {
                            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/betonquestanswer " + hashes.get(j)));
                        }

                        conv.sendMessage(parts.toArray(new BaseComponent[0]));
                    }

                    // Display endLines
                    for (final String message : endLines) {
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
    public void print(final String message) {
        if (endLines == null) {
            super.print(message);
            return;
        }

        // If endLines is defined, we add to it to be outputted after we have outputted our previous text
        endLines.add(message);
    }
}
