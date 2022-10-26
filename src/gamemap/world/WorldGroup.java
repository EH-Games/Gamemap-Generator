package gamemap.world;

import java.util.ArrayList;
import java.util.List;

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
		if(camera.bounds.intersects(bounds)) {
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

	public void render(Camera camera, boolean transparent) {
		if((visibilityFlags & camera.cameraFlag) == 0) return;
		if((layerFlags & camera.layerFlag) == 0) return;
		
		if(transparent) {
			for(WorldItem obj : objects) {
				if(obj.transparent) {
					obj.render(camera, transparent);
				}
			}
		} else {
			for(WorldItem obj : objects) {
				if(obj.transparent) {
					obj.render(camera, transparent);
				}
			}
		}
	}
}
