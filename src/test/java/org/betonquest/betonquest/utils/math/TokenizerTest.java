package org.betonquest.betonquest.utils.math;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.MockedStatic;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.math.tokens.Token;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test the {@link Tokenizer}.
 */
@SuppressWarnings({"deprecation", "PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
public class TokenizerTest {

    /**
     * Precision up to which to check equality of floating point numbers
     */
    public static final double REQUIRED_DOUBLE_PRECISION = 1E-7;

    /**
     * The package name of the package we assume to be inside for variable resolution.
     */
    public static final String TEST_PACKAGE = "package";

    /**
     * The player name to use for variable resolution.
     */
    public static final String TEST_PLAYER_ID = "player";

    private static Tokenizer createTokenizer() {
        return new Tokenizer(TEST_PACKAGE, '.');
    }

    private static void withVariables(final Executable executable, final ProtoVariable... variables) throws Throwable {
        try (MockedStatic<Config> config = mockStatic(Config.class);
             MockedStatic<BetonQuest> betonQuest = mockStatic(BetonQuest.class)) {
            final ConfigPackage configPackage = mock(ConfigPackage.class);
            final Map<String, ConfigPackage> packageMap = Collections.singletonMap(TEST_PACKAGE, configPackage);
            //noinspection ResultOfMethodCallIgnored
            config.when(Config::getPackages).thenReturn(packageMap);

            for (final ProtoVariable variableTemplate : variables) {
                final Variable var = mock(Variable.class);
                when(var.getValue(TEST_PLAYER_ID)).thenReturn(variableTemplate.getValue());
                betonQuest.when(() -> BetonQuest.createVariable(configPackage, "%" + variableTemplate.getKey() + "%")).thenReturn(var);
            }

            executable.execute();
        }
    }

    /**
     * Create the TokenizerTest.
     */
    public TokenizerTest() {
    }

    @Test
    /* default */ void testTokenizeNull() {
        final Tokenizer tokenizer = createTokenizer();
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> tokenizer.tokenize(null), "tokenizing null should fail (NPE)");
    }

    @Test
    /* default */ void testTokenizeEmpty() {
        final Tokenizer tokenizer = createTokenizer();
        assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(""), "an empty string should not be parsable");
    }

    @Test
    /* default */ void testTokenizeDigit() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();

        final Token result = tokenizer.tokenize("1");
        assertEquals(1, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a single letter integer should work");
    }

    @Test
    /* default */ void testTokenizeInteger() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final double number = 2_134_671;

        final Token result = tokenizer.tokenize(String.valueOf(number));
        assertEquals(number, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a multi letter integer should work");
    }

    @Test
    /* default */ void testTokenizeNegativeInteger() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final double number = -56_234;

        final Token result = tokenizer.tokenize(String.valueOf(number));
        assertEquals(number, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a negative integer should work");
    }

    @Test
    /* default */ void testTokenizeDecimal() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final double number = 45_213.234_5;

        final Token result = tokenizer.tokenize(String.valueOf(number));
        assertEquals(number, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a decimal should work");
    }

    @Test
    /* default */ void testTokenizeNegativeDecimal() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final double number = -7_342.634;

        final Token result = tokenizer.tokenize(String.valueOf(number));
        assertEquals(number, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a negative decimal should work");
    }

    @Test
    /* default */ void testTokenizeIntegerAddition() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "1+2";
        final double expectedResult = 3;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing addition of two integers should work");
    }

    @Test
    /* default */ void testTokenizeIntegerSubtraction() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "1-2";
        final double expectedResult = -1;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing subtraction of two integers should work");
    }

    @Test
    /* default */ void testTokenizeIntegerMultiplication() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "2*3";
        final double expectedResult = 6;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing multiplication of two integers should work");
    }

    @Test
    /* default */ void testTokenizeIntegerDivision() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "6/3";
        final double expectedResult = 2;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing division of two integers should work");
    }

    @Test
    /* default */ void testTokenizeDecimalAddition() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "1.7+3.5";
        final double expectedResult = 5.2;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing addition of two decimals should work");
    }

    @Test
    /* default */ void testTokenizeDecimalSubtraction() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "4.6-5.3";
        final double expectedResult = -0.7;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing subtraction of two decimals should work");
    }

    @Test
    /* default */ void testTokenizeDecimalMultiplication() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "2.4*5.3";
        final double expectedResult = 12.72;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing multiplication of two decimals should work");
    }

    @Test
    /* default */ void testTokenizeDecimalDivision() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "7.8/3.2";
        final double expectedResult = 2.4375;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing division of two decimals should work");
    }

    @Test
    /* default */ void testTokenizeAddNegativeNumber() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "18.3+-23.4";
        final double expectedResult = -5.1;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing addition of a negative number onto a number should work");
    }

    @Test
    /* default */ void testTokenizeSubtractNegativeNumber() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "7--5";
        final double expectedResult = 12;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing subtraction of a negative number from a number should work");
    }

    @Test
    /* default */ void testTokenizeMultiplyNegativeNumber() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "1.3*-2";
        final double expectedResult = -2.6;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing multiplication with a negative number should work");
    }

    @Test
    /* default */ void testTokenizeDotRuleAddThenMultiple() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "13+2*4";
        final double expectedResult = 21;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with first addition, then multiplication should first multiply, then add");
    }

    @Test
    /* default */ void testTokenizeDotRuleMultipleThenAdd() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "8*3+14";
        final double expectedResult = 38;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with first multiplication, then addition should first multiply, then add");
    }

    @Test
    /* default */ void testTokenizeDotRuleMinusThenDivide() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "3.7-12/24";
        final double expectedResult = 3.2;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with first subtraction, then division should first divide, then subtract");
    }

    @Test
    /* default */ void testTokenizeDotRuleDivideThenMinus() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "14/4-0.7";
        final double expectedResult = 2.8;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with first division, then subtraction should first divide, then subtract");
    }

    @Test
    /* default */ void testTokenizeDotRuleModulo() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "5+13%7-2";
        final double expectedResult = 9;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with addition, modulo and subtraction, then modulo should be done before adding and subtracting");
    }

    @Test
    /* default */ void testTokenizePowerRuleExponentThenMultiply() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "3^4*2";
        final double expectedResult = 162;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with first the power operator, then multiplication the power should be calculated before doing the multiplication");
    }

    @Test
    /* default */ void testTokenizePowerRuleMultiplyThenExponent() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "0.3*2^5";
        final double expectedResult = 9.6;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with first multiplication, then the power operator the power should be calculated before doing the multiplication");
    }

    @Test
    /* default */ void testTokenizeSimpleParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "(4123)";
        final double expectedResult = 4123;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a number inside a parenthesis should work");
    }

    @Test
    /* default */ void testTokenizeSimpleDoubleParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "((4123))";
        final double expectedResult = 4123;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a number inside multiple parenthesis should work");
    }

    @Test
    /* default */ void testTokenizeCalculationInParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "(423+543)";
        final double expectedResult = 966;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a string with a calculation inside a parenthesis should work");
    }

    @Test
    /* default */ void testTokenizeMultipleOfSum() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "2.3*(32+12+65)";
        final double expectedResult = 250.7;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a string multiplying the result of a calculation inside a parenthesis should work");
    }

    @Test
    /* default */ void testTokenizeDeepParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "1-(45+((23-5)*4))/6";
        final double expectedResult = -18.5;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a calculation with multiple layers of calculations inside parenthesis should work");
    }

    @Test
    /* default */ void testTokenizeChainedParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "(14+6)*(4-1)/(8*2)+(12/3)";
        final double expectedResult = 7.75;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a series of calculations in parenthesis should work");
    }

    @Test
    /* default */ void testTokenizeDeepAndChainedParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "((1-1)*2)/1.5+(3/4)";
        final double expectedResult = 0.75;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a mix of parenthesis inside and sequential should work; issue #1421");
    }

    @Disabled("negating parenthesis is not a supported feature")
    @Test
    /* default */ void testTokenizeNegativeParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "-(15.4-32.9)";
        final double expectedResult = 17.5;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing the negation of the result of a calculation inside parenthesis should work");
    }

    @Test
    /* default */ void testTokenizeAbsoluteOfPositive() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "|5|";
        final double expectedResult = 5;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing the calculation of the absolute of a positive number should work");
    }

    @Test
    /* default */ void testTokenizeAbsoluteOfNegative() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "|-7|";
        final double expectedResult = 7;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing the calculation of the absolute of a negative number should work");
    }

    @Test
    /* default */ void testTokenizeAbsoluteOfNegativeDecimal() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "|-32.65|";
        final double expectedResult = 32.65;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing the calculation of the absolute of a negative decimal should work");
    }

    @Disabled("negating absolute is not a supported feature")
    @Test
    /* default */ void testTokenizeNegativeAbsolute() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "-|-23.5|";
        final double expectedResult = -23.5;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing the negation of the result of the absolute should work");
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    /* default */ void testTokenizeVariable() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var";
        final double value = 43;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable should work");
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    /* default */ void testTokenizeVariableWithNumber() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var2";
        final double value = 44;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing a number should work");
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    /* default */ void testTokenizeVariableWithUnderscore() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var_able";
        final double value = 45;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing an underscore should work");
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    /* default */ void testTokenizeVariableWithTilde() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var~de";
        final double value = 46;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing a tilde should work");
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    /* default */ void testTokenizeVariableWithBang() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var!bang";
        final double value = 47;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing an exclamation mark should work");
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    /* default */ void testTokenizeVariableWithEquals() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var=qu";
        final double value = 48;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing an equals sign should work");
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    /* default */ void testTokenizeVariableWithNumberSign() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var#rav";
        final double value = 49;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing a number sign should work");
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    /* default */ void testTokenizeVariableWithAmpersand() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var&more";
        final double value = 50;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing an ampersand should work");
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    /* default */ void testTokenizeVariableWithApostrophe() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "variable's";
        final double value = 51;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing an apostrophe should work");
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    /* default */ void testTokenizeVariableWithQuestionMark() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var?not";
        final double value = 52;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing a question mark should work");
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    /* default */ void testTokenizeVariableWithDot() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var.net";
        final double value = 53;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing a dot should work");
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    /* default */ void testTokenizeVariableWithColon() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var:res";
        final double value = 54;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable containing a colon should work");
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    /* default */ void testTokenizeVariableEndingWithNumber() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var123";
        final double value = 53;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION, "tokenizing a variable ending with a digit should work");
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    /**
     * Array-safe container for variable-name to variable-value pairs.
     */
    private static class ProtoVariable {

        /**
         * The variable key.
         */
        private final String key;

        /**
         * The variable value.
         */
        private final String value;

        /**
         * Create a variable prototype used by {@link #withVariables(Executable, ProtoVariable...)}
         *
         * @param key variable name
         * @param value variable content
         */
        public ProtoVariable(final String key, final String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }
}
