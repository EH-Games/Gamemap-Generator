package gamemap.world;

import java.util.ArrayList;
import java.util.List;

/** A list of renderable objects within the world */
public class WorldGroup extends WorldObj {
	public List<WorldObj> objects = new ArrayList<>();
	
	/** Recalculate the bounds of this group from that of all child objects */
	@Override
	public void recalculateBounds() {
		if(objects.isEmpty()) {
			bounds.clear();
		} else {
			bounds.prepForBuild();
			for(WorldObj obj : objects) {
				obj.recalculateBounds();
				bounds.add(obj.bounds);
			}
		}
	}

	@Override
	public void testVisibility(Camera camera) {
		if(camera.bounds.intersects(bounds)) {
			visibilityFlags |= camera.cameraFlag;
			for(WorldObj obj : objects) {
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
			for(WorldObj obj : objects) {
				obj.testVisibility2d(camera);
			}
		} else {
			visibilityFlags &= ~camera.cameraFlag;
		}
	}
}
