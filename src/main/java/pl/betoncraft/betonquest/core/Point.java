/**
 * 
 */
package pl.betoncraft.betonquest.core;

/**
 * 
 * @author Co0sh
 */
public class Point {
	
	private String category;
	private int count;

	public Point(String category, int count) {
		this.category = category;
		this.count = count;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}
	
	/**
	 * Adds points to count
	 * @param add
	 */
	public void addPoints(int add) {
		this.count = this.count + add;
	}
}
