package org.betonquest.betonquest.api.message;

import org.jetbrains.annotations.Nullable;

public interface MessageParserRegistry {
    void registerParser(String name, MessageParser parser);

    @Nullable
    MessageParser getParser(String name);
}
