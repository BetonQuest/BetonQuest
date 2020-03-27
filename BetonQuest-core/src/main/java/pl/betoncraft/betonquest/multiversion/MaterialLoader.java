package pl.betoncraft.betonquest.multiversion;

import org.bukkit.Material;

/**
 * This class load material, that depending on the minecraft version
 */
public final class MaterialLoader {

    private MaterialLoader() {}

    /**
     * Convert a string into a material
     * 
     * @param materialString
     *            The name of the material
     * @return The material
     */
    public static Material getMaterial(final String materialString) {
        Material material = Material.matchMaterial(materialString);
        if (material == null) {
            material = Material.matchMaterial(materialString, true);
        }
        return material;
    }
}