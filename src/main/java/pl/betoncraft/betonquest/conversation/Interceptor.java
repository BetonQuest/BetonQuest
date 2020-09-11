package pl.betoncraft.betonquest.conversation;

import net.md_5.bungee.api.chat.BaseComponent;

public interface Interceptor {

    /**
     * Send message to player bypassing Interceptor
     */
    void sendMessage(String message);

    void sendMessage(BaseComponent... message);

    /**
     * Ends the work of this interceptor
     */
    void end();
}
