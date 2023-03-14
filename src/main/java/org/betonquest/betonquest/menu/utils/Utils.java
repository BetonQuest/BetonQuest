package org.betonquest.betonquest.menu.utils;

import lombok.CustomLog;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.GlobalVariableID;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Various utilities.
 */
@SuppressWarnings("PMD.ClassNamingConventions")
@CustomLog
public final class Utils {

    private Utils() {
    }

    /**
     * Allows you accessing and modifying private fields
     *
     * @param clazz the class which has the field
     * @param name  the field you want to access
     * @return the field for the given class with the given name
     * @throws NoSuchFieldException if the field with the specified name cant be found
     */
    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    public static Field getField(final Class<?> clazz, final String name) throws NoSuchFieldException {
        Class<?> superClazz = clazz;
        do {
            for (final Field field : superClazz.getDeclaredFields()) {
                if (field.getName().equals(name)) {
                    field.setAccessible(true);
                    return field;
                }
            }
        } while ((superClazz = superClazz.getSuperclass()) != null);
        throw new NoSuchFieldException("Can't find field " + name);
    }

    /**
     * Allows accessing and calling private methods
     *
     * @param clazz       the class which has the method
     * @param name        the name of the method
     * @param paramlength the amount of parameters the method has
     * @return the method for the given class with given name and parameters
     * @throws NoSuchMethodException if the method with the specified name and parameters cant be found
     */
    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    public static Method getMethod(final Class<?> clazz, final String name, final int paramlength) throws NoSuchMethodException {
        Class<?> superClazz = clazz;
        do {
            for (final Method method : superClazz.getDeclaredMethods()) {
                if (method.getName().equals(name) && method.getParameterTypes().length == paramlength) {
                    method.setAccessible(true);
                    return method;
                }
            }
        } while ((superClazz = superClazz.getSuperclass()) != null);
        throw new NoSuchMethodException("Can't find method " + name + " with " + paramlength + " parameters");
    }

    /**
     * This Method resolves global variables in the menu section
     * No need for other variable resolving here
     *
     * @param input the string to parse
     * @param pack  the context of the global variables
     * @return resolved String
     */
    public static String resolveGlobalVariables(final String input, final QuestPackage pack) {
        if (input == null) {
            return null;
        }
        String variableInput = input;

        final Pattern globalVariableRegex = Pattern.compile("\\$([^ $\\s]+)\\$");
        while (true) {
            final Matcher matcher = globalVariableRegex.matcher(variableInput);
            if (!matcher.find()) {
                break;
            }
            final String varName = matcher.group(1);
            final String varVal;
            if ("this".equals(varName)) {
                varVal = pack.getQuestPath();
            } else {
                final GlobalVariableID variableID;
                try {
                    variableID = new GlobalVariableID(pack, varName);
                } catch (final ObjectNotFoundException e) {
                    LOG.warn(pack, e.getMessage(), e);
                    return variableInput;
                }
                final String varRaw = variableID.getPackage().getConfig().getString("variables." + variableID.getBaseID());
                if (varRaw == null) {
                    LOG.warn(pack, String.format("Variable %s not defined in package %s", variableID.getBaseID(), variableID.getPackage().getQuestPath()));
                    return variableInput;
                }
                varVal = resolve(varRaw, variableID.getPackage());
            }
            variableInput = variableInput.replace("$" + varName + "$", varVal);
        }
        return variableInput;
    }

    /**
     * Inner loop of {{@link #resolveGlobalVariables(String, QuestPackage)}}
     */
    public static String resolve(final String input, final QuestPackage pack) {
        if (input == null) {
            return null;
        }
        final Pattern globalVariableRegex = Pattern.compile("\\$([^ $\\s]+)\\$");
        final Matcher matcher = globalVariableRegex.matcher(input);
        final StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            final String varName = matcher.group(1);
            final String varVal;
            if ("this".equals(varName)) {
                varVal = pack.getQuestPath();
            } else {
                try {
                    final GlobalVariableID variableID = new GlobalVariableID(pack, varName);
                    varVal = "$" + variableID.getPackage().getQuestPath() + "." + variableID.getBaseID() + "$";
                } catch (final ObjectNotFoundException e) {
                    LOG.warn(pack, e.getMessage(), e);
                    return input;
                }
            }
            matcher.appendReplacement(builder, Matcher.quoteReplacement(varVal));
        }
        matcher.appendTail(builder);
        return builder.toString();
    }
}
