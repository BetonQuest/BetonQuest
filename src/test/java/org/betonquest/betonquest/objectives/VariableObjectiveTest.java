package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.api.profiles.Profile;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VariableObjectiveTest {
    @ParameterizedTest
    @MethodSource("serializedVariableObjectiveData")
    void testLoadingVariable(final String serializedData, final String expectedKey, final String expectedValue, @Mock final Profile profile) {
        final VariableObjective.VariableData data = new VariableObjective.VariableData(serializedData, profile, "");
        final String value = data.get(expectedKey);
        assertEquals(expectedValue, value, "Values from deserialized variable objective instruction should be correct.");
    }

    public static Stream<Arguments> serializedVariableObjectiveData() {
        return Stream.of(
                Arguments.of("", "any", null),
                Arguments.of("test:data", "test", "data"),
                Arguments.of("test:data", "missing", null),
                Arguments.of("one:1\ntwo:22\nthree:333", "one", "1"),
                Arguments.of("one:1\ntwo:22\nthree:333", "two", "22"),
                Arguments.of("one:1\ntwo:22\nthree:333", "three", "333"),
                Arguments.of("one:1\ntwo:22\nthree:333", "four", null),
                Arguments.of("newline:This is a\nnewline test!", "newline", "This is a\nnewline test!"),
                Arguments.of("newline:This is a\nnewline test!", "newline test!", null),
                Arguments.of("multi-newline:This\nis\na\nmulti\nnewline\ntest!", "multi-newline", "This\nis\na\nmulti\nnewline\ntest!"),
                Arguments.of("first-newliner:This is a\nnewline test!\nsecond-newliner:This also\ncontains a newline!", "first-newliner", "This is a\nnewline test!"),
                Arguments.of("first-newliner:This is a\nnewline test!\nsecond-newliner:This also\ncontains a newline!", "second-newliner", "This also\ncontains a newline!")
        );
    }
}
