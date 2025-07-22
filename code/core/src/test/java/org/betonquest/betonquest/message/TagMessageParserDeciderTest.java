package org.betonquest.betonquest.message;

import org.betonquest.betonquest.api.message.MessageParserDecider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagMessageParserDeciderTest {
    private final TagMessageParserDecider decider = new TagMessageParserDecider("default");

    @Test
    void new_line() throws QuestException {
        final MessageParserDecider.Result result = decider.chooseParser("§x§9§a§6§6§9§dHello I am Wolf2323.\nHow are you?");
        assertEquals("default", result.parserId(), "The parser id is not correct");
        assertEquals("§x§9§a§6§6§9§dHello I am Wolf2323.\nHow are you?", result.message(), "The message is not correct");
    }

    @Test
    void explicit_parser() throws QuestException {
        final MessageParserDecider.Result result = decider.chooseParser("@[test]Hello I am Wolf2323.");
        assertEquals("test", result.parserId(), "The parser id is not correct");
        assertEquals("Hello I am Wolf2323.", result.message(), "The message is not correct");
    }
}
