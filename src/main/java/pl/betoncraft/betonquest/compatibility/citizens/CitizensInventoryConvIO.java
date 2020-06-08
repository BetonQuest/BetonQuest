/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.compatibility.citizens;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.betoncraft.betonquest.conversation.Conversation;
import pl.betoncraft.betonquest.conversation.InventoryConvIO;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class CitizensInventoryConvIO extends InventoryConvIO {

    public CitizensInventoryConvIO(Conversation conv, String playerID) {
        super(conv, playerID);
    }

    @Override
    public void display() {
        super.display();

        // Make sure we have an inventory
        if (inv == null) {
            return;
        }

        // this only applied to Citizens NPC conversations
        if (conv instanceof CitizensConversation) {
            CitizensConversation citizensConv = (CitizensConversation) conv;

            // get the head meta
            ItemStack head = inv.getContents()[0];
            ItemMeta npcMeta = head.getItemMeta();

            // read the texture from the NPC
            String texture = citizensConv.getNPC().data().get(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA);

            if (texture != null) { // Can be null if not cached yet

                try {
                    // prepare reflection magics
                    Class<?> profileClass = Class.forName("com.mojang.authlib.GameProfile");
                    Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");
                    Class<?> propertyMapClass = Class.forName("com.google.common.collect.ForwardingMultimap");

                    // abracadabra
                    Object profile = profileClass
                            .getConstructor(UUID.class, String.class)
                            .newInstance(UUID.randomUUID(), null);
                    Object property = propertyClass
                            .getConstructor(String.class, String.class, String.class)
                            .newInstance("textures", texture, citizensConv.getNPC().data().get(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA));
                    Object propertyMap = profileClass
                            .getMethod("getProperties")
                            .invoke(profile);
                    propertyMapClass
                            .getMethod("put", Object.class, Object.class)
                            .invoke(propertyMap, "textures", property);
                    Field field = npcMeta.getClass().getDeclaredField("profile");
                    field.setAccessible(true);
                    field.set(npcMeta, profile);

                    // set the npcMeta back to the head and set the head back to the inventory
                    // in case it doesn't happen automatically
                    head.setItemMeta(npcMeta);
                    inv.getContents()[0] = head;

                } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
                        | InstantiationException | InvocationTargetException | NoSuchMethodException
                        | ClassNotFoundException e) {
                    LogUtils.logThrowableIgnore(e);
                }
            }
        }
    }

    public static class CitizensCombined extends CitizensInventoryConvIO {

        public CitizensCombined(Conversation conv, String playerID) {
            super(conv, playerID);
            super.printMessages = true;
        }
    }

}
