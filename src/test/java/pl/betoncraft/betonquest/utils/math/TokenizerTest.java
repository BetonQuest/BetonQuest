package pl.betoncraft.betonquest.utils.math;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.MockedStatic;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.math.tokens.Token;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("deprecation")
public class TokenizerTest {

    public static final double REQUIRED_DOUBLE_PRECISION = 1E-7;
    public static final String TEST_PACKAGE = "package";
    public static final String TEST_PLAYER_ID = "player";

    private static Tokenizer createTokenizer() {
        return new Tokenizer(TEST_PACKAGE, '.');
    }

    private static void withVariables(final Executable executable, final ProtoVariable... variables) throws Throwable {
        try (final MockedStatic<Config> config = mockStatic(Config.class);
             final MockedStatic<BetonQuest> betonQuest = mockStatic(BetonQuest.class)) {
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

    public TokenizerTest() {
    }

    @Test
    public void testTokenizeNull() {
        final Tokenizer tokenizer = createTokenizer();
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> tokenizer.tokenize(null));
    }

    @Test
    public void testTokenizeEmpty() {
        final Tokenizer tokenizer = createTokenizer();
        assertThrows(InstructionParseException.class, () -> tokenizer.tokenize(""));
    }

    @Test
    public void testTokenizeDigit() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();

        final Token result = tokenizer.tokenize("1");
        assertEquals(1, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeInteger() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final double number = 2134671;

        final Token result = tokenizer.tokenize(String.valueOf(number));
        assertEquals(number, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeNegativeInteger() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final double number = -56234;

        final Token result = tokenizer.tokenize(String.valueOf(number));
        assertEquals(number, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeDecimal() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final double number = 45213.2345;

        final Token result = tokenizer.tokenize(String.valueOf(number));
        assertEquals(number, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeNegativeDecimal() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final double number = -7342.634;

        final Token result = tokenizer.tokenize(String.valueOf(number));
        assertEquals(number, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeIntegerAddition() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "1+2";
        final double expectedResult = 3;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeIntegerSubtraction() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "1-2";
        final double expectedResult = -1;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeIntegerMultiplication() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "2*3";
        final double expectedResult = 6;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeIntegerDivision() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "6/3";
        final double expectedResult = 2;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeDecimalAddition() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "1.7+3.5";
        final double expectedResult = 5.2;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeDecimalSubtraction() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "4.6-5.3";
        final double expectedResult = -0.7;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeDecimalMultiplication() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "2.4*5.3";
        final double expectedResult = 12.72;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeDecimalDivision() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "7.8/3.2";
        final double expectedResult = 2.4375;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeAddNegativeNumber() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "18.3+-23.4";
        final double expectedResult = -5.1;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeSubtractNegativeNumber() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "7--5";
        final double expectedResult = 12;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeMultiplyNegativeNumber() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "1.3*-2";
        final double expectedResult = -2.6;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeDotRuleAddThenMultiple() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "13+2*4";
        final double expectedResult = 21;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeDotRuleMultipleThenAdd() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "8*3+14";
        final double expectedResult = 38;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeDotRuleMinusThenDivide() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "3.7-12/24";
        final double expectedResult = 3.2;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeDotRuleDivideThenMinus() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "14/4-0.7";
        final double expectedResult = 2.8;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeDotRuleModulo() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "5+13%7-2";
        final double expectedResult = 9;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizePowerRuleExponentThenMultiply() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "3^4*2";
        final double expectedResult = 162;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizePowerRuleMultiplyThenExponent() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "0.3*2^5";
        final double expectedResult = 9.6;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeSimpleParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "(4123)";
        final double expectedResult = 4123;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeSimpleDoubleParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "((4123))";
        final double expectedResult = 4123;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeCalculationInParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "(423+543)";
        final double expectedResult = 966;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeMultipleOfSum() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "2.3*(32+12+65)";
        final double expectedResult = 250.7;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeDeepParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "1-(45+((23-5)*4))/6";
        final double expectedResult = -18.5;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeChainedParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "(14+6)*(4-1)/(8*2)+(12/3)";
        final double expectedResult = 7.75;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeDeepAndChainedParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "((1-1)*2)/1.5+(3/4)";
        final double expectedResult = 0.75;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Disabled("negating parenthesis is not a supported feature")
    @Test
    public void testTokenizeNegativeParenthesis() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "-(15.4-32.9)";
        final double expectedResult = 17.5;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeAbsoluteOfPositive() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "|5|";
        final double expectedResult = 5;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeAbsoluteOfNegative() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "|-7|";
        final double expectedResult = 7;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeAbsoluteOfNegativeDecimal() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "|-32.65|";
        final double expectedResult = 32.65;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Disabled("negating absolute is not a supported feature")
    @Test
    public void testTokenizeNegativeAbsolute() throws InstructionParseException, QuestRuntimeException {
        final Tokenizer tokenizer = createTokenizer();
        final String calculation = "-|-23.5|";
        final double expectedResult = -23.5;

        final Token result = tokenizer.tokenize(calculation);
        assertEquals(expectedResult, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
    }

    @Test
    public void testTokenizeVariable() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var";
        final double value = 43;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    public void testTokenizeVariableWithNumber() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var2";
        final double value = 44;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    public void testTokenizeVariableWithUnderscore() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var_able";
        final double value = 45;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    public void testTokenizeVariableWithTilde() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var~de";
        final double value = 46;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    public void testTokenizeVariableWithBang() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var!bang";
        final double value = 47;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    public void testTokenizeVariableWithEquals() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var=qu";
        final double value = 48;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    public void testTokenizeVariableWithNumberSign() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var#rav";
        final double value = 49;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    public void testTokenizeVariableWithAmpersand() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var&more";
        final double value = 50;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    public void testTokenizeVariableWithApostrophe() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "variable's";
        final double value = 51;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    public void testTokenizeVariableWithQuestionMark() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var?not";
        final double value = 52;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    public void testTokenizeVariableWithDot() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var.net";
        final double value = 53;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    public void testTokenizeVariableWithColon() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var:res";
        final double value = 54;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    @Test
    public void testTokenizeVariableEndingWithNumber() throws Throwable {
        final Tokenizer tokenizer = createTokenizer();
        final String variable = "var123";
        final double value = 53;

        withVariables(() -> {
            final Token result = tokenizer.tokenize(variable);
            assertEquals(value, result.resolve(TEST_PLAYER_ID), REQUIRED_DOUBLE_PRECISION);
        }, new ProtoVariable(variable, String.valueOf(value)));
    }

    private static class ProtoVariable {
        private final String key;
        private final String value;

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
