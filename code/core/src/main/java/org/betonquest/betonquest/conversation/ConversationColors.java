package org.betonquest.betonquest.conversation;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.text.TextParser;

/**
 * Holds the colors of the conversations.
 */
@SuppressWarnings("PMD.DataClass")
public class ConversationColors {

    /**
     * The text parser used to parse the colors.
     */
    private final TextParser textParser;

    /**
     * The config accessor used to get the colors from the config.
     */
    private final FileConfigAccessor config;

    /**
     * The color of the text in the conversation.
     */
    private Component text;

    /**
     * The color of the NPC name in the conversation.
     */
    private Component npc;

    /**
     * The color of the player name in the conversation.
     */
    private Component player;

    /**
     * The color of the number in the conversation.
     */
    private Component number;

    /**
     * The color of the answer in the conversation.
     */
    private Component answer;

    /**
     * The color of the option in the conversation.
     */
    private Component option;

    /**
     * The constructor of the ConversationColors class.
     *
     * @param textParser the text parser used to parse the colors
     * @param config     the config accessor used to get the colors from the config
     */
    public ConversationColors(final TextParser textParser, final FileConfigAccessor config) {
        this.textParser = textParser;
        this.config = config;
        this.text = Component.empty();
        this.npc = Component.empty();
        this.player = Component.empty();
        this.number = Component.empty();
        this.answer = Component.empty();
        this.option = Component.empty();
    }

    /**
     * Loads all the colors from the config.
     *
     * @throws QuestException if the config is not valid
     */
    public void load() throws QuestException {
        text = getColor("conversation.color.text");
        npc = getColor("conversation.color.npc");
        player = getColor("conversation.color.player");
        number = getColor("conversation.color.number");
        answer = getColor("conversation.color.answer");
        option = getColor("conversation.color.option");
    }

    private Component getColor(final String name) throws QuestException {
        final String raw = config.getString(name);
        if (raw == null) {
            throw new QuestException("Conversation color '" + name + "' does not exist in the config!");
        }
        return textParser.parse(raw);
    }

    /**
     * The color of the text in the conversation.
     *
     * @return the Component representing the text color
     */
    public Component getText() {
        return text;
    }

    /**
     * The color of the NPC name in the conversation.
     *
     * @return the Component representing the NPC color
     */
    public Component getNpc() {
        return npc;
    }

    /**
     * The color of the player name in the conversation.
     *
     * @return the Component representing the player color
     */
    public Component getPlayer() {
        return player;
    }

    /**
     * The color of the number in the conversation.
     *
     * @return the Component representing the number color
     */
    public Component getNumber() {
        return number;
    }

    /**
     * The color of the answer in the conversation.
     *
     * @return the Component representing the answer color
     */
    public Component getAnswer() {
        return answer;
    }

    /**
     * The color of the option in the conversation.
     *
     * @return the Component representing the option color
     */
    public Component getOption() {
        return option;
    }
}
