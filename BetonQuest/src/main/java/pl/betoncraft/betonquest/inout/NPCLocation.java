/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import org.bukkit.Location;

/**
 * 
 * @author Co0sh
 */
public class NPCLocation {
	
	private final double x;
	private final double y;
	private final double z;
	private final String world;

	public NPCLocation(Location location) {
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.world = location.getWorld().getName();
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return the z
	 */
	public double getZ() {
		return z;
	}

	/**
	 * @return the world
	 */
	public String getWorld() {
		return world;
	}
}
