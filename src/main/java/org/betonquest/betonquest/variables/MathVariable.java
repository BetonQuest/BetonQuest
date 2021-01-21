package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.LogUtils;

import java.util.Locale;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This variable evaluates the given calculation and returns the result.
 */
@SuppressWarnings("PMD.CommentRequired")
public class MathVariable extends Variable {

    private final Calculable calculation;

    public MathVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        final String instructionString = instruction.getInstruction();
        if (!instructionString.matches("math\\.calc:.+")) {
            throw new InstructionParseException("invalid format");
        }
        this.calculation = this.parse(instructionString.substring("math.calc:".length()));
    }

    @Override
    public String getValue(final String playerID) {
        try {
            final double value = this.calculation.calculate(playerID);
            if (value % 1 == 0) {
                return String.format(Locale.US, "%.0f", value);
            }
            return String.valueOf(value);
        } catch (QuestRuntimeException e) {
            LogUtils.getLogger().log(Level.WARNING, "Could not calculate '" + calculation.toString() + "' (" + e.getMessage() + "). Returning 0 instead.");
            LogUtils.logThrowable(e);
            return "0";
        }
    }

    /**
     * Recursively parse a calculable object from the given calculation string which can be calculated in later process
     *
     * @param string the string which should be evaluated
     * @return a calculable object which contains the whole calculation
     * @throws InstructionParseException if the instruction isn't valid
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    private Calculable parse(final String string) throws InstructionParseException {
        //clarify error messages for invalid calculations
        if (string.matches(".*[+\\-*/^]{2}.*")) {
            throw new InstructionParseException("invalid calculation (operations doubled)");
        }
        if (string.matches(".*(\\([^)]*|\\[[^]]*)")
                || string.matches("([^(]*\\)|[^\\[]*]).*")) {
            throw new InstructionParseException("invalid calculation (uneven braces)");
        }
        //calculate braces
        if (string.matches("(\\(.+\\)|\\[.+])")) {
            return this.parse(string.substring(1, string.length() - 1));
        }
        //calculate the absolute value
        if (string.matches("\\|.+\\|")) {
            return new AbsoluteValue(this.parse(string.substring(1, string.length() - 1)));
        }
        String tempCopy = string;
        final Matcher matcher = Pattern.compile("(\\(.+\\)|\\[.+]|\\|.+\\|)").matcher(tempCopy);
        //ignore content of braces for all next operations
        while (matcher.find()) {
            final int start = matcher.start();
            final int end = matcher.end();
            final int length = end - start;
            final StringBuilder substring = new StringBuilder(tempCopy.substring(0, start + 1));
            for (int i = 0; i < length - 2; i++) {
                substring.append(" ");
            }
            substring.append(tempCopy.substring(end - 1));
            tempCopy = substring.toString();
        }
        // ADDITION and SUBTRACTION
        int indexPlus = tempCopy.lastIndexOf('+');
        int indexMinus = tempCopy.lastIndexOf('-');
        if (indexPlus > indexMinus) {
            if (indexPlus == 0) {
                return new Calculation(new ClaculableVariable(0), this.parse(string.substring(1)), Operation.ADD);
            }
            //'+' comes after '-'
            return new Calculation(this.parse(string.substring(0, indexPlus)),
                    this.parse(string.substring(indexPlus + 1)),
                    Operation.ADD);
        } else if (indexMinus > indexPlus) {
            if (indexMinus == 0) {
                return new Calculation(new ClaculableVariable(0), this.parse(string.substring(1)), Operation.SUBTRACT);
            }
            //'-' comes after '+'
            return new Calculation(this.parse(string.substring(0, indexMinus)),
                    this.parse(string.substring(indexMinus + 1)),
                    Operation.SUBTRACT);
        }
        //MULTIPLY and DIVIDE
        indexPlus = tempCopy.lastIndexOf('*');
        indexMinus = tempCopy.lastIndexOf('/');
        if (indexPlus > indexMinus) {
            //'*' comes after '/'
            return new Calculation(this.parse(string.substring(0, indexPlus)),
                    this.parse(string.substring(indexPlus + 1)),
                    Operation.MULTIPLY);
        } else if (indexMinus > indexPlus) {
            //'/' comes after '*'
            return new Calculation(this.parse(string.substring(0, indexMinus)),
                    this.parse(string.substring(indexMinus + 1)),
                    Operation.DIVIDE);
        }
        //POW
        indexPlus = tempCopy.lastIndexOf('^');
        if (indexPlus != -1) {
            return new Calculation(this.parse(string.substring(0, indexPlus)),
                    this.parse(string.substring(indexPlus + 1)),
                    Operation.POW);
        }
        //if string matches a number
        if (string.matches("\\d+(\\.\\d+)?")) {
            return new ClaculableVariable(Double.parseDouble(string));
        }
        //if a variable is specified
        try {
            return new ClaculableVariable(super.instruction.getPackage(), "%" + string + "%");
        } catch (InstructionParseException e) {
            throw new InstructionParseException(e.getMessage(), e);
        }
    }

    private enum Operation {
        ADD('+'),
        SUBTRACT('-'),
        MULTIPLY('*'),
        DIVIDE('/'),
        POW('^');

        private final char operator;

        Operation(final char operator) {
            this.operator = operator;
        }
    }

    private interface Calculable {

        double calculate(String playerId) throws QuestRuntimeException;
    }

    private static class ClaculableVariable implements Calculable {

        private final VariableNumber variable;

        public ClaculableVariable(final VariableNumber variable) {
            this.variable = variable;
        }

        public ClaculableVariable(final double number) {
            this(new VariableNumber(number));
        }

        public ClaculableVariable(final ConfigPackage pack, final String variable) throws InstructionParseException {
            this(new VariableNumber(pack.getName(), variable));
        }

        @Override
        public double calculate(final String playerId) throws QuestRuntimeException {
            return variable.getDouble(playerId);
        }

        @Override
        public String toString() {
            return variable.toString().replace("%", "");
        }
    }

    private static class AbsoluteValue implements Calculable {

        private final Calculable number;

        public AbsoluteValue(final Calculable number) {
            this.number = number;
        }

        @Override
        public String toString() {
            return "|" + number + "|";
        }

        @Override
        public double calculate(final String playerId) throws QuestRuntimeException {
            return Math.abs(number.calculate(playerId));
        }
    }

    private static class Calculation implements Calculable {

        private final Calculable numberA;
        private final Calculable numberB;
        private final Operation operation;

        public Calculation(final Calculable numberA, final Calculable numberB, final Operation operation) {
            this.numberA = numberA;
            this.numberB = numberB;
            this.operation = operation;
        }

        @Override
        public double calculate(final String playerId) throws QuestRuntimeException {
            try {
                switch (operation) {
                    case ADD:
                        return numberA.calculate(playerId) + numberB.calculate(playerId);
                    case SUBTRACT:
                        return numberA.calculate(playerId) - numberB.calculate(playerId);
                    case MULTIPLY:
                        return numberA.calculate(playerId) * numberB.calculate(playerId);
                    case DIVIDE:
                        return numberA.calculate(playerId) / numberB.calculate(playerId);
                    case POW:
                        return Math.pow(numberA.calculate(playerId), numberB.calculate(playerId));
                    default:
                        throw new QuestRuntimeException("unsupported operation: " + operation);
                }
            } catch (ArithmeticException e) {
                throw new QuestRuntimeException(e.getMessage(), e);
            }
        }

        @Override
        @SuppressWarnings("PMD.SimplifyStartsWith")
        public String toString() {
            final String numberA = this.numberA instanceof Calculation || this.numberA.toString().startsWith("-")
                    ? "(" + this.numberA.toString() + ")" : this.numberA.toString();
            final String numberB = this.numberB instanceof Calculation || this.numberB.toString().startsWith("-")
                    ? "(" + this.numberB.toString() + ")" : this.numberB.toString();
            return numberA + operation.operator + numberB;
        }
    }
}
