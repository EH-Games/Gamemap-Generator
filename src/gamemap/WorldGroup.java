package gamemap;

import java.util.ArrayList;
import java.util.List;

/** A list of renderable objects within the world */
public class WorldGroup extends WorldObject {
	public List<WorldObject> objects = new ArrayList<>();
	
	/** Recalculate the bounds of this group from that of all child objects */
	@Override
	public void recalculateBounds() {
		if(objects.isEmpty()) {
			bounds.clear();
		} else {
			bounds.prepForBuild();
			for(WorldObject obj : objects) {
				obj.recalculateBounds();
				bounds.add(obj.bounds);
			}
			// TODO set bounds from first item
			// TODO update bounds
		}
	}
}
