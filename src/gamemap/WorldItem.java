package gamemap;

import com.ehgames.util.Vec3;

/** A renderable item within the world. */
public class WorldItem extends WorldObject {
	public final Vec3	position	= new Vec3();
	public Object		renderer;
	/** Earliest time this object is visible (inclusive) */
	public int			minTime;
	/** Latest time this object is visible (exclusive) */
	public int			maxTime;
	/**
	 * A bitmask indicating in which areas this object is visible.<br>
	 * For example, a game might use area 0(bit 1) as outdoors,
	 * higher bits for specific interiors, and a combination of bits
	 * to represent things visible in multiple of these.
	 */
	public int		areaFlags;
	/**
	 * Layer this object is in. Generally used for alpha ordering.<br>
	 * First all objects either without transparency or with 
	 * nearest-sampled, bitmask transparency are rendered lowest layer to highest layer.<br>
	 * Then all objects with transparency that are either linear sampled or have
	 */ 
	public int		layer;
	
	@Override
	public void recalculateBounds() {}
}
