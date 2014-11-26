/**
 * 
 */
package pl.betoncraft.betonquest.inout;

import org.bukkit.Location;

/**
 * 
 * @author Co0sh
 */
public class UnifiedLocation {
	
	private final double x;
	private final double y;
	private final double z;
	private final String world;

	public UnifiedLocation(Location location) {
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.world = location.getWorld().getName();
	}
	
	public UnifiedLocation(double x, double y, double z, String world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
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
