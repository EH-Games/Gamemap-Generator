package gamemap.world;

import java.util.HashMap;
import java.util.Map;

import com.ehgames.util.Vec3;

import gamemap.Model;
import gamemap.Texture;

/** A renderable item within the world. */
public class WorldObject extends WorldItem {
	public final Vec3			position	= new Vec3();
	public Model				model;
	public Map<String, Texture>	textures	= new HashMap<>();
	/** Earliest time this object is visible (inclusive) */
	public int					minTime		= Integer.MIN_VALUE;
	/** Latest time this object is visible (exclusive) */
	public int					maxTime		= Integer.MAX_VALUE;

	/** The nearest distance this item is visible, squared for comparison */
	public float				minDistSq;
	/** The furthest distance this item is visible, squared for comparison */
	public float				maxDistSq;

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
	
	@Override
	public void render(Camera camera, boolean transparent) {
		if((visibilityFlags & camera.cameraFlag) == 0) return;
		if(layerFlags != camera.layerFlag) return;
		
		if(model != null) {
			model.render(transparent, textures);
		}
	}
}
