/**
 * The root package of the API containing classes that are used across the API
 * and are not easy to associate with a single subpackage.
 */
@DefaultQualifier(value = NotNull.class, locations = {FIELD, PARAMETER, RETURN})
package org.betonquest.betonquest.api;

import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;

import static org.checkerframework.framework.qual.TypeUseLocation.FIELD;
import static org.checkerframework.framework.qual.TypeUseLocation.PARAMETER;
import static org.checkerframework.framework.qual.TypeUseLocation.RETURN;
