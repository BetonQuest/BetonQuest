package org.betonquest.betonquest;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class InstructionParserTest {

    public static Stream<Arguments> parsedRequiredArguments() {
        return Stream.of(
                Arguments.of(" \n id '1st arg' \n ",
                        new String[] {"id", "1st arg" }),
                Arguments.of("'1st arg\n...' '2nd \"arg'",
                        new String[] {"1st arg\n...", "2nd \"arg" }),
                Arguments.of("'required \\'arg\\''",
                        new String[] { "required 'arg'"}),
                Arguments.of("test :op:t:v1",
                        new String[] { "test", ":op:t:v1"}),
                Arguments.of("'hello\\ world' 'hello \\\\\\\\\\' world'",
                        new String[] { "hello world", "hello \\\\' world"})
        );
    }

    public static Stream<Arguments> parsedOptionalArguments() {
        return Stream.of(
          Arguments.of("id opt:v1 test:v2", Map.of("opt", "v1", "test", "v2")),
          Arguments.of("test :opt:v1 test:v2", Map.of("test", "v2")),
          Arguments.of("test:'hello world'", Map.of("test", "hello world")),
          Arguments.of("test:'hello \\'world\\''", Map.of("test", "hello 'world'")),
          Arguments.of("test:v1:v2::", Map.of("test", "v1:v2::"))
        );
    }

    @ParameterizedTest
    @MethodSource("parsedRequiredArguments")
    void testRequiredArguments(final String instruction, final String[] expected) {
        final Instruction.Parser parser = new Instruction.Parser(instruction);
        final Instruction.Data data = parser.parse();
        assertArrayEquals(expected, data.getRequiredArguments());
    }

    @ParameterizedTest
    @MethodSource("parsedOptionalArguments")
    void testOptionalArguments(final String instruction, final Map<String, String> expected) {
        final Instruction.Parser parser = new Instruction.Parser(instruction);
        final Instruction.Data data = parser.parse();
        assertEquals(expected, data.getOptionalArguments());
    }
}
