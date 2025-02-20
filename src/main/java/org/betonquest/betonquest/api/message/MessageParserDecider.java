package org.betonquest.betonquest.api.message;

import org.betonquest.betonquest.api.quest.QuestException;

public interface MessageParserDecider {
    Result chooseParser(String message) throws QuestException;

    record Result(String parserId, String message) {
    }
}
