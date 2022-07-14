package pl.betoncraft.betonquest.compatibility.citizens;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.conversation.Conversation;
import pl.betoncraft.betonquest.conversation.InventoryConvIO;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

@SuppressWarnings("PMD.CommentRequired")
public class CitizensInventoryConvIO extends InventoryConvIO {

    public CitizensInventoryConvIO(final Conversation conv, final String playerID) {
        super(conv, playerID);
    }

    @Override
    protected SkullMeta updateSkullMeta(final SkullMeta meta) {
        // this only applied to Citizens NPC conversations
        if (conv instanceof CitizensConversation) {
            if (Bukkit.isPrimaryThread()) {
                throw new IllegalStateException("Must be called async!");
            }

            final CitizensConversation citizensConv = (CitizensConversation) conv;
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
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
                     | InterruptedException | ExecutionException e) {
                LogUtils.getLogger().log(Level.FINE, "Could not resolve a skin Texture!", e);
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
