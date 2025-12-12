package org.betonquest.betonquest.text;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.text.TextParserDecider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagTextParserDeciderTest {

    private final TagTextParserDecider decider = new TagTextParserDecider("default");

    @Test
    void new_line() throws QuestException {
        final TextParserDecider.Result result = decider.chooseParser("§x§9§a§6§6§9§dHello I am Wolf2323.\nHow are you?");
        assertEquals("default", result.parserId(), "The parser id is not correct");
        assertEquals("§x§9§a§6§6§9§dHello I am Wolf2323.\nHow are you?", result.text(), "The text is not correct");
    }

    @Test
    void explicit_parser() throws QuestException {
        final TextParserDecider.Result result = decider.chooseParser("@[test]Hello I am Wolf2323.");
        assertEquals("test", result.parserId(), "The parser id is not correct");
        assertEquals("Hello I am Wolf2323.", result.text(), "The text is not correct");
    }
}
