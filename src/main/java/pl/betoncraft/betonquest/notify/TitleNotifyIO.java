package pl.betoncraft.betonquest.notify;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;

import java.util.Map;
import java.util.regex.Pattern;

public class TitleNotifyIO extends NotifyIO {

    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    private static final Pattern REPLACE_PATTERN = Pattern.compile("((?:^|\\G|[^\\\\])(?:\\\\\\\\)*)_");
    private String subtitle;

    public TitleNotifyIO(final Map<String, String> data) throws InstructionParseException {
        super(data);

        fadeIn = getIntegerData("fadein", 10);
        stay = getIntegerData("stay", 70);
        fadeOut = getIntegerData("fadeout", 20);

        subtitle = data.getOrDefault("subtitle", "");
        subtitle = REPLACE_PATTERN.matcher(subtitle).replaceAll("$1 ");
        subtitle = subtitle.replace("\\_", "_");
        subtitle = subtitle.replace("\\\\", "\\");
    }

    @Override
    protected void notifyPlayer(final String message, final Player player) {
        //For some reason this needs to be done manually for subtitles...
        subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
        player.sendTitle(message, subtitle, fadeIn, stay, fadeOut);
    }
}
