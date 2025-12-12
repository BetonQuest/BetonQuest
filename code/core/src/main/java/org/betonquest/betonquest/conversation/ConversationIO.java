package org.betonquest.betonquest.conversation;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Used to display messages in conversation.
 */
public interface ConversationIO {

    /**
     * Starts the io.
     */
    void begin();

    /**
     * Set the text of response chosen by the NPC. Should be called once per
     * conversation cycle.
     *
     * @param npcName  the name of the NPC
     * @param response the text the NPC chose
     */
    void setNpcResponse(Component npcName, Component response);

    /**
     * Adds the text of the player option. Should be called for each option in a conversation cycle.
     *
     * @param option     the text of an option
     * @param properties the property configuration section for the text
     * @throws QuestException if the option cannot be added, e.g., if the properties are invalid
     */
    void addPlayerOption(Component option, ConfigurationSection properties) throws QuestException;

    /**
     * Displays all data to the player. Should be called after setting all
     * options.
     */
    void display();

    /**
     * Clears the data. Should be called before the cycle begins to ensure
     * nothing is left from previous one.
     */
    void clear();

    /**
     * Ends the work of this conversation IO. Should be called when the
     * conversation ends.
     *
     * @param callback the callback to execute after the conversation has actually ended
     */
    void end(Runnable callback);
}
