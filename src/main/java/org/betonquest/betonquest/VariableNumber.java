package org.betonquest.betonquest;

import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Represents a number which might also be a variable.
 */
public class VariableNumber {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create();

    /**
     * The constant value of this variable number if no variable was set.
     */
    private final double number;

    /**
     * The variable to parse to get the value of the variable number.
     * If {@code null} then {@link #number} will be used.
     */
    private final Variable variable;

    /**
     * Parses the string as a variable or as a number if it's not a variable.
     *
     * @param pack the package in which the variable is defined
     * @param tmp  the string to parse
     * @throws InstructionParseException If the variable could not be created.
     */
    public VariableNumber(final QuestPackage pack, final String tmp) throws InstructionParseException {
        if (tmp.length() > 2 && tmp.charAt(0) == '%' && tmp.endsWith("%")) {
            this.variable = parseAsVariable(pack, tmp);
            this.number = 0.0;
        } else {
            this.variable = null;
            this.number = parseAsNumber(tmp);
        }
    }

    /**
     * Parses the string as a variable or as a number if it's not a variable.
     *
     * @param packName the package in which the variable is defined
     * @param tmp      the string to parse
     * @throws InstructionParseException If the variable could not be created.
     * @deprecated Use {@link #VariableNumber(QuestPackage, String)} instead.
     */
    @Deprecated
    public VariableNumber(final String packName, final String tmp) throws InstructionParseException {
        this(Config.getPackages().get(packName), tmp);
    }

    /**
     * Creates the VariableNumber using specified number.
     *
     * @param number the number to use
     */
    public VariableNumber(final int number) {
        this.number = number;
        this.variable = null;
    }

    /**
     * Creates the VariableNumber using specified number.
     *
     * @param number the number to use
     */
    public VariableNumber(final double number) {
        this.number = number;
        this.variable = null;
    }

    private Variable parseAsVariable(final QuestPackage pack, final String variable) throws InstructionParseException {
        final Variable parsed;
        try {
            parsed = BetonQuest.createVariable(pack, variable);
        } catch (final InstructionParseException e) {
            throw new InstructionParseException("Could not create variable: " + e.getMessage(), e);
        }
        if (parsed == null) {
            throw new InstructionParseException("Could not create variable");
        }
        return parsed;
    }

    private double parseAsNumber(final String variable) throws InstructionParseException {
        try {
            return Double.parseDouble(variable);
        } catch (final NumberFormatException e) {
            throw new InstructionParseException("Not a number: " + variable, e);
        }
    }

    /**
     * Returns an integer represented by this variable. If it's a double, this
     * method will return the floor of it.
     *
     * @param profile the {@link Profile} of the player for variable resolving
     * @return the integer represented by this variable number
     */
    public int getInt(final Profile profile) {
        return (int) Math.floor(resolveVariable(profile));
    }

    /**
     * Returns a double represented by this variable.
     *
     * @param profile the {@link Profile} of the player for variable resolving
     * @return the double represented by this variable number
     * @throws QuestRuntimeException when the variable does not resolve to a number
     */
    public double getDouble(final Profile profile) throws QuestRuntimeException {
        return resolveVariable(profile);
    }

    private double resolveVariable(final Profile profile) {
        if (variable == null) {
            return number;
        } else if (profile == null) {
            return 0;
        } else {
            final String resolved = variable.getValue(profile);
            double parsed = 0;
            try {
                parsed = Double.parseDouble(resolved);
            } catch (final NumberFormatException e) {
                LOG.debug("Could not parse the variable as a number, it's value is: '" + resolved + "'; returning 0.", e);
            }
            return parsed;
        }
    }

    /**
     * To check if a value will be guarantied not zero or less
     *
     * @return true if a variable is set or the constant value is greater zero,
     * false if no variable is set and the constant value is zero or less
     */
    public boolean explicitLessThanOne() {
        return variable == null && number < 1;
    }

    @Override
    public String toString() {
        return variable == null ? String.valueOf(number) : variable.toString();
    }
}
