package gamemap.world;

import com.ehgames.util.AABB;

public abstract class WorldItem {
	/** The bounds of this object, used for culling */
	public final AABB	bounds	= new AABB();

	/** Friendly name. Currently serves no greater purpose */
	public String		name;

	// internal flags indicating what cameras have deemed this object passes
	// their culling, time of day, and layer tests
	int					visibilityFlags;
	boolean				transparent;
	boolean				opaque;
	/**
	 * A bitmask indicating in which areas this item is visible.<br>
	 * For example, a game might use area 0(bit 1) as outdoors,
	 * higher bits for specific interiors, and a combination of bits
	 * to represent things visible in multiple of these.
	 */
	public int			areaFlags;
	
	WorldItem() {}

	public abstract void recalculateBounds();
	public abstract void testVisibility(Camera camera);
	public abstract void testVisibility2d(Camera camera);
	public abstract void render(Camera camera, boolean transparent);
}
