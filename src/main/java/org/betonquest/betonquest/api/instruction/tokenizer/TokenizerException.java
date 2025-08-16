package org.betonquest.betonquest.api.instruction.tokenizer;

import java.io.Serial;

/**
 * Instruction string tokenizer exception.
 */
public class TokenizerException extends Exception {
    /**
     * Serial version uid for {@link java.io.Serializable} interface.
     */
    @Serial
    private static final long serialVersionUID = -1564590171872246056L;

    /**
     * Create a new tokenizer exception with a message.
     *
     * @param message message about the cause of the tokenization failure
     */
    public TokenizerException(final String message) {
        super(message);
    }
}
