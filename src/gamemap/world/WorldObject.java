package gamemap.world;

import com.ehgames.util.AABB;
import com.ehgames.util.GL;
import com.ehgames.util.Vec3;

import gamemap.ObjectRenderer;

/** A renderable item within the world. */
public class WorldObject extends WorldItem {
	public static boolean		drawBounds	= false;
	
	public final Vec3			position	= new Vec3();
	public ObjectRenderer		renderer;
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
		if(!camera.testVisibility(bounds)) return false;
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
	public void render(RenderState state) {
		if((visibilityFlags & state.camera.cameraFlag) == 0) return;
		if(layerFlags != state.camera.layerFlag) return;
		
		if(renderer != null) {
			state.object = this;
			renderer.render(state);
		}
		
		//drawBounds = false;
		if(drawBounds) {
			GL gl = state.gl;
			gl.disable(GL.TEXTURE_2D);
			gl.begin(GL.LINE_LOOP);
			gl.color3f(1, 0, 0);
			AABB box = state.object.bounds;
			float z = box.max.z + 1;
			gl.vertex3f(box.min.x, box.min.y, z);
			gl.vertex3f(box.max.x, box.min.y, z);
			gl.vertex3f(box.max.x, box.max.y, z);
			gl.vertex3f(box.min.x, box.max.y, z);
			gl.end();
			gl.begin(GL.POINTS);
			Vec3 pos = state.object.position;
			gl.color3f(0, 1, 0);
			gl.vertex3f(pos.x, pos.y, z);
			gl.color3f(1, 1, 1);
			gl.end();
			gl.enable(GL.TEXTURE_2D);
		}
	}
	
	@Override
	public void destroyResources(GL gl) {
		if(renderer != null) {
			renderer.destroyResources(gl);
		}
	}
}
