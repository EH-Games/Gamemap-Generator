package gamemap;

import com.ehgames.util.Vec3;

/** A renderable item within the world. */
public class WorldItem extends WorldObj {
	public final Vec3	position	= new Vec3();
	public Object		renderer;
	/** Earliest time this object is visible (inclusive) */
	public int			minTime		= Integer.MIN_VALUE;
	/** Latest time this object is visible (exclusive) */
	public int			maxTime		= Integer.MAX_VALUE;
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
	 * Then all objects with transparency that are either linear sampled or
	 * are translucent are rendered lowest layer to highest layer.<br>
	 */ 
	public int		layer;
	
	/** The nearest distance this item is visible, squared for comparison */
	public float	minDistSq;
	/** The furthest distance this item is visible, squared for comparison */
	public float	maxDistSq;
	
	public boolean	isTrasparent;
	public boolean	isOpaque;
	
	@Override
	public void recalculateBounds() {}
	
	private boolean isVisible(Camera camera) {
		// area test
		if((camera.areaFlag & areaFlags) == 0) return false;
		// aabb test
		if(!camera.bounds.intersects(bounds)) return false;
		// time test
		if(maxTime < minTime) {
			// visible at midnight
			if(camera.time >= maxTime && camera.time < minTime) return false;
		} else {
			// not visible at midnight
			if(camera.time < minTime || camera.time >= maxTime) return false;
		}
		/*
		// distance test; these might be redundant with aabb test
		float distSq = camera.position.distanceSq(position);
		if(distSq < minDistSq || distSq > maxDistSq) return false;
		return distSq > camera.nearClip && distSq < camera.farClip;
		/*/
		return true;
		//*/
	}
	
	private boolean isVisible2d(Camera camera) {
		// area test
		if((camera.areaFlag & areaFlags) == 0) return false;
		// time test
		if(maxTime < minTime) {
			// visible at midnight
			if(camera.time >= maxTime && camera.time < minTime) return false;
		} else {
			// not visible at midnight
			if(camera.time < minTime || camera.time >= maxTime) return false;
		}
		// aabb test
		return camera.bounds.intersects2d(bounds);
	}

	@Override
	public void testVisibility(Camera camera) {
		if(isVisible(camera)) {
			visibilityFlags |= camera.cameraFlag;
		} else {
			visibilityFlags &= ~camera.cameraFlag;
		}
	}

	@Override
	public void testVisibility2d(Camera camera) {
		if(isVisible2d(camera)) {
			visibilityFlags |= camera.cameraFlag;
		} else {
			visibilityFlags &= ~camera.cameraFlag;
		}
	}
}
