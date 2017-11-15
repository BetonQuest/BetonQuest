/**
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
package pl.betoncraft.betonquest.events;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.metadata.FixedMetadataValue;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.Instruction.Item;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.ItemID;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Spawns mobs at given location
 * 
 * @author Jakub Sapalski
 */
public class SpawnMobEvent extends QuestEvent {

	private LocationData loc;
	private EntityType type;
	private VariableNumber amount;
	private String name;
	private String marked;
	
	private QuestItem helmet;
	private QuestItem chestplate;
	private QuestItem leggings;
	private QuestItem boots;
	private QuestItem mainHand;
	private QuestItem offHand;
	private Item[] drops;

	public SpawnMobEvent(Instruction instruction) throws InstructionParseException {
		super(instruction);
		staticness = true;
		persistent = true;
		loc = instruction.getLocation();
		String entity = instruction.next();
		try {
			type = EntityType.valueOf(entity.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new InstructionParseException("Entity type '" + entity + "' does not exist");
		}
		amount = instruction.getVarNum();
		name = instruction.getOptional("name");
		if (name != null) {
			name = name.replace('_', ' ');
		}
		marked = instruction.getOptional("marked");
		if (marked != null) {
			marked = Utils.addPackage(instruction.getPackage(), marked);
		}
		ItemID item;
		item = instruction.getItem(instruction.getOptional("h"));
		helmet = item == null ? null : new QuestItem(item);
		item = instruction.getItem(instruction.getOptional("c"));
		chestplate = item == null ? null : new QuestItem(item);
		item = instruction.getItem(instruction.getOptional("l"));
		leggings = item == null ? null : new QuestItem(item);
		item = instruction.getItem(instruction.getOptional("b"));
		boots = item == null ? null : new QuestItem(item);
		item = instruction.getItem(instruction.getOptional("m"));
		mainHand = item == null ? null : new QuestItem(item);
		item = instruction.getItem(instruction.getOptional("o"));
		offHand = item == null ? null : new QuestItem(item);
		drops = instruction.getItemList(instruction.getOptional("drops"));
	}

	@SuppressWarnings("deprecation")
    @Override
	public void run(String playerID) throws QuestRuntimeException {
		Location location = loc.getLocation(playerID);
		int a = amount.getInt(playerID);
		for (int i = 0; i < a; i++) {
			Entity entity = location.getWorld().spawnEntity(location, type);
			if (entity instanceof LivingEntity) {
				LivingEntity living = (LivingEntity) entity;
				EntityEquipment eq = living.getEquipment();
				eq.setHelmet(helmet == null ? null : helmet.generate(1));
				eq.setHelmetDropChance(0);
				eq.setChestplate(chestplate == null ? null : chestplate.generate(1));
				eq.setChestplateDropChance(0);
				eq.setLeggings(leggings == null ? null : leggings.generate(1));
				eq.setLeggingsDropChance(0);
				eq.setBoots(boots == null ? null : boots.generate(1));
				eq.setBootsDropChance(0);
				try {
	                eq.setItemInMainHand(mainHand == null ? null : mainHand.generate(1));
	                eq.setItemInMainHandDropChance(0);
	                eq.setItemInOffHand(offHand == null ? null : offHand.generate(1));
	                eq.setItemInOffHandDropChance(0);
				} catch (LinkageError e) {
				    eq.setItemInHand(mainHand.generate(1));
				    eq.setItemInHandDropChance(0);
				}
			}
			int j = 0;
			for (Item item : drops) {
				entity.setMetadata("betonquest-drops-" + j,
						new FixedMetadataValue(BetonQuest.getInstance(), item.getID().getFullID() + ":"
								+ item.getAmount().getInt(playerID)));
				j++;
			}
			if (name != null && entity instanceof LivingEntity) {
				LivingEntity livingEntity = (LivingEntity) entity;
				livingEntity.setCustomName(name);
			}
			if (marked != null) {
				entity.setMetadata("betonquest-marked", new FixedMetadataValue(BetonQuest.getInstance(), marked));
			}
		}
	}
}
