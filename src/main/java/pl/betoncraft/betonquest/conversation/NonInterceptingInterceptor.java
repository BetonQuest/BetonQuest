package pl.betoncraft.betonquest.conversation;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class NonInterceptingInterceptor implements Interceptor {

    protected final Conversation conv;
    protected final Player player;

    public NonInterceptingInterceptor(final Conversation conv, final String playerID) {
        this.conv = conv;
        this.player = PlayerConverter.getPlayer(playerID);
    }

    @Override
    public void sendMessage(final String message) {
        player.spigot().sendMessage(TextComponent.fromLegacyText(message));
    }

    @Override
    public void sendMessage(final BaseComponent... message) {
        player.spigot().sendMessage(message);
    }

    @Override
    public void end() {

    }
}
