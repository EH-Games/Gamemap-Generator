package gamemap.types;

/**
 * Filtering to be applied to images.<br>
 * Each {@link MapView} has its own filter,
 * and each sprite sheet of {@link MapInfo#icons} has one.<br>
 * Linear will result in smoothing between pixels, while nearest will result in a hard cutoff.
 */
public enum ImageFilter {
	// breaking java naming conventions so it ends up as
	// lowercase in json without employing additional code 
	linear, nearest;
}
