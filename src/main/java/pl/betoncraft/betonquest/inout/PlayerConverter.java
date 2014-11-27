package pl.betoncraft.betonquest.inout;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.BetonQuest;

@SuppressWarnings("deprecation")
public class PlayerConverter {
	
	private static PlayerConversionType type;
	
	static {
		String uuid = BetonQuest.getInstance().getConfig().getString("uuid");
		BetonQuest.getInstance().getLogger().info(uuid);
		if (uuid != null &&  uuid.equals("true")) {
			type = PlayerConversionType.UUID;
			BetonQuest.getInstance().getLogger().info("Using UUID!");
		} else {
			type = PlayerConversionType.NAME;
			BetonQuest.getInstance().getLogger().info("Using Names!");
		}
	}
	
	public enum PlayerConversionType {
		UUID,
		NAME
	}
	
	public static String getID(Player player) {
		if (type == PlayerConversionType.NAME) {
			return player.getName();
		} else if (type == PlayerConversionType.UUID) {
			return player.getUniqueId().toString();
		} else {
			return null;
		}
	}
	
	public static String getID(String name) {
		if (type == PlayerConversionType.NAME) {
			return name;
		} else if (type == PlayerConversionType.UUID) {
			return Bukkit.getOfflinePlayer(name).getUniqueId().toString();
		} else {
			return null;
		}
	}
	
	public static Player getPlayer(String ID) {
		if (type == PlayerConversionType.NAME) {
			return Bukkit.getPlayer(ID);
		} else if (type == PlayerConversionType.UUID) {
			return Bukkit.getPlayer(UUID.fromString(ID));
		} else {
			return null;
		}
	}

	public static PlayerConversionType getType() {
		return type;
	}

}
