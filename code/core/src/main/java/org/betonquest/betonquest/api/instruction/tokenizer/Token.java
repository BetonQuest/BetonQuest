package org.betonquest.betonquest.api.instruction.tokenizer;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a token in a parsable string.
 * <p>
 * A null value means that the token has to have children and only leaf tokens can have a value.
 *
 * @param parent   The parent of the token or null if it is the root
 * @param value    The value of the token
 * @param children The children of the token
 */
public record Token(@Nullable Token parent, @Nullable String value, List<Token> children) {

    /**
     * Creates a new root token without a value.
     */
    public Token() {
        this(null, null);
    }

    /**
     * Creates a new token with the given parent.
     *
     * @param parent the parent of the token
     */
    public Token(final Token parent) {
        this(parent, null);
    }

    /**
     * Creates a new token with the given prefix.
     *
     * @param parent the nullable parent of the token
     * @param value  the nullable value of the token
     */
    public Token(@Nullable final Token parent, @Nullable final String value) {
        this(parent, value, new ArrayList<>());
    }

    /**
     * Resolves the value of the token.
     *
     * @return the value of the token
     */
    public String resolveValue() {
        return value == null ? children.stream().map(Token::value).collect(Collectors.joining("")) : value;
    }

    /**
     * Checks if the token is empty.
     *
     * @return true if the token is empty, false otherwise
     */
    public boolean isEmpty() {
        return value == null && children.isEmpty();
    }

    /**
     * Adds a child to the token.
     *
     * @param child the child to add
     */
    public void addChild(final Token child) {
        children.add(child);
    }

    @Override
    public String toString() {
        return value == null ? "{children=" + children + "}" : "{value='" + value + "'}";
    }
}
