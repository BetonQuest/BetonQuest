/**
 * Implementation of core {@link org.betonquest.betonquest.api.quest.event.Event Event}s
 * that come with BetonQuest. This package does not contain events that require
 * third-party plugins but only those that are available on a clean server.
 * This package contains general implementations of different concepts
 * and adapters between various related interfaces. Sub-packages contain
 * concrete implementation for different events.
 */
@DefaultQualifier(value = NotNull.class, locations = {FIELD, PARAMETER, RETURN})
package org.betonquest.betonquest.quest.event;

import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;

import static org.checkerframework.framework.qual.TypeUseLocation.FIELD;
import static org.checkerframework.framework.qual.TypeUseLocation.PARAMETER;
import static org.checkerframework.framework.qual.TypeUseLocation.RETURN;
