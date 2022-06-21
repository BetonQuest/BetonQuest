package org.betonquest.betonquest.menu.utils;

import org.bukkit.ChatColor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Various utilities.
 */
@SuppressWarnings("PMD.ClassNamingConventions")
public final class Utils {

    private Utils() {
    }

    /**
     * Translate alternate color codes of a string list
     *
     * @param colorCodeChar the char which should be replaced with <b>§</b>
     * @param lines         the string list to translate
     * @return <b>lines</b> with <b>colorCodeChar</b> replaced by <b>§</b>
     */
    public static List<String> translateAlternateColorcodes(final char colorCodeChar, final List<String> lines) {
        final List<String> translated = new ArrayList<>(lines.size());
        for (final String line : lines) {
            translated.add(ChatColor.translateAlternateColorCodes(colorCodeChar, line));
        }
        return translated;
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
     * @param string name of a class which should be loaded
     * @return if the class exists
     */
    public static boolean doesClassExist(final String string) {
        try {
            Class.forName(string);
            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }
}
