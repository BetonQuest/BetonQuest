package org.betonquest.betonquest.util.math;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.kernel.processor.adapter.PlaceholderAdapter;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerExtension;
import org.betonquest.betonquest.util.math.tokens.Token;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test the {@link Tokenizer}.
 */
@SuppressWarnings({"deprecation", "PMD.CyclomaticComplexity"})
@ExtendWith(BetonQuestLoggerExtension.class)
class TokenizerTest {

    /**
     * Precision up to which to check equality of floating point numbers.
     */
    private static final double REQUIRED_DOUBLE_PRECISION = 1E-7;

    /**
     * The profile to use for argument resolution.
     */
    private static final Profile TEST_PLAYER_PROFILE = mock(Profile.class);

    /**
     * The package name of the package we assume to be inside for argument resolution.
     */
    private static final QuestPackage TEST_PACK = mock(QuestPackage.class);

    /**
     * The {@link PlaceholderManager} to create and resolve placeholders.
     */
    private final PlaceholderManager placeholders = mock(PlaceholderManager.class);

    @BeforeAll
    static void setUp() {
        when(TEST_PACK.getQuestPath()).thenReturn("testPack");
    }

    @Test
    void tokenize_empty() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(""), "an empty string should not be parsable");
        assertEquals("missing calculation", exception.getMessage(), "exception should be about missing a calculation");
    }

    @Test
    void tokenize_negation_only() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize("-"), "a string containing only a negation should throw an exception");
        assertEquals("invalid calculation (negation missing value)", exception.getMessage(), "exception should be about missing something to negate");
    }

    @Test
    void tokenize_digit() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize("1");
        assertEquals(1, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a single letter integer should work");
    }

    @Test
    void tokenize_integer() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final double number = 2_134_671;

        final Token result = tokenizer.tokenize(String.valueOf(number));
        assertEquals(number, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a multi letter integer should work");
    }

    @Test
    void tokenize_negative_integer() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final double number = -56_234;

        final Token result = tokenizer.tokenize(String.valueOf(number));
        assertEquals(number, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a negative integer should work");
    }

    @Test
    void tokenize_decimal() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final double number = 45_213.234_5;

        final Token result = tokenizer.tokenize(String.valueOf(number));
        assertEquals(number, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a decimal should work");
    }

    @Test
    void tokenize_negative_decimal() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final double number = -7_342.634;

        final Token result = tokenizer.tokenize(String.valueOf(number));
        assertEquals(number, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a negative decimal should work");
    }

    @Test
    void tokenize_two_decimal_points() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String number = "43.654.123";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(number), "tokenizing a string with digits that contain two decimal point symbols should throw an exception");
        assertEquals("invalid calculation (missing operator)", exception.getMessage(), "exception should be about missing operator");
    }

    @Test
    void tokenize_integer_addition() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "1+2";
        final double expectedResult = 3;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing addition of two integers should work");
    }

    @Test
    void tokenize_integer_subtraction() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "1-2";
        final double expectedResult = -1;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing subtraction of two integers should work");
    }

    @Test
    void tokenize_integer_multiplication() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "2*3";
        final double expectedResult = 6;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing multiplication of two integers should work");
    }

    @Test
    void tokenize_integer_division() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "6/3";
        final double expectedResult = 2;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing division of two integers should work");
    }

    @Test
    void tokenize_decimal_addition() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "1.7+3.5";
        final double expectedResult = 5.2;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing addition of two decimals should work");
    }

    @Test
    void tokenize_decimal_subtraction() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "4.6-5.3";
        final double expectedResult = -0.7;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing subtraction of two decimals should work");
    }

    @Test
    void tokenize_decimal_multiplication() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "2.4*5.3";
        final double expectedResult = 12.72;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing multiplication of two decimals should work");
    }

    @Test
    void tokenize_decimal_division() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "7.8/3.2";
        final double expectedResult = 2.4375;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing division of two decimals should work");
    }

    @Test
    void tokenize_add_negative_number() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "18.3+-23.4";
        final double expectedResult = -5.1;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing addition of a negative number onto a number should work");
    }

    @Test
    void tokenize_subtract_negative_number() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "7--5";
        final double expectedResult = 12;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing subtraction of a negative number from a number should work");
    }

    @Test
    void tokenize_multiply_negative_number() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "1.3*-2";
        final double expectedResult = -2.6;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing multiplication with a negative number should work");
    }

    @Test
    void tokenize_dot_rule_add_then_multiple() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "13+2*4";
        final double expectedResult = 21;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with first addition, then multiplication should first multiply, then add");
    }

    @Test
    void tokenize_dot_rule_multiple_then_add() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "8*3+14";
        final double expectedResult = 38;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with first multiplication, then addition should first multiply, then add");
    }

    @Test
    void tokenize_dot_rule_minus_then_divide() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "3.7-12/24";
        final double expectedResult = 3.2;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with first subtraction, then division should first divide, then subtract");
    }

    @Test
    void tokenize_dot_rule_divide_then_minus() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "14/4-0.7";
        final double expectedResult = 2.8;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with first division, then subtraction should first divide, then subtract");
    }

    @Test
    void tokenize_dot_rule_multiply_then_subtraction() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "10-2*3-4";
        final double expectedResult = 0;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a multiplication between two subtractions should not group the trailing subtraction into the multiplication");
    }

    @Test
    void tokenize_dot_rule_divide_then_subtraction() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "10-4/2-3";
        final double expectedResult = 5;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a division between two subtractions should not group the trailing subtraction into the division");
    }

    @Test
    void tokenize_dot_rule_modulo() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "5+13%7-2";
        final double expectedResult = 9;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with addition, modulo and subtraction, then modulo should be done before adding and subtracting");
    }

    @Test
    void tokenize_power_rule_exponent_then_multiply() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "3^4*2";
        final double expectedResult = 162;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with first the power operator, then multiplication the power should be calculated before doing the multiplication");
    }

    @Test
    void tokenize_power_rule_multiply_then_exponent() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "0.3*2^5";
        final double expectedResult = 9.6;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with first multiplication, then the power operator the power should be calculated before doing the multiplication");
    }

    @Test
    void tokenize_round_to_integer() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "2.2~0";
        final double expectedResult = 2;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "the round operator should round the first value to the given number of decimal digits when tokenized");
    }

    @Test
    void tokenize_round_edge_case() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "3.05~1";
        final double expectedResult = 3.1;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenized round operator should also properly round edge cases like 0.5");
    }

    @Test
    void tokenize_round_result() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "10-3.03*4~1";
        final double expectedResult = -2.1;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "round operator should have lowest priority when tokenized");
    }

    @Test
    void tokenize_round_in_parenthesis() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "(4.0345~2)*4";
        final double expectedResult = 16.12;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "rounding inside parenthesis should work to when tokenized");
    }

    @Test
    void tokenize_round_parenthesis() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "(4.0345*4)~2";
        final double expectedResult = 16.14;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "rounding the content of a parenthesis should also tokenize");
    }

    @Test
    void tokenize_round_power() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "2.5^3~2";
        final double expectedResult = 15.63;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "round operator should have lower priority then power operator");
    }

    @Test
    void tokenize_round_negative_digits() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "1234~-2";
        final double expectedResult = 1200;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "round operator should work with negative number of decimal digits");
    }

    @Test
    void tokenize_round_minus() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "13.243~7-5";
        final double expectedResult = 13.24;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "round operator should work if a mathematical expression is given as number of decimal digits");
    }

    @Test
    void tokenize_nothing_times_something() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "*64";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string without value before the first operator should throw an exception");
        assertEquals("invalid calculation (operator missing first value)", exception.getMessage(), "exception should be about parenthesis / brackets mismatch");
    }

    @Test
    void tokenize_something_times_nothing() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "423*";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string without value after the last operator should throw an exception");
        assertEquals("invalid calculation (operator missing second value)", exception.getMessage(), "exception should be about parenthesis / brackets mismatch");
    }

    @Test
    void tokenize_double_times_operator() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "423**64";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with two adjacent plus operators should throw an exception");
        assertEquals("invalid calculation (doubled operators)", exception.getMessage(), "exception should be about parenthesis / brackets mismatch");
    }

    @Test
    void tokenize_simple_parenthesis() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "(4123)";
        final double expectedResult = 4123;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a number inside a parenthesis should work");
    }

    @Test
    void tokenize_simple_double_parenthesis() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "((4123))";
        final double expectedResult = 4123;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a number inside multiple parenthesis should work");
    }

    @Test
    void tokenize_calculation_in_parenthesis() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "(423+543)";
        final double expectedResult = 966;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a calculation inside a parenthesis should work");
    }

    @Test
    void tokenize_multiple_of_sum() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "2.3*(32+12+65)";
        final double expectedResult = 250.7;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string multiplying the result of a calculation inside a parenthesis should work");
    }

    @Test
    void tokenize_deep_parenthesis() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "1-(45+((23-5)*4))/6";
        final double expectedResult = -18.5;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a calculation with multiple layers of calculations inside parenthesis should work");
    }

    @Test
    void tokenize_chained_parenthesis() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "(14+6)*(4-1)/(8*2)+(12/3)";
        final double expectedResult = 7.75;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a series of calculations in parenthesis should work");
    }

    @Test
    void tokenize_deep_and_chained_parenthesis() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "((1-1)*2)/1.5+(3/4)";
        final double expectedResult = 0.75;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a mix of parenthesis inside and sequential should work; issue #1421");
    }

    @Test
    void tokenize_negative_parenthesis() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "-(15.4-32.9)";
        final double expectedResult = 17.5;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the negation of the result of a calculation inside parenthesis should work");
    }

    @Test
    void tokenize_missing_opening_parenthesis() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "865)";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with a missing opening parenthesis should throw an exception");
        assertEquals("invalid calculation (unbalanced parenthesis)", exception.getMessage(), "exception should be about parenthesis imbalance");
    }

    @Test
    void tokenize_missing_closing_parenthesis() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "(846";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with a missing closing parenthesis should throw an exception");
        assertEquals("invalid calculation (unbalanced parenthesis)", exception.getMessage(), "exception should be about parenthesis imbalance");
    }

    @Test
    void tokenize_empty_parenthesis() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "()";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with an empty parenthesis should throw an exception");
        assertEquals("invalid calculation (empty parenthesis)", exception.getMessage(), "exception should be about empty parenthesis");
    }

    @Test
    void tokenize_negated_empty_parenthesis() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "-()";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with a negated empty parenthesis should throw an exception");
        assertEquals("invalid calculation (empty parenthesis)", exception.getMessage(), "exception should be about empty parenthesis");
    }

    @Test
    void tokenize_nested_empty_parenthesis() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "(())";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with a nested empty parenthesis should throw an exception");
        assertEquals("invalid calculation (empty parenthesis)", exception.getMessage(), "exception should be about empty parenthesis");
    }

    @Test
    void tokenize_nested_empty_parenthesis_with_calculation() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "(()+5)";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with a nested empty parenthesis followed by a calculation should throw an exception");
        assertEquals("invalid calculation (empty parenthesis)", exception.getMessage(), "exception should be about empty parenthesis");
    }

    @Test
    void tokenize_number_then_parenthesis_without_operator() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "7(2+4)";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string that connects a number with a parenthesis without operator should throw an exception");
        assertEquals("invalid calculation (missing operator)", exception.getMessage(), "exception should be about missing operator");
    }

    @Test
    void tokenize_parenthesis_then_number_without_operator() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "(2+4)7";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string that connects a parenthesis with a number without operator should throw an exception");
        assertEquals("invalid calculation (missing operator)", exception.getMessage(), "exception should be about missing operator");
    }

    @Test
    void tokenize_simple_brackets() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "[546]";
        final double expectedResult = 546;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a number inside a bracket should work");
    }

    @Test
    void tokenize_simple_double_brackets() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "[[546]]";
        final double expectedResult = 546;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a number inside multiple brackets should work");
    }

    @Test
    void tokenize_calculation_in_brackets() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "[329+603]";
        final double expectedResult = 932;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a calculation inside a bracket should work");
    }

    @Test
    void tokenize_parenthesis_in_brackets() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "[(8345)]";
        final double expectedResult = 8345;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a number inside a parenthesis and then a bracket should work");
    }

    @Test
    void tokenize_brackets_in_parenthesis() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "([8345])";
        final double expectedResult = 8345;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a number inside a bracket and then a parenthesis should work");
    }

    @Test
    void tokenize_invalid_parenthesis_and_brackets_mix() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "[(653])";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with interleaved brackets and parenthesis should throw an exception");
        assertEquals("invalid calculation (parenthesis / brackets mismatch)", exception.getMessage(), "exception should be about parenthesis / brackets mismatch");
    }

    @Test
    void tokenize_absolute_of_positive() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "|5|";
        final double expectedResult = 5;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the calculation of the absolute of a positive number should work");
    }

    @Test
    void tokenize_absolute_of_negative() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "|-7|";
        final double expectedResult = 7;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the calculation of the absolute of a negative number should work");
    }

    @Test
    void tokenize_absolute_of_negative_decimal() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "|-32.65|";
        final double expectedResult = 32.65;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the calculation of the absolute of a negative decimal should work");
    }

    @Test
    void tokenize_negative_absolute() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "-|-23.5|";
        final double expectedResult = -23.5;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the negation of the result of the absolute should work");
    }

    @Test
    void tokenize_difference_of_absolutes() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "|-3|-|-7|";
        final double expectedResult = -4;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the difference of two absolutes should work");
    }

    @Test
    void tokenize_absolute_of_absolute_in_parenthesis() throws QuestException {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "|1-(|3|)|";
        final double expectedResult = 2;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the absolute another absolute wrapped in parenthesis should work");
    }

    @Test
    void tokenize_empty_absolute() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "||";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing an empty absolute value should throw an exception");
        assertEquals("invalid calculation (empty absolute value)", exception.getMessage(), "exception should be about empty absolute value");
    }

    @Test
    void tokenize_negated_empty_absolute() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "-||";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a negated empty absolute value should throw an exception");
        assertEquals("invalid calculation (empty absolute value)", exception.getMessage(), "exception should be about empty absolute value");
    }

    @Test
    void tokenize_number_then_absolute_without_operator() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "7|2+4|";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string that connects a number with an absolute without operator should throw an exception");
        assertEquals("invalid calculation (missing operator)", exception.getMessage(), "exception should be about missing operator");
    }

    @Test
    void tokenize_absolute_then_number_without_operator() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "|2+4|7";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string that connects an absolute with a number without operator should throw an exception");
        assertEquals("invalid calculation (missing operator)", exception.getMessage(), "exception should be about missing operator");
    }

    @Test
    void tokenize_invalid_absolute_and_parenthesis_mix() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "|(653|)";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with interleaved absolutes and parenthesis should throw an exception");
        assertEquals("invalid calculation (unbalanced absolute value)", exception.getMessage(), "exception should be about parenthesis / absolute mismatch");
    }

    @Test
    void tokenize_invalid_parenthesis_and_absolute_mix() {
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);
        final String calculation = "(|653)|";

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with interleaved absolutes and parenthesis should throw an exception");
        assertEquals("invalid calculation (unbalanced absolute value)", exception.getMessage(), "exception should be about parenthesis / absolute mismatch");
    }

    @Test
    void tokenize_variable() throws Throwable {
        final String variable = "var";
        final double value = 43;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable should work");
    }

    @Test
    void tokenize_variable_with_number() throws Throwable {
        final String variable = "var2";
        final double value = 44;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing a number should work");
    }

    @Test
    void tokenize_variable_with_underscore() throws Throwable {
        final String variable = "var_able";
        final double value = 45;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing an underscore should work");
    }

    @Test
    void tokenize_variable_with_bang() throws Throwable {
        final String variable = "var!bang";
        final double value = 47;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing an exclamation mark should work");
    }

    @Test
    void tokenize_variable_with_equals() throws Throwable {
        final String variable = "var=qu";
        final double value = 48;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing an equals sign should work");
    }

    @Test
    void tokenize_variable_with_number_sign() throws Throwable {
        final String variable = "var#rav";
        final double value = 49;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing a number sign should work");
    }

    @Test
    void tokenize_variable_with_ampersand() throws Throwable {
        final String variable = "var&more";
        final double value = 50;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing an ampersand should work");
    }

    @Test
    void tokenize_variable_with_apostrophe() throws Throwable {
        final String variable = "variable's";
        final double value = 51;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing an apostrophe should work");
    }

    @Test
    void tokenize_variable_with_question_mark() throws Throwable {
        final String variable = "var?not";
        final double value = 52;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing a question mark should work");
    }

    @Test
    void tokenize_variable_with_dot() throws Throwable {
        final String variable = "var.net";
        final double value = 53;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing a dot should work");
    }

    @Test
    void tokenize_variable_with_colon() throws Throwable {
        final String variable = "var:res";
        final double value = 54;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing a colon should work");
    }

    @Test
    void tokenize_variable_ending_with_number() throws Throwable {
        final String variable = "var123";
        final double value = 53;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable ending with a digit should work");
    }

    @Test
    void tokenize_negate_variable() throws Throwable {
        final String variable = "var";
        final String calculation = '-' + variable;
        final double value = 23;

        withVariables(new ProtoVariable(variable, String.valueOf(-value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the negation of a variable should work");
    }

    @Test
    void tokenize_number_plus_variable() throws Throwable {
        final String variable = "var";
        final String calculation = "5+" + variable;
        final double value = 7;
        final double expectedResult = 12;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the addition of a number to a variable should work");
    }

    @Test
    void tokenize_variable_plus_number() throws Throwable {
        final String variable = "var";
        final String calculation = variable + "+7";
        final double value = 5;
        final double expectedResult = 12;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the addition of a variable to a number should work");
    }

    @Test
    void tokenize_addition_of_variables() throws Throwable {
        final String firstVariable = "var1";
        final String secondVariable = "var2";
        final String calculation = firstVariable + '+' + secondVariable;
        final double firstValue = 64;
        final double secondValue = 32;
        final double expectedResult = 96;

        withVariables(new ProtoVariable(firstVariable, String.valueOf(firstValue)), new ProtoVariable(secondVariable, String.valueOf(secondValue)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the addition of two placeholders should work");
    }

    @Test
    void tokenize_variable_in_parenthesis() throws Throwable {
        final String variable = "var";
        final String calculation = "(" + variable + ")";
        final double value = 6354.6;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the a variable inside parenthesis should work");
    }

    @Test
    void tokenize_number_then_variable_without_operator() throws Throwable {
        final String variable = "var";
        final String calculation = "756.39" + variable;
        final double value = 57.1;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string that connects a number with a variable without operator should throw an exception");
        assertEquals("invalid calculation (missing operator)", exception.getMessage(), "exception should be about missing operator");
    }

    @Test
    void tokenize_variable_then_parenthesis_without_operator() throws Throwable {
        final String variable = "var";
        final String calculation = variable + "(1+3)";
        final double value = 5;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string that connects a variable with a parenthesis without operator should throw an exception");
        assertEquals("invalid calculation (missing operator)", exception.getMessage(), "exception should be about missing operator");
    }

    @Test
    void tokenize_parenthesis_then_variable_without_operator() throws Throwable {
        final String variable = "var";
        final String calculation = "(1+3)" + variable;
        final double value = 8;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string that connects a parenthesis with a variable without operator should throw an exception");
        assertEquals("invalid calculation (missing operator)", exception.getMessage(), "exception should be about missing operator");
    }

    @Test
    void tokenize_curly_braces_variable() throws Throwable {
        final String variable = "var";
        final String calculation = "{" + variable + "}";
        final double value = 65;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the a curly braces variable should work");
    }

    @Test
    void tokenize_negated_curly_braces_variable() throws Throwable {
        final String variable = "var";
        final String calculation = "-{" + variable + "}";
        final double value = 66;
        final double expected = -value;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expected, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the a curly braces variable should work");
    }

    @Test
    void tokenize_curly_braces_variable_with_escaped_backslash() throws Throwable {
        final String variable = "back\\slash";
        final String calculation = "{back\\\\slash}";
        final double value = 17;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the a curly braces variable with escaped backslash should work");
    }

    @Test
    void tokenize_curly_braces_variable_with_escaped_closing_brace() throws Throwable {
        final String variable = "closing{curly}brace";
        final String calculation = "{closing{curly\\}brace}";
        final double value = 27;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the a curly braces variable with escaped closing curly brace should work");
    }

    @Test
    void tokenize_curly_braces_variable_containing_math_expression() throws Throwable {
        final String variable = "math:5+(7-9*var+|[|-3|+(8)]|)";
        final String calculation = "{" + variable + "}";
        final double value = 32;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the a curly braces variable with escaped closing curly brace should work");
    }

    @Test
    void tokenize_curly_braces_variable_containing_double_escaped_backslash() throws Throwable {
        final String variable = "\\\\";
        final String calculation = "{\\\\\\\\}";
        final double value = 87;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the a curly braces variable with two backslashes in a row should work");
    }

    @Test
    void tokenize_missing_opening_curly_brace() throws Throwable {
        final String variable = "invalid";
        final String calculation = variable + "}";

        withVariables(new ProtoVariable(variable, "83"));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string that connects a parenthesis with a variable without operator should throw an exception");
        assertEquals("invalid calculation (unbalanced curly brace)", exception.getMessage(), "exception should be about missing operator");
    }

    @Test
    void tokenize_missing_closing_curly_brace() throws Throwable {
        final String variable = "invalid";
        final String calculation = "{" + variable;

        withVariables(new ProtoVariable(variable, "83"));
        final Tokenizer tokenizer = new Tokenizer(placeholders, TEST_PACK);

        final QuestException exception = assertThrows(QuestException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string that connects a parenthesis with a variable without operator should throw an exception");
        assertEquals("invalid calculation (unbalanced curly brace)", exception.getMessage(), "exception should be about missing operator");
    }

    private void withVariables(final ProtoVariable... variables) throws Throwable {
        for (final ProtoVariable variableTemplate : variables) {
            final PlayerPlaceholder var = mock(PlayerPlaceholder.class);
            when(var.getValue(TEST_PLAYER_PROFILE)).thenReturn(variableTemplate.value());
            final PlaceholderAdapter varWrapper = new PlaceholderAdapter(mock(Instruction.class), var, null);
            when(this.placeholders.create(TEST_PACK, "%" + variableTemplate.key() + "%")).thenReturn(varWrapper);
        }
    }

    /**
     * Array-safe container for variable-name to variable-value pairs.
     *
     * @param key   The variable key.
     * @param value The variable value.
     */
    private record ProtoVariable(String key, String value) {

    }
}
