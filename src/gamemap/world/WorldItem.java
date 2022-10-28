package gamemap.world;

import com.ehgames.util.AABB;

public abstract class WorldItem {
	/** The bounds of this object, used for culling */
	public final AABB	bounds		= new AABB();

	/** Friendly name. Currently serves no greater purpose */
	public String		name;

	// internal flags indicating what cameras have deemed this object passes
	// their culling, time of day, and layer tests
	int					visibilityFlags;
	boolean				transparent	= true;
	boolean				opaque		= true;
	/**
	 * A bitmask indicating in which areas this item is visible.<br>
	 * For example, a game might use area 0(bit 1) as outdoors,
	 * higher bits for specific interiors, and a combination of bits
	 * to represent things visible in multiple of these.
	 */
	public int			areaFlags	= 1;
	/**
	 * Bits denoting the layer this item is in. Generally used for alpha ordering.<br>
	 * For groups, this value is tested as a mask. For objects, it is tested exactly.
	 * Therefore, any object's layerFlags should only consist of a single bit.<br> 
	 * First all objects either without transparency or with nearest-sampled,
	 * bitmask transparency are rendered lowest layer to highest layer.<br>
	 * Then all objects with transparency that are either linear sampled or are
	 * translucent are rendered lowest layer to highest layer.<br>
	 */
	public int			layerFlags	= 1;

	WorldItem() {}

	public abstract void recalculateBounds();
	public abstract void testVisibility(Camera camera);
	public abstract void testVisibility2d(Camera camera);
	public abstract void render(RenderState state);
}
