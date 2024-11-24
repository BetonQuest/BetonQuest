package org.betonquest.betonquest.utils.math;

import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.utils.math.tokens.Token;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test the {@link Tokenizer}.
 */
@SuppressWarnings({"deprecation", "PMD.CyclomaticComplexity"})
@ExtendWith(BetonQuestLoggerService.class)
class TokenizerTest {
    /**
     * Precision up to which to check equality of floating point numbers.
     */
    public static final double REQUIRED_DOUBLE_PRECISION = 1E-7;

    /**
     * The profile to use for variable resolution.
     */
    public static final Profile TEST_PLAYER_PROFILE = mock(Profile.class);

    /**
     * The package name of the package we assume to be inside for variable resolution.
     */
    private static final QuestPackage TEST_PACK = mock(QuestPackage.class);

    /**
     * The variable processor to use for variable resolution.
     */
    private final VariableProcessor variableProcessor = mock(VariableProcessor.class);

    @BeforeAll
    static void setUp() {
        when(TEST_PACK.getQuestPath()).thenReturn("testPack");
    }

    @Test
    void testTokenizeEmpty() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(""), "an empty string should not be parsable");
        assertEquals("missing calculation", exception.getMessage(), "exception should be about missing a calculation");
    }

    @Test
    void testTokenizeNegationOnly() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize("-"), "a string containing only a negation should throw an exception");
        assertEquals("invalid calculation (negation missing value)", exception.getMessage(), "exception should be about missing something to negate");
    }

    @Test
    void testTokenizeDigit() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize("1");
        assertEquals(1, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a single letter integer should work");
    }

    @Test
    void testTokenizeInteger() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final double number = 2_134_671;

        final Token result = tokenizer.tokenize(String.valueOf(number));
        assertEquals(number, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a multi letter integer should work");
    }

    @Test
    void testTokenizeNegativeInteger() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final double number = -56_234;

        final Token result = tokenizer.tokenize(String.valueOf(number));
        assertEquals(number, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a negative integer should work");
    }

    @Test
    void testTokenizeDecimal() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final double number = 45_213.234_5;

        final Token result = tokenizer.tokenize(String.valueOf(number));
        assertEquals(number, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a decimal should work");
    }

    @Test
    void testTokenizeNegativeDecimal() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final double number = -7_342.634;

        final Token result = tokenizer.tokenize(String.valueOf(number));
        assertEquals(number, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a negative decimal should work");
    }

    @Test
    void testTokenizeTwoDecimalPoints() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String number = "43.654.123";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(number), "tokenizing a string with digits that contain two decimal point symbols should throw an exception");
        assertEquals("invalid calculation (missing operator)", exception.getMessage(), "exception should be about missing operator");
    }

    @Test
    void testTokenizeIntegerAddition() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "1+2";
        final double expectedResult = 3;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing addition of two integers should work");
    }

    @Test
    void testTokenizeIntegerSubtraction() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "1-2";
        final double expectedResult = -1;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing subtraction of two integers should work");
    }

    @Test
    void testTokenizeIntegerMultiplication() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "2*3";
        final double expectedResult = 6;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing multiplication of two integers should work");
    }

    @Test
    void testTokenizeIntegerDivision() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "6/3";
        final double expectedResult = 2;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing division of two integers should work");
    }

    @Test
    void testTokenizeDecimalAddition() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "1.7+3.5";
        final double expectedResult = 5.2;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing addition of two decimals should work");
    }

    @Test
    void testTokenizeDecimalSubtraction() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "4.6-5.3";
        final double expectedResult = -0.7;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing subtraction of two decimals should work");
    }

    @Test
    void testTokenizeDecimalMultiplication() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "2.4*5.3";
        final double expectedResult = 12.72;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing multiplication of two decimals should work");
    }

    @Test
    void testTokenizeDecimalDivision() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "7.8/3.2";
        final double expectedResult = 2.4375;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing division of two decimals should work");
    }

    @Test
    void testTokenizeAddNegativeNumber() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "18.3+-23.4";
        final double expectedResult = -5.1;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing addition of a negative number onto a number should work");
    }

    @Test
    void testTokenizeSubtractNegativeNumber() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "7--5";
        final double expectedResult = 12;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing subtraction of a negative number from a number should work");
    }

    @Test
    void testTokenizeMultiplyNegativeNumber() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "1.3*-2";
        final double expectedResult = -2.6;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing multiplication with a negative number should work");
    }

    @Test
    void testTokenizeDotRuleAddThenMultiple() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "13+2*4";
        final double expectedResult = 21;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with first addition, then multiplication should first multiply, then add");
    }

    @Test
    void testTokenizeDotRuleMultipleThenAdd() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "8*3+14";
        final double expectedResult = 38;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with first multiplication, then addition should first multiply, then add");
    }

    @Test
    void testTokenizeDotRuleMinusThenDivide() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "3.7-12/24";
        final double expectedResult = 3.2;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with first subtraction, then division should first divide, then subtract");
    }

    @Test
    void testTokenizeDotRuleDivideThenMinus() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "14/4-0.7";
        final double expectedResult = 2.8;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with first division, then subtraction should first divide, then subtract");
    }

    @Test
    void testTokenizeDotRuleModulo() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "5+13%7-2";
        final double expectedResult = 9;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with addition, modulo and subtraction, then modulo should be done before adding and subtracting");
    }

    @Test
    void testTokenizePowerRuleExponentThenMultiply() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "3^4*2";
        final double expectedResult = 162;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with first the power operator, then multiplication the power should be calculated before doing the multiplication");
    }

    @Test
    void testTokenizePowerRuleMultiplyThenExponent() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "0.3*2^5";
        final double expectedResult = 9.6;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with first multiplication, then the power operator the power should be calculated before doing the multiplication");
    }

    @Test
    void testTokenizeRoundToInteger() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "2.2~0";
        final double expectedResult = 2;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "the round operator should round the first value to the given number of decimal digits when tokenized");
    }

    @Test
    void testTokenizeRoundEdgeCase() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "3.05~1";
        final double expectedResult = 3.1;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenized round operator should also properly round edge cases like 0.5");
    }

    @Test
    void testTokenizeRoundResult() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "10-3.03*4~1";
        final double expectedResult = -2.1;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "round operator should have lowest priority when tokenized");
    }

    @Test
    void testTokenizeRoundInParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "(4.0345~2)*4";
        final double expectedResult = 16.12;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "rounding inside parenthesis should work to when tokenized");
    }

    @Test
    void testTokenizeRoundParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "(4.0345*4)~2";
        final double expectedResult = 16.14;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "rounding the content of a parenthesis should also tokenize");
    }

    @Test
    void testTokenizeRoundPower() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "2.5^3~2";
        final double expectedResult = 15.63;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "round operator should have lower priority then power operator");
    }

    @Test
    void testTokenizeRoundNegativeDigits() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "1234~-2";
        final double expectedResult = 1200;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "round operator should work with negative number of decimal digits");
    }

    @Test
    void testTokenizeRoundMinus() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "13.243~7-5";
        final double expectedResult = 13.24;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "round operator should work if a mathematical expression is given as number of decimal digits");
    }

    @Test
    void testTokenizeNothingTimesSomething() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "*64";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string without value before the first operator should throw an exception");
        assertEquals("invalid calculation (operator missing first value)", exception.getMessage(), "exception should be about parenthesis / brackets mismatch");
    }

    @Test
    void testTokenizeSomethingTimesNothing() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "423*";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string without value after the last operator should throw an exception");
        assertEquals("invalid calculation (operator missing second value)", exception.getMessage(), "exception should be about parenthesis / brackets mismatch");
    }

    @Test
    void testTokenizeDoubleTimesOperator() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "423**64";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with two adjacent plus operators should throw an exception");
        assertEquals("invalid calculation (doubled operators)", exception.getMessage(), "exception should be about parenthesis / brackets mismatch");
    }

    @Test
    void testTokenizeSimpleParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "(4123)";
        final double expectedResult = 4123;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a number inside a parenthesis should work");
    }

    @Test
    void testTokenizeSimpleDoubleParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "((4123))";
        final double expectedResult = 4123;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a number inside multiple parenthesis should work");
    }

    @Test
    void testTokenizeCalculationInParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "(423+543)";
        final double expectedResult = 966;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a calculation inside a parenthesis should work");
    }

    @Test
    void testTokenizeMultipleOfSum() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "2.3*(32+12+65)";
        final double expectedResult = 250.7;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string multiplying the result of a calculation inside a parenthesis should work");
    }

    @Test
    void testTokenizeDeepParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "1-(45+((23-5)*4))/6";
        final double expectedResult = -18.5;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a calculation with multiple layers of calculations inside parenthesis should work");
    }

    @Test
    void testTokenizeChainedParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "(14+6)*(4-1)/(8*2)+(12/3)";
        final double expectedResult = 7.75;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a series of calculations in parenthesis should work");
    }

    @Test
    void testTokenizeDeepAndChainedParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "((1-1)*2)/1.5+(3/4)";
        final double expectedResult = 0.75;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a mix of parenthesis inside and sequential should work; issue #1421");
    }

    @Test
    void testTokenizeNegativeParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "-(15.4-32.9)";
        final double expectedResult = 17.5;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the negation of the result of a calculation inside parenthesis should work");
    }

    @Test
    void testTokenizeMissingOpeningParenthesis() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "865)";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with a missing opening parenthesis should throw an exception");
        assertEquals("invalid calculation (unbalanced parenthesis)", exception.getMessage(), "exception should be about parenthesis imbalance");
    }

    @Test
    void testTokenizeMissingClosingParenthesis() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "(846";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with a missing closing parenthesis should throw an exception");
        assertEquals("invalid calculation (unbalanced parenthesis)", exception.getMessage(), "exception should be about parenthesis imbalance");
    }

    @Test
    void testTokenizeEmptyParenthesis() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "()";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with an empty parenthesis should throw an exception");
        assertEquals("invalid calculation (empty parenthesis)", exception.getMessage(), "exception should be about empty parenthesis");
    }

    @Test
    void testTokenizeNegatedEmptyParenthesis() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "-()";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with a negated empty parenthesis should throw an exception");
        assertEquals("invalid calculation (empty parenthesis)", exception.getMessage(), "exception should be about empty parenthesis");
    }

    @Test
    void testTokenizeNestedEmptyParenthesis() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "(())";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with a nested empty parenthesis should throw an exception");
        assertEquals("invalid calculation (empty parenthesis)", exception.getMessage(), "exception should be about empty parenthesis");
    }

    @Test
    void testTokenizeNestedEmptyParenthesisWithCalculation() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "(()+5)";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with a nested empty parenthesis followed by a calculation should throw an exception");
        assertEquals("invalid calculation (empty parenthesis)", exception.getMessage(), "exception should be about empty parenthesis");
    }

    @Test
    void testTokenizeNumberThenParenthesisWithoutOperator() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "7(2+4)";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string that connects a number with a parenthesis without operator should throw an exception");
        assertEquals("invalid calculation (missing operator)", exception.getMessage(), "exception should be about missing operator");
    }

    @Test
    void testTokenizeParenthesisThenNumberWithoutOperator() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "(2+4)7";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string that connects a parenthesis with a number without operator should throw an exception");
        assertEquals("invalid calculation (missing operator)", exception.getMessage(), "exception should be about missing operator");
    }

    @Test
    void testTokenizeSimpleBrackets() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "[546]";
        final double expectedResult = 546;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a number inside a bracket should work");
    }

    @Test
    void testTokenizeSimpleDoubleBrackets() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "[[546]]";
        final double expectedResult = 546;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a number inside multiple brackets should work");
    }

    @Test
    void testTokenizeCalculationInBrackets() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "[329+603]";
        final double expectedResult = 932;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a calculation inside a bracket should work");
    }

    @Test
    void testTokenizeParenthesisInBrackets() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "[(8345)]";
        final double expectedResult = 8345;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a number inside a parenthesis and then a bracket should work");
    }

    @Test
    void testTokenizeBracketsInParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "([8345])";
        final double expectedResult = 8345;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a number inside a bracket and then a parenthesis should work");
    }

    @Test
    void testTokenizeInvalidParenthesisAndBracketsMix() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "[(653])";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with interleaved brackets and parenthesis should throw an exception");
        assertEquals("invalid calculation (parenthesis / brackets mismatch)", exception.getMessage(), "exception should be about parenthesis / brackets mismatch");
    }

    @Test
    void testTokenizeAbsoluteOfPositive() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "|5|";
        final double expectedResult = 5;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the calculation of the absolute of a positive number should work");
    }

    @Test
    void testTokenizeAbsoluteOfNegative() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "|-7|";
        final double expectedResult = 7;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the calculation of the absolute of a negative number should work");
    }

    @Test
    void testTokenizeAbsoluteOfNegativeDecimal() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "|-32.65|";
        final double expectedResult = 32.65;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the calculation of the absolute of a negative decimal should work");
    }

    @Test
    void testTokenizeNegativeAbsolute() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "-|-23.5|";
        final double expectedResult = -23.5;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the negation of the result of the absolute should work");
    }

    @Test
    void testTokenizeDifferenceOfAbsolutes() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "|-3|-|-7|";
        final double expectedResult = -4;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the difference of two absolutes should work");
    }

    @Test
    void testTokenizeAbsoluteOfAbsoluteInParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "|1-(|3|)|";
        final double expectedResult = 2;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the absolute another absolute wrapped in parenthesis should work");
    }

    @Test
    void testTokenizeEmptyAbsolute() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "||";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing an empty absolute value should throw an exception");
        assertEquals("invalid calculation (empty absolute value)", exception.getMessage(), "exception should be about empty absolute value");
    }

    @Test
    void testTokenizeNegatedEmptyAbsolute() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "-||";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a negated empty absolute value should throw an exception");
        assertEquals("invalid calculation (empty absolute value)", exception.getMessage(), "exception should be about empty absolute value");
    }

    @Test
    void testTokenizeNumberThenAbsoluteWithoutOperator() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "7|2+4|";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string that connects a number with an absolute without operator should throw an exception");
        assertEquals("invalid calculation (missing operator)", exception.getMessage(), "exception should be about missing operator");
    }

    @Test
    void testTokenizeAbsoluteThenNumberWithoutOperator() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "|2+4|7";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string that connects an absolute with a number without operator should throw an exception");
        assertEquals("invalid calculation (missing operator)", exception.getMessage(), "exception should be about missing operator");
    }

    @Test
    void testTokenizeInvalidAbsoluteAndParenthesisMix() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "|(653|)";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with interleaved absolutes and parenthesis should throw an exception");
        assertEquals("invalid calculation (unbalanced absolute value)", exception.getMessage(), "exception should be about parenthesis / absolute mismatch");
    }

    @Test
    void testTokenizeInvalidParenthesisAndAbsoluteMix() {
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);
        final String calculation = "(|653)|";

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string with interleaved absolutes and parenthesis should throw an exception");
        assertEquals("invalid calculation (unbalanced absolute value)", exception.getMessage(), "exception should be about parenthesis / absolute mismatch");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeVariable() throws Throwable {
        final String variable = "var";
        final double value = 43;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeVariableWithNumber() throws Throwable {
        final String variable = "var2";
        final double value = 44;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing a number should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeVariableWithUnderscore() throws Throwable {
        final String variable = "var_able";
        final double value = 45;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing an underscore should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeVariableWithBang() throws Throwable {
        final String variable = "var!bang";
        final double value = 47;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing an exclamation mark should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeVariableWithEquals() throws Throwable {
        final String variable = "var=qu";
        final double value = 48;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing an equals sign should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeVariableWithNumberSign() throws Throwable {
        final String variable = "var#rav";
        final double value = 49;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing a number sign should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeVariableWithAmpersand() throws Throwable {
        final String variable = "var&more";
        final double value = 50;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing an ampersand should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeVariableWithApostrophe() throws Throwable {
        final String variable = "variable's";
        final double value = 51;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing an apostrophe should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeVariableWithQuestionMark() throws Throwable {
        final String variable = "var?not";
        final double value = 52;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing a question mark should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeVariableWithDot() throws Throwable {
        final String variable = "var.net";
        final double value = 53;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing a dot should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeVariableWithColon() throws Throwable {
        final String variable = "var:res";
        final double value = 54;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing a colon should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeVariableEndingWithNumber() throws Throwable {
        final String variable = "var123";
        final double value = 53;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(variable);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable ending with a digit should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeNegateVariable() throws Throwable {
        final String variable = "var";
        final String calculation = '-' + variable;
        final double value = 23;

        withVariables(new ProtoVariable(variable, String.valueOf(-value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the negation of a variable should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeNumberPlusVariable() throws Throwable {
        final String variable = "var";
        final String calculation = "5+" + variable;
        final double value = 7;
        final double expectedResult = 12;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the addition of a number to a variable should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeVariablePlusNumber() throws Throwable {
        final String variable = "var";
        final String calculation = variable + "+7";
        final double value = 5;
        final double expectedResult = 12;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the addition of a variable to a number should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeAdditionOfVariables() throws Throwable {
        final String firstVariable = "var1";
        final String secondVariable = "var2";
        final String calculation = firstVariable + '+' + secondVariable;
        final double firstValue = 64;
        final double secondValue = 32;
        final double expectedResult = 96;

        withVariables(new ProtoVariable(firstVariable, String.valueOf(firstValue)), new ProtoVariable(secondVariable, String.valueOf(secondValue)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the addition of two variables should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeVariableInParenthesis() throws Throwable {
        final String variable = "var";
        final String calculation = "(" + variable + ")";
        final double value = 6354.6;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the a variable inside parenthesis should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeNumberThenVariableWithoutOperator() throws Throwable {
        final String variable = "var";
        final String calculation = "756.39" + variable;
        final double value = 57.1;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string that connects a number with a variable without operator should throw an exception");
        assertEquals("invalid calculation (missing operator)", exception.getMessage(), "exception should be about missing operator");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeVariableThenParenthesisWithoutOperator() throws Throwable {
        final String variable = "var";
        final String calculation = variable + "(1+3)";
        final double value = 5;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string that connects a variable with a parenthesis without operator should throw an exception");
        assertEquals("invalid calculation (missing operator)", exception.getMessage(), "exception should be about missing operator");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeParenthesisThenVariableWithoutOperator() throws Throwable {
        final String variable = "var";
        final String calculation = "(1+3)" + variable;
        final double value = 8;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string that connects a parenthesis with a variable without operator should throw an exception");
        assertEquals("invalid calculation (missing operator)", exception.getMessage(), "exception should be about missing operator");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeCurlyBracesVariable() throws Throwable {
        final String variable = "var";
        final String calculation = "{" + variable + "}";
        final double value = 65;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the a curly braces variable should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeNegatedCurlyBracesVariable() throws Throwable {
        final String variable = "var";
        final String calculation = "-{" + variable + "}";
        final double value = 66;
        final double expected = -value;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expected, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the a curly braces variable should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeCurlyBracesVariableWithEscapedBackslash() throws Throwable {
        final String variable = "back\\slash";
        final String calculation = "{back\\\\slash}";
        final double value = 17;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the a curly braces variable with escaped backslash should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeCurlyBracesVariableWithEscapedClosingBrace() throws Throwable {
        final String variable = "closing{curly}brace";
        final String calculation = "{closing{curly\\}brace}";
        final double value = 27;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the a curly braces variable with escaped closing curly brace should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeCurlyBracesVariableContainingMathExpression() throws Throwable {
        final String variable = "math:5+(7-9*var+|[|-3|+(8)]|)";
        final String calculation = "{" + variable + "}";
        final double value = 32;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the a curly braces variable with escaped closing curly brace should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeCurlyBracesVariableContainingDoubleEscapedBackslash() throws Throwable {
        final String variable = "\\\\";
        final String calculation = "{\\\\\\\\}";
        final double value = 87;

        withVariables(new ProtoVariable(variable, String.valueOf(value)));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(value, result.resolve(TEST_PLAYER_PROFILE), REQUIRED_DOUBLE_PRECISION, "tokenizing the a curly braces variable with two backslashes in a row should work");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeMissingOpeningCurlyBrace() throws Throwable {
        final String variable = "invalid";
        final String calculation = variable + "}";

        withVariables(new ProtoVariable(variable, "83"));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string that connects a parenthesis with a variable without operator should throw an exception");
        assertEquals("invalid calculation (unbalanced curly brace)", exception.getMessage(), "exception should be about missing operator");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testTokenizeMissingClosingCurlyBrace() throws Throwable {
        final String variable = "invalid";
        final String calculation = "{" + variable;

        withVariables(new ProtoVariable(variable, "83"));
        final Tokenizer tokenizer = new Tokenizer(variableProcessor, TEST_PACK);

        final InstructionParseException exception = assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(calculation), "tokenizing a string that connects a parenthesis with a variable without operator should throw an exception");
        assertEquals("invalid calculation (unbalanced curly brace)", exception.getMessage(), "exception should be about missing operator");
    }

    private void withVariables(final ProtoVariable... variables) throws Throwable {
        for (final ProtoVariable variableTemplate : variables) {
            final Variable var = mock(Variable.class);
            when(var.getValue(TEST_PLAYER_PROFILE)).thenReturn(variableTemplate.value());
            when(variableProcessor.create(TEST_PACK, "%" + variableTemplate.key() + "%")).thenReturn(var);
        }
    }

    /**
     * Array-safe container for variable-name to variable-value pairs.
     *
     * @param key   The variable key.
     * @param value The variable value.
     */
    private record ProtoVariable(String key, String value) {

        /**
         * Create a variable prototype used by {@link #withVariables(ProtoVariable...)}
         *
         * @param key   variable name
         * @param value variable content
         */
        private ProtoVariable {
        }
    }
}
