package org.betonquest.betonquest.compatibility.citizens;

import lombok.CustomLog;
import net.citizensnpcs.trait.SkinTrait;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.InventoryConvIO;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class CitizensInventoryConvIO extends InventoryConvIO {

    public CitizensInventoryConvIO(final Conversation conv, final String playerID) {
        super(conv, playerID);
    }

    @Override
    protected SkullMeta updateSkullMeta(final SkullMeta meta) {
        // this only applied to Citizens NPC conversations
        if (conv instanceof CitizensConversation) {
            final CitizensConversation citizensConv = (CitizensConversation) conv;
            // read the texture from the NPC
            final SkinTrait skinTrait = citizensConv.getNPC().getTrait(SkinTrait.class);
            final String texture = skinTrait.getTexture();

            if (texture != null) { // Can be null if not cached yet
                try {
                    // prepare reflection magics
                    final Class<?> profileClass = Class.forName("com.mojang.authlib.GameProfile");
                    final Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");
                    final Class<?> propertyMapClass = Class.forName("com.google.common.collect.ForwardingMultimap");

                    // abracadabra
                    final Object profile = profileClass
                            .getConstructor(UUID.class, String.class)
                            .newInstance(UUID.randomUUID(), null);
                    final Object property = propertyClass
                            .getConstructor(String.class, String.class, String.class)
                            .newInstance("textures", texture, skinTrait.getSignature());
                    final Object propertyMap = profileClass
                            .getMethod("getProperties")
                            .invoke(profile);
                    propertyMapClass
                            .getMethod("put", Object.class, Object.class)
                            .invoke(propertyMap, "textures", property);
                    final Field field = meta.getClass().getDeclaredField("profile");
                    field.setAccessible(true);
                    field.set(meta, profile);
                    return meta;
                } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
                        | InstantiationException | InvocationTargetException | NoSuchMethodException
                        | ClassNotFoundException e) {
                    LOG.debug("Could not resolve a skin Texture!", e);
                }
            }
        }
        return super.updateSkullMeta(meta);
    }

    public static class CitizensCombined extends CitizensInventoryConvIO {

        public CitizensCombined(final Conversation conv, final String playerID) {
            super(conv, playerID);
            super.printMessages = true;
        }
    }

}
