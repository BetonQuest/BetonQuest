package org.betonquest.betonquest.lib.function;

import org.betonquest.betonquest.lib.function.token.FunctionToken;
import org.betonquest.betonquest.lib.function.token.FunctionTokenType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionTokenTest {

    @Test
    void make_sure_only_valid_tokens_resolve_their_value() {
        final FunctionToken validToken = new FunctionToken(FunctionTokenType.STRING, "\"test\"");
        assertEquals("test", validToken.containedValue(), "Valid token should resolve its value");
        final FunctionToken invalidToken = new FunctionToken(FunctionTokenType.STRING, "55");
        assertEquals("55", invalidToken.containedValue(), "Invalid token should not resolve its value and return it raw");
    }
}
