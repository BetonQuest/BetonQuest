package org.betonquest.betonquest.menu.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Various utilities.
 */
@SuppressWarnings("PMD.ClassNamingConventions")
public final class Utils {

    private Utils() {
    }

    /**
     * Allows you accessing and modifying private fields.
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
     * Allows accessing and calling private methods.
     *
     * @param clazz       the class which has the method
     * @param name        the name of the method
     * @param paramLength the amount of parameters the method has
     * @return the method for the given class with given name and parameters
     * @throws NoSuchMethodException if the method with the specified name and parameters cant be found
     */
    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    public static Method getMethod(final Class<?> clazz, final String name, final int paramLength) throws NoSuchMethodException {
        Class<?> superClazz = clazz;
        do {
            for (final Method method : superClazz.getDeclaredMethods()) {
                if (method.getName().equals(name) && method.getParameterTypes().length == paramLength) {
                    method.setAccessible(true);
                    return method;
                }
            }
        } while ((superClazz = superClazz.getSuperclass()) != null);
        throw new NoSuchMethodException("Can't find method " + name + " with " + paramLength + " parameters");
    }
}
