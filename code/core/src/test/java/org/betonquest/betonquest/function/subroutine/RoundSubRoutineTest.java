package org.betonquest.betonquest.function.subroutine;

import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionProvider;
import org.betonquest.betonquest.lib.function.assignment.NumberSourceAssignment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoundSubRoutineTest {

    private RoundSubRoutine roundSubRoutine;

    @BeforeEach
    void setUp() {
        roundSubRoutine = new RoundSubRoutine();
    }

    @Test
    void round_without_precision() throws Exception {
        final FunctionProvider provider = mock(FunctionProvider.class);
        final Map<String, FunctionAssignment> args = Map.of(
                "x", new NumberSourceAssignment(2.6),
                "precision", new NumberSourceAssignment(0)
        );
        final FunctionAssignment result = roundSubRoutine.evaluateSubroutine(provider, args);
        assertEquals(3.0, result.asNumber().doubleValue(), 0.000_001, "round(2.6, 0) should equal 3.0");
    }

    @Test
    void round_with_precision_zero() throws Exception {
        final FunctionProvider provider = mock(FunctionProvider.class);
        final Map<String, FunctionAssignment> args = Map.of(
                "x", new NumberSourceAssignment(2.2),
                "precision", new NumberSourceAssignment(0)
        );
        final FunctionAssignment result = roundSubRoutine.evaluateSubroutine(provider, args);
        assertEquals(2.0, result.asNumber().doubleValue(), 0.000_001, "round(2.2, 0) should equal 2.0");
    }

    @ParameterizedTest
    @CsvSource({
            "3.05, 1, 3.1",
            "2.567, 2, 2.57",
            "1.999, 2, 2.0",
            "123.456, 1, 123.5",
            "0.12345, 4, 0.1235",
            "4.0345, 2, 4.03",
            "2.5, 3, 2.5"
    })
    void round_with_precision_decimal_places(final double input, final double precision, final double expected) throws Exception {
        final FunctionProvider provider = mock(FunctionProvider.class);
        final Map<String, FunctionAssignment> args = Map.of(
                "x", new NumberSourceAssignment(input),
                "precision", new NumberSourceAssignment(precision)
        );
        final FunctionAssignment result = roundSubRoutine.evaluateSubroutine(provider, args);
        assertEquals(expected, result.asNumber().doubleValue(), 0.000_001,
                "round(" + input + ", " + precision + ") should equal " + expected);
    }

    @Test
    void round_negative_value() throws Exception {
        final FunctionProvider provider = mock(FunctionProvider.class);
        final Map<String, FunctionAssignment> args = Map.of(
                "x", new NumberSourceAssignment(-2.567),
                "precision", new NumberSourceAssignment(2)
        );
        final FunctionAssignment result = roundSubRoutine.evaluateSubroutine(provider, args);
        assertEquals(-2.57, result.asNumber().doubleValue(), 0.000_001, "round(-2.567, 2) should equal -2.57");
    }

    @Test
    void round_edge_case_0_5() throws Exception {
        final FunctionProvider provider = mock(FunctionProvider.class);
        final Map<String, FunctionAssignment> args = Map.of(
                "x", new NumberSourceAssignment(0.5),
                "precision", new NumberSourceAssignment(0)
        );
        final FunctionAssignment result = roundSubRoutine.evaluateSubroutine(provider, args);
        assertEquals(1.0, result.asNumber().doubleValue(), 0.000_001, "round(0.5, 0) should equal 1.0");
    }
}
