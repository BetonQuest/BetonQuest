package org.betonquest.betonquest.notify;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;

import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class ActionBarNotifyIO extends NotifyIO {

    public ActionBarNotifyIO(final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, data);
    }

    @Override
    protected void notifyPlayer(final String message, final OnlineProfile onlineProfile) {
        final BaseComponent[] textMessage = TextComponent.fromLegacyText(message);
        onlineProfile.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, textMessage);
    }
}
