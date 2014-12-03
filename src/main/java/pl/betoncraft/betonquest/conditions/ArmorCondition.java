/**
 * 
 */
package pl.betoncraft.betonquest.conditions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.core.Condition;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * Instruction string: type:leggings material:iron
 * @author Co0sh
 */
public class ArmorCondition extends Condition {

	private Material armor;
	private String type;
	private String material;
	private Map<Enchantment,Integer> enchants = new HashMap<Enchantment,Integer>();
	
	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public ArmorCondition(String playerID, String instructions) {
		super(playerID, instructions);
		for (String part : instructions.split(" ")) {
			if (part.contains("type:")) {
				type = part.substring(5).toUpperCase();
			} else if (part.contains("material:")) {
				material = part.substring(9).toUpperCase();
			} else if (part.contains("enchants:")) {
				for (String enchant : part.substring(9).split(",")) {
					enchants.put(Enchantment.getByName(enchant.split(":")[0]), Integer.decode(enchant.split(":")[1]));
				}
			}
		}
		if (type != null && material != null) {
			armor = Material.matchMaterial(material + "_" + type);
		}
	}

	@Override
	public boolean isMet() {
		for (ItemStack item : PlayerConverter.getPlayer(playerID).getEquipment().getArmorContents()) {
			if (item.getType().equals(armor)) {
				if (enchants != null) {
					for (Enchantment enchant : enchants.keySet()) {
						if (item.getEnchantments().get(enchant) == null) {
							return false;
						}
						if (item.getEnchantments().get(enchant) < enchants.get(enchant)) {
							return false;
						}
					}
				}
				return true;
			}
		}
		return false;
	}

}
