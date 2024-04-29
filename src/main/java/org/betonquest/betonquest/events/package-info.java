/**
 * Old implementation of core {@link org.betonquest.betonquest.api.QuestEvent QuestEvent}s
 * that come with BetonQuest. This package does not contain conditions that require
 * third-party plugins but only those that are available on a clean server.
 *
 * @deprecated the new events are on the {@link org.betonquest.betonquest.quest.event} package
 */
@Deprecated
@DefaultQualifier(value = NotNull.class, locations = {FIELD, PARAMETER, RETURN})
package org.betonquest.betonquest.events;

import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;

import static org.checkerframework.framework.qual.TypeUseLocation.FIELD;
import static org.checkerframework.framework.qual.TypeUseLocation.PARAMETER;
import static org.checkerframework.framework.qual.TypeUseLocation.RETURN;
