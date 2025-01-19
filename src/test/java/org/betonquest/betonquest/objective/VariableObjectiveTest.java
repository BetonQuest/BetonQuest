package org.betonquest.betonquest.objective;

import org.betonquest.betonquest.api.profile.Profile;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VariableObjectiveTest {
    public static Stream<Arguments> serializedVariableObjectiveData() {
        return Stream.of(
                Arguments.of("", "any", null),
                Arguments.of("test:data", "test", "data"),
                Arguments.of("test:data", "missing", null),
                Arguments.of("one:1\ntwo:22\nthree:333", "one", "1"),
                Arguments.of("one:1\ntwo:22\nthree:333", "two", "22"),
                Arguments.of("one:1\ntwo:22\nthree:333", "three", "333"),
                Arguments.of("one:1\ntwo:22\nthree:333", "four", null),
                Arguments.of("newline:This is a\\nnewline test!", "newline", "This is a\nnewline test!"),
                Arguments.of("newline:This is a\nnewline test!", "newline", "This is a\nnewline test!"),
                Arguments.of("newline:This is a\\\\nnewline test!", "newline", "This is a\\nnewline test!"),
                Arguments.of("newline:This is a\nnewline test!", "newline test!", null),
                Arguments.of("multi-newline:This\\nis\\na\\nmulti\\nnewline\\ntest!", "multi-newline", "This\nis\na\nmulti\nnewline\ntest!"),
                Arguments.of("multi-newline:This\nis\na\nmulti\nnewline\ntest!", "multi-newline", "This\nis\na\nmulti\nnewline\ntest!"),
                Arguments.of("multi-newline:This\\\\nis\\\\na\\\\nmulti\\\\nnewline\\\\ntest!", "multi-newline", "This\\nis\\na\\nmulti\\nnewline\\ntest!"),
                Arguments.of("first-newliner:This is a\\nnewline test!\nsecond-newliner:This also\\ncontains a newline!", "first-newliner", "This is a\nnewline test!"),
                Arguments.of("first-newliner:This is a\\nnewline test!\nsecond-newliner:This also\\ncontains a newline!", "second-newliner", "This also\ncontains a newline!"),
                Arguments.of("first-newliner:This is a\nnewline test!\nsecond-newliner:This also\ncontains a newline!", "first-newliner", "This is a\nnewline test!"),
                Arguments.of("first-newliner:This is a\nnewline test!\nsecond-newliner:This also\ncontains a newline!", "second-newliner", "This also\ncontains a newline!"),
                Arguments.of("first-newliner:This is a\\\\nnewline test!\nsecond-newliner:This also\\\\ncontains a newline!", "first-newliner", "This is a\\nnewline test!"),
                Arguments.of("first-newliner:This is a\\\\nnewline test!\nsecond-newliner:This also\\\\ncontains a newline!", "second-newliner", "This also\\ncontains a newline!"),
                Arguments.of("contains_colon:This: Is a test.", "contains_colon", "This: Is a test."),
                Arguments.of("contains_colon:This\\: Is a test.", "contains_colon", "This: Is a test."),
                Arguments.of("rou\\ge:backslash", "rou\\ge", "backslash"),
                Arguments.of("rou\\\\ge:backslash", "rou\\ge", "backslash"),
                Arguments.of("rouge:back\\slash", "rouge", "back\\slash"),
                Arguments.of("rouge:back\\\\slash", "rouge", "back\\slash"),
                Arguments.of("evil\\:key:Should still work correctly.", "evil:key", "Should still work correctly."),
                Arguments.of("space in key:Whyyouask?WhynotIanswer!", "space in key", "Whyyouask?WhynotIanswer!"),
                Arguments.of("test:works\nspace in second key:Whyyouask?WhynotIanswer!", "test", "works"),
                Arguments.of("test:works\nspace in second key:Whyyouask?WhynotIanswer!", "space in second key", "Whyyouask?WhynotIanswer!"),
                Arguments.of("ending\\::\\:beginning", "ending:", ":beginning"),
                Arguments.of("escaped_escape\\\\:does_not_escape", "escaped_escape\\", "does_not_escape"),
                Arguments.of(" starts_with_space : ends with space ", " starts_with_space ", " ends with space "),
                Arguments.of("newline\\nin_key?:That's fancy!", "newline\nin_key?", "That's fancy!"),
                Arguments.of("newline\\\\nin_key?:That's fancy!", "newline\\nin_key?", "That's fancy!"),
                Arguments.of("test:works\nnewline_in\\nsecond_key?:That's fancy!", "test", "works"),
                Arguments.of("test:works\nnewline_in\\nsecond_key?:That's fancy!", "newline_in\nsecond_key?", "That's fancy!"),
                Arguments.of("test:works\n\\\\:Only backslash key!", "test", "works"),
                Arguments.of("test:works\n\\\\:Only backslash key!", "\\", "Only backslash key!"),
                Arguments.of("test:works\n\\\\\\\\\\\\:Multi backslash key!", "test", "works"),
                Arguments.of("test:works\n\\\\\\\\\\\\:Multi backslash key!", "\\\\\\", "Multi backslash key!")
        );
    }

    public static Stream<Arguments> serializableVariableObjectiveData() {
        return Stream.of(
                Arguments.of(Collections.emptyMap(), ""),
                Arguments.of(Collections.singletonMap("test", "data"), "test:data"),
                Arguments.of(linkedMapOf("one", "1", "two", "22", "three", "333"), "one:1\ntwo:22\nthree:333"),
                Arguments.of(Collections.singletonMap("newline", "This is a\nnewline test!"), "newline:This is a\\nnewline test!"),
                Arguments.of(Collections.singletonMap("multi-newline", "This\nis\na\nmulti\nnewline\ntest!"), "multi-newline:This\\nis\\na\\nmulti\\nnewline\\ntest!"),
                Arguments.of(linkedMapOf("first-newliner", "This is a\nnewline test!", "second-newliner", "This also\ncontains a newline!"), "first-newliner:This is a\\nnewline test!\nsecond-newliner:This also\\ncontains a newline!"),
                Arguments.of(Collections.singletonMap("contains_colon", "This: Is a test."), "contains_colon:This\\: Is a test."),
                Arguments.of(Collections.singletonMap("rouge", "back\\slash"), "rouge:back\\\\slash"),
                Arguments.of(Collections.singletonMap("rou\\ge", "backslash"), "rou\\\\ge:backslash"),
                Arguments.of(Collections.singletonMap("evil:key", "Should still work correctly."), "evil\\:key:Should still work correctly."),
                Arguments.of(Collections.singletonMap("space in key", "Whyyouask?WhynotIanswer!"), "space in key:Whyyouask?WhynotIanswer!"),
                Arguments.of(linkedMapOf("test", "works", "space in second key", "Whyyouask?WhynotIanswer!"), "test:works\nspace in second key:Whyyouask?WhynotIanswer!"),
                Arguments.of(Collections.singletonMap("ending:", ":beginning"), "ending\\::\\:beginning"),
                Arguments.of(Collections.singletonMap("escaped_escape\\", "does_not_escape"), "escaped_escape\\\\:does_not_escape"),
                Arguments.of(Collections.singletonMap(" starts_with_space ", " ends with space "), " starts_with_space : ends with space "),
                Arguments.of(Collections.singletonMap("newline\nin_key?", "That's fancy!"), "newline\\nin_key?:That's fancy!"),
                Arguments.of(linkedMapOf("test", "works", "newline_in\nsecond_key?", "That's fancy!"), "test:works\nnewline_in\\nsecond_key?:That's fancy!"),
                Arguments.of(linkedMapOf("test", "works", "\\", "Only backslash key!"), "test:works\n\\\\:Only backslash key!"),
                Arguments.of(linkedMapOf("test", "works", "\\\\\\", "Multi backslash key!"), "test:works\n\\\\\\\\\\\\:Multi backslash key!")
        );
    }

    public static Stream<Map<String, String>> variableObjectiveData() {
        return Stream.of(
                Collections.emptyMap(),
                Collections.singletonMap("test", "data"),
                linkedMapOf("one", "1", "two", "22", "three", "333"),
                Collections.singletonMap("newline", "This is a\nnewline test!"),
                Collections.singletonMap("multi-newline", "This\nis\na\nmulti\nnewline\ntest!"),
                linkedMapOf("first-newliner", "This is a\nnewline test!", "second-newliner", "This also\ncontains a newline!"),
                Collections.singletonMap("contains_colon", "This: Is a test."),
                Collections.singletonMap("rouge", "back\\slash"),
                Collections.singletonMap("rou\\ge", "backslash"),
                Collections.singletonMap("evil:key", "Should still work correctly."),
                Collections.singletonMap("space in key", "Whyyouask?WhynotIanswer!"),
                linkedMapOf("test", "works", "space in second key", "Whyyouask?WhynotIanswer!"),
                Collections.singletonMap("ending:", ":beginning"),
                Collections.singletonMap("escaped_escape\\", "does_not_escape"),
                Collections.singletonMap(" starts_with_space ", " ends with space "),
                Collections.singletonMap("newline\nin_key?", "That's fancy!"),
                linkedMapOf("test", "works", "newline_in\nsecond_key?", "That's fancy!"),
                linkedMapOf("test", "works", "\\", "Only backslash key!"),
                linkedMapOf("test", "works", "\\\\\\", "Multi backslash key!"),
                Collections.singletonMap("test", "double-escaped\\\\newline?")
        );
    }

    private static Map<String, String> linkedMapOf(final String... values) {
        final Map<String, String> map = new LinkedHashMap<>();
        for (int index = 0; index < values.length - 1; index += 2) {
            map.put(values[index], values[index + 1]);
        }
        return map;
    }

    @ParameterizedTest
    @MethodSource("serializedVariableObjectiveData")
    void loading_variables_from_serialized_data(final String serializedData, final String expectedKey, final String expectedValue, @Mock final Profile profile) {
        final VariableObjective.VariableData data = new VariableObjective.VariableData(serializedData, profile, "");
        final String value = data.get(expectedKey);
        assertEquals(expectedValue, value, "Values from deserialized variable objective instruction should be correct.");
    }

    @ParameterizedTest
    @MethodSource("serializableVariableObjectiveData")
    void storing_variables_as_serialized_data(final Map<String, String> variables, final String expectedData) {
        final String serialized = VariableObjective.VariableData.serializeData(variables);
        assertEquals(expectedData, serialized, "Serialized variable objective data instruction should be correct.");
    }

    @ParameterizedTest
    @MethodSource("variableObjectiveData")
    void cycling_variables_through_serialization(final Map<String, String> variables) {
        final String serialized = VariableObjective.VariableData.serializeData(variables);
        final Map<String, String> deserialized = VariableObjective.VariableData.deserializeData(serialized);
        assertEquals(variables, deserialized, "Variables should be the same after going through serialization.");
    }
}
