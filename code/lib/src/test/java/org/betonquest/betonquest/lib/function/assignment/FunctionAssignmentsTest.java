package org.betonquest.betonquest.lib.function.assignment;

import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FunctionAssignmentsTest {

    private static Stream<Arguments> assignments() {
        return Stream.of(
                Arguments.of(new StringSourceAssignment("test"), "test", Double.NaN, false),
                Arguments.of(new StringSourceAssignment("55"), "55", 55, true),
                Arguments.of(new StringSourceAssignment("-12.99"), "-12.99", -12.99, false),
                Arguments.of(new StringSourceAssignment("string with spaces"), "string with spaces", Double.NaN, false),
                Arguments.of(new StringSourceAssignment("true"), "true", Double.NaN, true),
                Arguments.of(new StringSourceAssignment("false"), "false", Double.NaN, false),

                Arguments.of(new NumberSourceAssignment(55), "55", 55, true),
                Arguments.of(new NumberSourceAssignment(12.12), "12.12", 12.12, true),
                Arguments.of(new NumberSourceAssignment(-5), "-5", -5, false),
                Arguments.of(new NumberSourceAssignment(1), "1", 1, true),
                Arguments.of(new NumberSourceAssignment(0), "0", 0, false),

                Arguments.of(new BooleanSourceAssignment(true), "true", 1, true),
                Arguments.of(new BooleanSourceAssignment(false), "false", 0, false),

                Arguments.of(new NumericInvertedFunctionAssignment(new NumberSourceAssignment(55)), "-55", -55, false),
                Arguments.of(new NumericInvertedFunctionAssignment(new NumberSourceAssignment(55L)), "-55", -55, false),
                Arguments.of(new NumericInvertedFunctionAssignment(new NumberSourceAssignment(-55)), "55", 55, true),
                Arguments.of(new NumericInvertedFunctionAssignment(new StringSourceAssignment("-55")), "55.0", 55, true),
                Arguments.of(new NumericInvertedFunctionAssignment(new BooleanSourceAssignment(true)), "-1", -1, false),
                Arguments.of(new NumericInvertedFunctionAssignment(new BooleanSourceAssignment(false)), "0", 0, true),

                Arguments.of(new BooleanInvertedFunctionAssignment(new NumberSourceAssignment(55)), "false", 0, false),
                Arguments.of(new BooleanInvertedFunctionAssignment(new NumberSourceAssignment(-55)), "true", 1, true),
                Arguments.of(new BooleanInvertedFunctionAssignment(new StringSourceAssignment("-55")), "true", 1, true),
                Arguments.of(new BooleanInvertedFunctionAssignment(new BooleanSourceAssignment(true)), "false", 0, false),
                Arguments.of(new BooleanInvertedFunctionAssignment(new BooleanSourceAssignment(false)), "true", 1, true)
        );
    }

    @ParameterizedTest
    @MethodSource("assignments")
    void test_numbers(final FunctionAssignment assignment, final String value, final Number number, final boolean booleanValue) {
        assertEquals(number.doubleValue(), assignment.asNumber().doubleValue(), 0.000_001, "Number value should be the same as the original value");
    }

    @ParameterizedTest
    @MethodSource("assignments")
    void test_strings(final FunctionAssignment assignment, final String value, final Number number, final boolean booleanValue) {
        assertEquals(value, assignment.asString(), "String value should be the same as the original value");
        assertEquals(value, assignment.toString(), "toString value should be the same as the original value");
    }

    @ParameterizedTest
    @MethodSource("assignments")
    void test_booleans(final FunctionAssignment assignment, final String value, final Number number, final boolean booleanValue) {
        assertEquals(booleanValue, assignment.asBoolean(), "Boolean value should be the same as the original value");
    }
}
