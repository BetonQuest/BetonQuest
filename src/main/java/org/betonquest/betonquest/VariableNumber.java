package org.betonquest.betonquest;

import lombok.CustomLog;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Represents a number which might also be a variable.
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class VariableNumber {

    private double number;
    private Variable variable;

    /**
     * Parses the string as a number or saves it as a variable if it's not a
     * number.
     *
     * @param packName the package in which the variable is defined
     * @param variable the string to parse
     * @throws InstructionParseException If the variable could not be created.
     */
    public VariableNumber(final String packName, final String variable) throws InstructionParseException {
        if (variable.length() > 2 && variable.charAt(0) == '%' && variable.endsWith("%")) {
            try {
                this.variable = BetonQuest.createVariable(Config.getPackages().get(packName), variable);
            } catch (final InstructionParseException e) {
                throw new InstructionParseException("Could not create variable: " + e.getMessage(), e);
            }
            if (this.variable == null) {
                throw new InstructionParseException("Could not create variable");
            }
        } else {
            number = Double.parseDouble(variable);
        }
    }

    /**
     * Creates the VariableNumber using specified number.
     *
     * @param number the number to use
     */
    public VariableNumber(final int number) {
        this.number = number;
    }

    /**
     * Creates the VariableNumber using specified number.
     *
     * @param number the number to use
     */
    public VariableNumber(final double number) {
        this.number = number;
    }

    /**
     * Returns an integer represented by this variable. If it's a double, this
     * method will return the floor of it.
     *
     * @param playerID ID of the player for whom the variable should be resolved
     * @return the integer represented by this variable number
     */
    public int getInt(final String playerID) {
        return (int) Math.floor(resolveVariable(playerID));
    }

    /**
     * Returns a double represented by this variable.
     *
     * @param playerID ID of the player for whom the variable should be resolved
     * @return the double represented by this variable number
     * @throws QuestRuntimeException when the variable does not resolve to a number
     */
    public double getDouble(final String playerID) throws QuestRuntimeException {
        return resolveVariable(playerID);
    }

    private double resolveVariable(final String playerID) {
        if (variable == null) {
            return number;
        } else if (playerID == null) {
            return 0;
        } else {
            final String resolved = variable.getValue(playerID);
            double parsed = 0;
            try {
                parsed = Double.parseDouble(resolved);
            } catch (final NumberFormatException e) {
                LOG.debug("Could not parse the variable as a number, it's value is: '" + resolved + "'; returning 0.", e);
            }
            return parsed;
        }
    }

    @Override
    public String toString() {
        return variable == null ? String.valueOf(number) : variable.toString();
    }
}
