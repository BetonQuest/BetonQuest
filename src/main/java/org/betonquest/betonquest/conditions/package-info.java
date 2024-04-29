/**
 * Implementation of core {@link org.betonquest.betonquest.api.Condition Condition}s
 * that come with BetonQuest. This package does not contain conditions that require
 * third-party plugins but only those that are available on a clean server.
 */
@DefaultQualifier(value = NotNull.class, locations = {FIELD, PARAMETER, RETURN})
package org.betonquest.betonquest.conditions;

import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;

import static org.checkerframework.framework.qual.TypeUseLocation.FIELD;
import static org.checkerframework.framework.qual.TypeUseLocation.PARAMETER;
import static org.checkerframework.framework.qual.TypeUseLocation.RETURN;
