package pl.betoncraft.betonquest.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * A method of selectinig blocks (pre 1.13 version)
 *
 * Block selector format:
 *   MATERIAL:data
 *
 * Where:
 *   - MATERIAL - The material type.
 *   - data - optional data type. If left out then it will not be compared against
 *
 * Example:
 *   - LOG - Test against all LOG's
 *   - LOG:1 - Test only against LOG:1
 *
 */
public class BlockSelector {

    private Integer data;
    private Material material;
    private String string;

    public BlockSelector(String string) {
        String materialName;
        this.string = string;

        if (string.contains(":")) {
            String[] parts = string.split(":");
            materialName = parts[0];
            try {
                data = Integer.valueOf(parts[1]);
            } catch (IllegalArgumentException e) {
                Debug.error("Invalid data type: " + parts[1]);
            }
        } else {
            materialName = string;
        }

        material = Material.matchMaterial(materialName);
    }

    public void setData(int data) {
        this.data = data;
    }

    public String asString() {
        return string;
    }

    public boolean isValid() {
        return material != null;
    }


    /**
     * Return true if material matches our selector. State is ignored
     */
    public boolean match(Material material) {
        return (material == this.material);
    }

    /**
     * Return true if block matches our selector
     * @param block Block to test
     * @return boolean True if a match occurred
     */
    @SuppressWarnings("deprecation")
    public boolean match(Block block) {
        if (block.getType() != material) {
            return false;
        }

        return data == null || block.getData() == data;
    }

}
