package gamemap.world;

import java.util.ArrayList;
import java.util.List;

import com.ehgames.util.GL;

/** A list of renderable objects within the world */
public final class WorldGroup extends WorldItem {
	public List<WorldItem> objects = new ArrayList<>();
	
	/** Recalculate the bounds of this group from that of all child objects */
	@Override
	public void recalculateBounds() {
		if(objects.isEmpty()) {
			bounds.clear();
		} else {
			bounds.prepForBuild();
			for(WorldItem obj : objects) {
				obj.recalculateBounds();
				bounds.add(obj.bounds);
			}
		}
	}
	
	public void recalculateFlags() {
		areaFlags = 0;
		transparent = false;
		opaque = false;
		for(WorldItem obj : objects) {
			if(obj instanceof WorldGroup) {
				((WorldGroup) obj).recalculateFlags();
			}
			areaFlags |= obj.areaFlags;
			transparent |= obj.transparent;
			opaque |= obj.opaque;
		}
	}

	@Override
	public void testVisibility(Camera camera) {
		if(camera.testVisibility(bounds)) {
			visibilityFlags |= camera.cameraFlag;
			for(WorldItem obj : objects) {
				obj.testVisibility(camera);
			}
		} else {
			visibilityFlags &= ~camera.cameraFlag;
		}
	}
	
	@Override
	public void testVisibility2d(Camera camera) {
		if(camera.bounds.intersects2d(bounds)) {
			visibilityFlags |= camera.cameraFlag;
			for(WorldItem obj : objects) {
				obj.testVisibility2d(camera);
			}
		} else {
			visibilityFlags &= ~camera.cameraFlag;
		}
	}

	@Override
	public void render(RenderState state) {
		if((visibilityFlags & state.camera.cameraFlag) == 0) return;
		if((layerFlags & state.camera.layerFlag) == 0) return;
		
		if(transparent) {
			for(WorldItem obj : objects) {
				if(obj.transparent) {
					obj.render(state);
				}
			}
		} else {
			for(WorldItem obj : objects) {
				if(obj.transparent) {
					obj.render(state);
				}
			}
		}
	}
	
	@Override
	public void destroyResources(GL gl) {
		for(WorldItem obj : objects) {
			obj.destroyResources(gl);
		}
	}
}
