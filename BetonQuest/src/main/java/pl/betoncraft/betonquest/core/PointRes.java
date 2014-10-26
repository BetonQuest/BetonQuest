/**
 * 
 */
package pl.betoncraft.betonquest.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Co0sh
 */
public class PointRes {
	
	private List<Point> points = new ArrayList<Point>();
	
	private int iterator;
	
	public PointRes(ResultSet res) {
		try {
			while (res.next()) {
				points.add(new Point(res.getString("category"), res.getInt("count")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		iterator = -1;
	}
	
	/**
	 * Moves cursor to next place and returns true if there is anything there
	 * @return
	 */
	public boolean next() {
		iterator++;
		if (iterator < points.size()) {
			return true;
		}
		return false;
	}
	
	/**
	 * returns string (tag) on this row
	 * @return
	 */
	public Point getPoint() {
		return points.get(iterator);
	}
}
