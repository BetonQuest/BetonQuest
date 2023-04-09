package org.betonquest.betonquest.compatibility.citizens;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.citizensnpcs.trait.SkinTrait;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.InventoryConvIO;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("PMD.CommentRequired")
public class CitizensInventoryConvIO extends InventoryConvIO {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(CitizensInventoryConvIO.class);

    public CitizensInventoryConvIO(final Conversation conv, final OnlineProfile onlineProfile) {
        super(conv, onlineProfile);
    }

    @Override
    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    protected SkullMeta updateSkullMeta(final SkullMeta meta) {
        // this only applied to Citizens NPC conversations
        if (conv instanceof final CitizensConversation citizensConv) {
            if (Bukkit.isPrimaryThread()) {
                throw new IllegalStateException("Must be called async!");
            }

            try {
                final SkinTrait skinTrait = Bukkit.getScheduler().callSyncMethod(BetonQuest.getInstance(), () -> citizensConv.getNPC().getOrAddTrait(SkinTrait.class)).get();
                final String texture = skinTrait.getTexture();

                if (texture != null) {
                    final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
                    final Property property = new Property("textures", texture, skinTrait.getSignature());
                    gameProfile.getProperties().put("textures", property);

                    final Field field = meta.getClass().getDeclaredField("profile");
                    field.setAccessible(true);
                    field.set(meta, gameProfile);
                    return meta;
                }
            } catch (final NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
                           | InterruptedException | ExecutionException e) {
                LOG.debug(citizensConv.getPackage(), "Could not resolve a skin Texture!", e);
            }
        }
        return super.updateSkullMeta(meta);
    }

    public static class CitizensCombined extends CitizensInventoryConvIO {

        public CitizensCombined(final Conversation conv, final OnlineProfile onlineProfile) {
            super(conv, onlineProfile);
            super.printMessages = true;
        }
    }

}
