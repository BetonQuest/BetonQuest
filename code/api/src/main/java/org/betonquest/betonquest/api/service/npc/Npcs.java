package org.betonquest.betonquest.api.service.npc;

import org.betonquest.betonquest.api.service.ServiceFeature;

/**
 * Register new npcs with {@link NpcRegistry} or access existing ones with {@link NpcManager}.
 *
 * @since 3.0.0
 */
@SuppressWarnings("PMD.ShortClassName")
public interface Npcs extends ServiceFeature<NpcManager, NpcRegistry> {

}
