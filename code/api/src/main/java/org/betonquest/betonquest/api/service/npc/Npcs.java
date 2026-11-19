package org.betonquest.betonquest.api.service.npc;

import org.betonquest.betonquest.api.service.ServiceFeature;

/**
 * Provides access to the NPC service.
 * <p>
 * Use the {@link NpcRegistry} to register new NPC types and the {@link NpcManager} to manage them.
 *
 * @since 3.0.0
 */
@SuppressWarnings("PMD.ShortClassName")
public interface Npcs extends ServiceFeature<NpcManager, NpcRegistry> {

}
