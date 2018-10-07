package pl.betoncraft.betonquest.conversation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.ChatPaginator;
import pl.betoncraft.betonquest.BetonQuest;
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
        List<String> lines = new ArrayList<>(Arrays.asList(ChatPaginator.wordWrap(
                Utils.multiLineColorCodes(textFormat.replace("%npc%", npcName) + npcText, npcTextColor),
                ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH - 2)));

        endLines = new ArrayList<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (lines.size() == 0) {
                    // Display Options
                    for (int j = 1; j <= options.size(); j++) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                                "tellraw " + name + " [{\"text\":\"" + number.replace("%number%", Integer.toString(j))
                                        + "\"},{\"text\":\"" + options.get(j) + "\",\"color\":\"" + color + "\",\"bold\":\"" + bold
                                        + "\",\"italic\":\"" + italic + "\",\"underlined\":\"" + underline
                                        + "\",\"strikethrough\":\"" + strikethrough + "\",\"obfuscated\":\"" + magic
                                        + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/betonquestanswer "
                                        + hashes.get(j) + "\"}}]");
                    }

                    // Display endLines
                    for (String message : endLines) {
                        SlowTellrawConvIO.super.print(message);
                    }

                    endLines = null;

                    this.cancel();
                    return;
                }

                player.sendMessage(lines.remove(0));
            }
        }.runTaskTimer(BetonQuest.getPlugin(), 0, 2);
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
